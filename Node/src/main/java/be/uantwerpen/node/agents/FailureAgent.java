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

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;

public class FailureAgent extends Agent {
    private int failedNode;
    public FailureAgent(int failedNode) {
        this.failedNode = failedNode;
    }

    @Override
    public void run() {



        // File has been uploaded to this node, is not a matching hash -> replicated instance has failed
        // 1. Find the new correct node
        // 2. Send the file to that instance
        // 3. Update list
        /*
        FileSystem.getLocalFiles().entrySet()
                                .stream()
                                .filter( e -> e.getValue().getReplicatedOnNode() == failedNode)
                                .forEach();

        // File has been replicated to this node, is a matching hash -> local instance has failed
        // 1. Remove file
        FileSystem.getReplicatedFiles(false).entrySet()
                .removeIf(e -> e.getValue().getLocalOnNode() == failedNode);


        // File has been downloaded to this node -> delete
        FileSystem.getDownloadedFiles().entrySet()
                .removeIf(e -> e.getValue().getLocalOnNode() == failedNode);
    }

    private void newReplication(String file, FileParameters parameters) {
        try {
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
            //FileSender.sendFile(String path, String host, int id, String type)


        } catch (IOException | InterruptedException | ParseException e) {
            throw new RuntimeException(e);
        }
        */
    }

}


