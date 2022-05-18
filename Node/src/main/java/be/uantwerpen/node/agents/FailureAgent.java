package be.uantwerpen.node.agents;

import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.cache.IpTableCache;
import be.uantwerpen.node.fileSystem.FileParameters;
import be.uantwerpen.node.fileSystem.FileSystem;
import be.uantwerpen.node.lifeCycle.running.services.FileSender;
import be.uantwerpen.node.utils.Hash;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;

public class FailureAgent extends Agent {
    private final int startingNode;
    private int hasBeenRunTimes;
    private final int failedNode;
    public FailureAgent(int failedNode) {
        this.failedNode = failedNode;
        this.startingNode = NodeParameters.id;
        this.hasBeenRunTimes = 0;
    }

    @Override
    public void run() {
        if(NodeParameters.DEBUG) System.out.println("[F-A] Node "+failedNode+ " has failed!");
        if(NodeParameters.DEBUG) System.out.println("[F-A] Agent Started at node "+startingNode+ " !");
        if (!(hasBeenRunTimes == 0) & startingNode == NodeParameters.id) return;
        // File has been uploaded to this node, is not a matching hash -> replicated instance has failed
        // 1. Find the new correct node
        // 2. Send the file to that instance
        // 3. Update list
        if(NodeParameters.DEBUG) System.out.println("[F-A] Sending new replicated files.");
        FileSystem.getLocalFiles().entrySet()
                                .stream()
                                .filter( e -> e.getValue().getReplicatedOnNode() == failedNode)
                                .forEach(e -> newReplication(e.getKey(),e.getValue()));

        // File has been replicated to this node, is a matching hash, local instance has failed -> delete
        if(NodeParameters.DEBUG) System.out.println("[F-A] Deleting replicated files...");
        FileSystem.getReplicatedFiles(false).entrySet()
                .removeIf(e -> e.getValue().getLocalOnNode() == failedNode);


        // File has been downloaded to this node -> delete
        if(NodeParameters.DEBUG) System.out.println("[F-A] Deleting downloaded files...");
        FileSystem.getDownloadedFiles().entrySet()
                .removeIf(e -> e.getValue().getLocalOnNode() == failedNode);

        // Sending
        if(NodeParameters.DEBUG) System.out.println("[F-A] Sending agent to next neighbor with id: "+ NodeParameters.nextID);
        try {
            var request = HttpRequest.newBuilder(
                            URI.create("http://" + IpTableCache.getInstance().getIp(NodeParameters.nextID).getHostAddress() + ":8080/api/agent?agent="+this))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            hasBeenRunTimes++;
            if(NodeParameters.DEBUG) System.out.println("[F-A] Done. Agent has been ran "+hasBeenRunTimes+" times. The dude is getting old.");
            if (hasBeenRunTimes > 50) if(NodeParameters.DEBUG) System.out.println("[F-A] There might be an agent in the loop, or a loop in the agent...");
            if (response.statusCode() != 200) if(NodeParameters.DEBUG) System.out.println("[F-A] Next node was not able to process Agent. Agent died here. RIP");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    private void newReplication(String file, FileParameters parameters) {
        try {
            System.out.println("[F-A] Sending file:" + file);
            var request = HttpRequest.newBuilder(
                            URI.create("http://"+NodeParameters.getNameServerIp().getHostAddress()+":8080/naming/file2host?filename="+file))
                            .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return;

            // Parameters
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response.body());
            int id = ((Long) json.get("id")).intValue();
            String ip = String.valueOf(json.get("ip"));

            // Send file
            FileSender.sendFile(NodeParameters.localFolder+"/"+file, ip, id, "Owner");
            if(NodeParameters.DEBUG) System.out.print(" [DONE]");


        } catch (IOException | InterruptedException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
