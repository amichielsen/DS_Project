package be.uantwerpen.node.agents;

import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.cache.IpTableCache;
import be.uantwerpen.node.fileSystem.FileParameters;
import be.uantwerpen.node.fileSystem.FileSystem;
import be.uantwerpen.node.lifeCycle.Failure;
import be.uantwerpen.node.lifeCycle.running.services.FileSender;
import be.uantwerpen.node.lifeCycle.running.services.ReplicationService;
import be.uantwerpen.node.utils.Hash;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/***
 * SyncAgent: Agent responsible for synchronizing the whole system
 * 1. Updates list of all files in the system
 * 2. Lock files if required
 * 3. Unlock files if required
 * 4. Checks whether Next Node should be the owner of the file
 */
public class SyncAgent extends Agent {

    private HashMap<String, FileParameters> agentList = new HashMap<>();
    private HashMap<String, FileParameters> origList = new HashMap<>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();

    private static SyncAgent instance;

    static {
            instance = new SyncAgent();
    }

    public static SyncAgent getInstance(){
        return instance;
    }
    private SyncAgent() {
    }

    @Override
    public void run() {
        if(NodeParameters.DEBUG) System.out.println("[S-A] Sync Agent started on this node");
        File dir = new File(NodeParameters.replicaFolder);
        while (true) {
            writeLock.lock();
            FileSystem.fs.putAll(agentList); //Update local list according to agent

            File[] directoryListing = dir.listFiles();
            if (directoryListing == null) return;

            for (File child : directoryListing) {
                //FileSystem.addLocal(child.getName(), 0);
                if (!agentList.containsKey(child.getName())) //Update agentList
                    agentList.put(child.getName(), FileSystem.getFileParameters(child.getName()));

                if (agentList.get(child.getName()).isLocked() && agentList.get(child.getName()).getLockedOnNode() == NodeParameters.id) {
                    FileSystem.getFileParameters(child.getName()).unLock();
                }

                //Checks whether another Node should own the file
                if (NodeParameters.nextID < Hash.generateHash(child.getName())) {
                    try {

                        String ipNext = IpTableCache.getInstance().getIp(NodeParameters.nextID).getHostAddress();
                        FileSender.sendFile(child.getPath(), ipNext, FileSystem.fs.get(child.getName()).getLocalOnNode(), "Owner");
                        var client = HttpClient.newHttpClient();
                        var request2 = HttpRequest.newBuilder(
                                        URI.create("http://" + ipNext + ":8080/api/changeOwner?filename=" + child.getName()))
                                .build();
                        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
                        FileSystem.getFileParameters(child.getName()).setReplicatedOnNode(NodeParameters.nextID);
                        child.delete();


                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                }

            }

            //Lock files on agent
            while (NodeParameters.lockRequest.size() > 0) {
                String lockedFile = NodeParameters.lockRequest.poll();
                agentList.get(lockedFile).lock(NodeParameters.id);
            }

            while (NodeParameters.removeLocks.size() > 0) {
                String lockedFile = NodeParameters.removeLocks.poll();
                agentList.get(lockedFile).unLock();
            }
            if(this.origList != this.agentList && !Objects.equals(NodeParameters.id, NodeParameters.nextID)) {
                for (int i = 0; i < 6; i++) {
                    try { //Pass agentList to next one
                        HttpRequest request = HttpRequest.newBuilder(
                                        URI.create("http://" + IpTableCache.getInstance().getIp(NodeParameters.nextID).getHostAddress() + ":8080/api/syncagent"))
                                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(this.agentList)))
                                .build();
                        if(NodeParameters.DEBUG) System.out.println("[S-A] request: " + request);
                        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                        //if (NodeParameters.DEBUG) System.out.println("[S-A] Done. Moving on");
                        if (response.statusCode() != 200) if (NodeParameters.DEBUG)
                            System.out.println("[S-A] Next node was not able to process Agent. Agent died here. RIP");
                        break;
                    } catch (IOException | InterruptedException e) {
                        if (i < 4) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        } else {
                            System.out.println("[" + getName() + "] [Error] connection error with next node (likely offline)");
                            throw new RuntimeException(e);
                            //throw new RuntimeException(e);
                        }
                    }
                }
            }
            writeLock.unlock();
        }
    }

    public HashMap<String, FileParameters> getAgentList() {
        return agentList;
    }

    public void setAgentList(HashMap<String, FileParameters> agentList) {
        writeLock.lock();
        this.origList = this.agentList;
        if(NodeParameters.DEBUG) System.out.println("Old Agent's list: " + this.agentList);
        this.agentList = agentList;
        if(NodeParameters.DEBUG) System.out.println("New Agent's list: " + this.agentList);
        writeLock.unlock();
    }
}
