package be.uantwerpen.node.agents;

import be.uantwerpen.node.utils.NodeParameters;
import be.uantwerpen.node.utils.cache.IpTableCache;
import be.uantwerpen.node.utils.fileSystem.FileParameters;
import be.uantwerpen.node.utils.fileSystem.FileSystem;
import be.uantwerpen.node.lifeCycle.running.services.FileSender;
import be.uantwerpen.node.utils.Hash;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/***
 * SyncAgent: Agent responsible for synchronizing the whole system
 * 1. Updates list of all files in the system
 * 2. Lock files if required
 * 3. Unlock files if required
 * 4. Checks whether Next Node should be the owner of the file
 */
public class SyncAgent extends Agent {

    private HashMap<String, FileParameters> agentList = new HashMap<>();

    private HashMap<Integer, String> deletionList = new HashMap<>();


    public SyncAgent() {
    }

    @Override
    public void run() {

        while (NodeParameters.upForDeletion.size() > 0) {
            String deletedFile = NodeParameters.upForDeletion.poll();
            if(NodeParameters.DEBUG) System.out.println("[S-A] Deletion of: " +deletedFile);
            agentList.remove(deletedFile);
            FileSystem.removeFile(deletedFile);
            deletionList.put(NodeParameters.id, deletedFile);
        }

        ArrayList<File> deletions = new ArrayList<>();

        //if(NodeParameters.DEBUG) System.out.println("[S-A] Sync Agent started on this node");

        for (Map.Entry<Integer, String> entry : deletionList.entrySet()) {
            if (Objects.equals(entry.getKey(), NodeParameters.id)) {
                deletionList.remove(entry.getKey());
            }
            agentList.remove(entry.getValue());
            FileSystem.removeFile(entry.getValue());
        }
        File dir = new File(NodeParameters.replicaFolder);

            File[] directoryListing = dir.listFiles();
            if (directoryListing == null) return;

            //Only update this list with info of other nodes, this nodes knows the latest of its own files
            for(Map.Entry<String, FileParameters> entry: agentList.entrySet()){
                if(!FileSystem.getReplicatedFiles(true).containsKey(entry.getKey())){
                    FileSystem.fs.put(entry.getKey(), entry.getValue());
                }

            }

            for (File child : directoryListing) {
                //FileSystem.addLocal(child.getName(), 0);
                agentList.put(child.getName(), FileSystem.getFileParameters(child.getName()));

                if (agentList.get(child.getName()) != null) {
                    if (agentList.get(child.getName()).isLocked() && agentList.get(child.getName()).getLockedOnNode() == NodeParameters.id) {
                        FileSystem.getFileParameters(child.getName()).unLock();
                    }
                }

                //If file is replicated here and local as well, should go to previous
                if (!(FileSystem.fs.get(child.getName()) == null)) {
                    if (FileSystem.fs.get(child.getName()).getLocalOnNode() == NodeParameters.id && !NodeParameters.id.equals(NodeParameters.previousID)) {
                        for (int i = 0; i < 10; i++) {
                            try {
                                FileSender.sendFile((child.getPath()), IpTableCache.getInstance().getIp(NodeParameters.previousID).getHostAddress(), FileSystem.getFileParameters(child.getName()).getLocalOnNode(), "Owner");
                                HttpRequest request2 = HttpRequest.newBuilder(
                                                URI.create("http:/" + IpTableCache.getInstance().getIp(NodeParameters.previousID) + ":8080/api/changeOwner"))
                                        .PUT(HttpRequest.BodyPublishers.ofString(child.getName()))
                                        .build();
                                if (NodeParameters.DEBUG)
                                    System.out.println("[S-A] Request change owner: " + request2);
                                HttpResponse<String> response2 = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());
                                FileSystem.getFileParameters(child.getName()).setReplicatedOnNode(NodeParameters.previousID);
                                if (child.delete())
                                    if (NodeParameters.DEBUG)
                                        System.out.println("[S-A] Successful deletion of " + child.getName());
                                break;
                            } catch (IOException | InterruptedException e) {
                                if (i < 8) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                } else {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        continue;
                    }
                }

                //Checks whether another Node should own the file
                if (!(FileSystem.fs.get(child.getName()) == null)) {
                    if (isForNext(child.getName())) {
                        if ((FileSystem.fs.get(child.getName()).getLocalOnNode() != NodeParameters.nextID)) {
                            if (NodeParameters.DEBUG)
                                System.out.println("[S-A] File: " + child.getName() + " should be for: " + NodeParameters.nextID);
                            for (int i = 0; i < 10; i++) {
                                try {
                                    String ipNext = IpTableCache.getInstance().getIp(NodeParameters.nextID).getHostAddress();
                                    FileSender.sendFile(child.getPath(), ipNext, FileSystem.fs.get(child.getName()).getLocalOnNode(), "Owner");
                                    HttpClient client = HttpClient.newHttpClient();
                                    HttpRequest request2 = HttpRequest.newBuilder(
                                                    URI.create("http://" + ipNext + ":8080/api/changeOwner"))
                                            .PUT(HttpRequest.BodyPublishers.ofString(child.getName()))
                                            .build();
                                    //if (NodeParameters.DEBUG) System.out.println("[S-A] request: " + request2);
                                    HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
                                    FileSystem.getFileParameters(child.getName()).setReplicatedOnNode(NodeParameters.nextID);
                                    if (child.delete())
                                        if (NodeParameters.DEBUG)
                                            System.out.println("[S-A] Successful deletion of " + child.getName());
                                    this.printList();
                                    break;
                                } catch (IOException | InterruptedException e) {
                                    if (!child.exists()) continue;
                                    if (i < 8) {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    } else {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        } else if (!Objects.equals(NodeParameters.previousID, NodeParameters.nextID)) {
                            if(NodeParameters.DEBUG) System.out.println("[S-A] Query for file in IPCache: " + child.getName());
                            int id = IpTableCache.getInstance().findNodeFromFile(Hash.generateHash(child.getName()));
                            String ip = IpTableCache.getInstance().getIp(id).getHostAddress();
                            if (NodeParameters.nextID != id &&  NodeParameters.id != id) {
                                if (NodeParameters.DEBUG)
                                    System.out.println("[S-A] File: " + child.getName() + " should be for (via cache): " + id + " with ip: " + ip);
                                for (int i = 0; i < 10; i++) {
                                    try {
                                        FileSender.sendFile(child.getPath(), ip, FileSystem.fs.get(child.getName()).getLocalOnNode(), "Owner");
                                        HttpClient client = HttpClient.newHttpClient();
                                        HttpRequest request2 = HttpRequest.newBuilder(
                                                URI.create("http://" + ip + ":8080/api/changeOwner"))
                                                .PUT(HttpRequest.BodyPublishers.ofString(child.getName()))
                                                .build();
                                        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
                                        FileSystem.getFileParameters(child.getName()).setReplicatedOnNode(NodeParameters.nextID);
                                        if (child.delete())
                                            if (NodeParameters.DEBUG)
                                                System.out.println("[S-A] Successful deletion of " + child.getName());
                                        break;
                                    } catch (IOException | InterruptedException e) {
                                        if (!child.exists()) continue;
                                        if (i < 8) {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException ex) {
                                                throw new RuntimeException(ex);
                                            }
                                        } else {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }



            //Unlock files on agent
            while (NodeParameters.removeLocks.size() > 0) {
                String lockedFile = NodeParameters.removeLocks.poll();
                agentList.get(lockedFile).unLock();
            }

            //Lock files on agent
            while (NodeParameters.lockRequest.size() > 0) {
                String lockedFile = NodeParameters.lockRequest.poll();
                agentList.get(lockedFile).lock(NodeParameters.id);
            }




            //Only send it if there are other nodes in the system
            if(!Objects.equals(NodeParameters.id, NodeParameters.nextID)) {
                for (int i = 0; i < 10; i++) {
                    try { //Pass agentList to next one
                        HttpRequest request = HttpRequest.newBuilder(
                                        URI.create("http://" + IpTableCache.getInstance().getIp(NodeParameters.nextID).getHostAddress() + ":8080/api/syncagent"))
                                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(this)))
                                .build();
                        //if(NodeParameters.DEBUG) System.out.println("[S-A] request: " + request);
                        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() != 200) if (NodeParameters.DEBUG)
                            System.out.println("[S-A] Next node was not able to process Agent. Agent died here. RIP");
                        break;
                    } catch (IOException | InterruptedException e) {
                        if (i < 8) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        } else {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    }

    public boolean isForNext(String file){
        int hash = Hash.generateHash(file);
        return (((NodeParameters.nextID < hash && (NodeParameters.nextID > NodeParameters.id | (hash<NodeParameters.id)))|(NodeParameters.nextID > hash && hash < NodeParameters.id && NodeParameters.nextID > NodeParameters.id)));
    }

    public HashMap<String, FileParameters> getAgentList() {
        return agentList;
    }

    public void setAgentList(HashMap<String, FileParameters> agentList) {
        this.agentList = agentList;
    }

    public HashMap<Integer, String> getDeletionList() {
        return deletionList;
    }

    public void setDeletionList(HashMap<Integer, String> deletionList) {
        this.deletionList = deletionList;
    }

    public void printList() {
        if (NodeParameters.DEBUG) {
            System.out.println("[S-A] Agent's list: ");
            for (Map.Entry<String, FileParameters> entry : agentList.entrySet())
                System.out.println("[S-A] " + entry.getKey() + " Replicated on: " + entry.getValue().getReplicatedOnNode());
            System.out.println("[S-A] Local on this node list: ");
            for (Map.Entry<String, FileParameters> entry : FileSystem.getLocalFiles().entrySet())
                System.out.println("[S-A] " + entry.getKey() + " local on: " + entry.getValue().getLocalOnNode());
        }
    }
}
