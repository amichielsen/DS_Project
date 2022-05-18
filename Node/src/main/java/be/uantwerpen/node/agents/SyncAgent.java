package be.uantwerpen.node.agents;

import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.cache.IpTableCache;
import be.uantwerpen.node.fileSystem.FileParameters;
import be.uantwerpen.node.fileSystem.FileSystem;
import be.uantwerpen.node.lifeCycle.running.services.FileSender;
import be.uantwerpen.node.lifeCycle.running.services.ReplicationService;
import be.uantwerpen.node.utils.Hash;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/***
 * SyncAgent: Agent responsible for synchronizing the whole system
 * 1. Updates list of all files in the system
 * 2. Lock files if required
 * 3. Unlock files if required
 * 4. Checks whether Next Node should be the owner of the file
 */
public class SyncAgent extends Agent {
    private HashMap<String, Integer> systemFiles = new HashMap<>();
    private static Queue<String> lockRequest = new LinkedList<>();
    private static Queue<String> removeLocks = new LinkedList<>();
    private final File dir;
    public SyncAgent() {
        dir = new File(NodeParameters.replicaFolder);
    }

    @Override
    public void run() {
        File[] directoryListing = dir.listFiles();
        if (directoryListing == null) return;

        for (File child : directoryListing) {
            FileSystem.addLocal(child.getName(), 0);

            while(lockRequest.size() > 0){
                String lockedFile = lockRequest.poll();
                FileSystem.getFileParameters(lockedFile).lock();
            }
            while(removeLocks.size() > 0){
                String lockedFile = removeLocks.poll();
                FileSystem.getFileParameters(lockedFile).unLock();
            }

            if(NodeParameters.nextID < Hash.generateHash(child.getName())){
                try {
                    String ipNext = IpTableCache.getInstance().getIp(NodeParameters.nextID).getHostAddress();
                    FileParameters currentParamaters = FileSystem.getFileParameters(child.getName());

                    FileSender.sendFile(child.getPath(), ipNext, currentParamaters.getLocalOnNode(), "Owner");
                    var client = HttpClient.newHttpClient();
                    var request2 = HttpRequest.newBuilder(
                                    URI.create("http://" + ipNext+ ":8080/api/addLogEntry?filename=" + child.getName() + "?log=" + currentParamaters))
                            .build();
                    HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
                    child.delete();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public static void addLockRequest(String filename){
        lockRequest.add(filename);
    }

    public static void removeLock(String filename){
        removeLocks.add(filename);
    }
}
