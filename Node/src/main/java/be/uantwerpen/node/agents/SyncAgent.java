package be.uantwerpen.node.agents;

import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.cache.IpTableCache;
import be.uantwerpen.node.lifeCycle.running.services.FileSender;
import be.uantwerpen.node.lifeCycle.running.services.ReplicationService;
import be.uantwerpen.node.utils.Hash;
import org.w3c.dom.Node;

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



    @Override
    public void run() {
        File dir = new File(NodeParameters.replicaFolder);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if(NodeParameters.bookkeeper.get(child.getName()) != null){
                    if(!NodeParameters.systemFiles.containsKey(child.getName())){
                        NodeParameters.systemFiles.put(child.getName(), 0);
                    }
                }
                while(NodeParameters.lockRequest.size() > 0){
                    String lockedFile = NodeParameters.lockRequest.poll();
                    NodeParameters.systemFiles.put(lockedFile, 1);
                }
                while(NodeParameters.removeLocks.size() > 0){
                    String lockedFile = NodeParameters.removeLocks.poll();
                    NodeParameters.systemFiles.put(lockedFile, 0);
                }
                if(NodeParameters.nextID < Hash.generateHash(child.getName())){
                    try {
                        String ipNext = IpTableCache.getInstance().getIp(NodeParameters.nextID).getHostAddress();
                        HashMap<String, Integer> fileInfo = (HashMap) NodeParameters.bookkeeper.get(child.getName());
                        FileSender.sendFile(child.getPath(), ipNext, fileInfo.get("Local"), "Owner");
                        var client = HttpClient.newHttpClient();
                        var request2 = HttpRequest.newBuilder(
                                        URI.create("http://" + ipNext+ ":8080/api/addLogEntry?filename=" + child.getName() + "?log=" + fileInfo))
                                .build();
                        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
                        child.delete();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
    }
}
