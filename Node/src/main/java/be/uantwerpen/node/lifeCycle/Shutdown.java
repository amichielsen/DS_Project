package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.cache.IpTableCache;
import be.uantwerpen.node.fileSystem.FileParameters;
import be.uantwerpen.node.fileSystem.FileSystem;
import be.uantwerpen.node.lifeCycle.running.services.FileSender;
import be.uantwerpen.node.lifeCycle.running.services.ReplicationService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The last and final state...
 * Here we send a REST to our current neighbors, updating them with their new neighbor.
 * At the same time we also let this know to our NameServer.
 * Now we can peacefully go to sleep...
 */
public class Shutdown extends State {
    public Shutdown(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
    }

    @Override
    public void run() {

        try {
            var previousIp = IpTableCache.getInstance().getIp(NodeParameters.previousID).getHostAddress();
            var nextIp = IpTableCache.getInstance().getIp(NodeParameters.nextID).getHostAddress();
            updateNextIdOfPreviousNode(previousIp, NodeParameters.nextID);
            updatePreviousIdOfNextNode(nextIp, NodeParameters.previousID);
            this.sendFilesToPrevious();
            this.deleteLocalFiles();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    //contact previous node to update its next
    public String updateNextIdOfPreviousNode(String hostIp, Integer nextHostId) throws IOException, InterruptedException {
        if (Objects.nonNull(nextHostId)) {
            // create a client
            var client = HttpClient.newHttpClient();

            // create a request
            var request = HttpRequest.newBuilder(
                            URI.create("http://" + hostIp + ":8080/api/updateNext?hostId=" + nextHostId))
                    .build();

            // use the client to send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // the response:
            System.out.println(response.body());
            return response.body();

        } else return "error: hostID is null";
    }

    //contact previous node to update its next
    public String updatePreviousIdOfNextNode(String hostIp, Integer previousHostId) throws IOException, InterruptedException {
        if (Objects.nonNull(previousHostId)) {
            // create a client
            var client = HttpClient.newHttpClient();

            // create a request
            var request = HttpRequest.newBuilder(
                            URI.create("http://" + hostIp + ":8080/api/updatePrevious?hostId=" + previousHostId))
                    .build();

            // use the client to send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // the response:
            System.out.println(response.body());
            return response.body();

        } else return "error: hostID is null";
    }


    //File handling
    public void sendFilesToPrevious() {
        File dir = new File(NodeParameters.replicaFolder);
        File[] directoryListing = dir.listFiles();

        if (directoryListing != null) {
            HashMap<String, be.uantwerpen.node.fileSystem.FileParameters> replicatedFiles = (HashMap<String, be.uantwerpen.node.fileSystem.FileParameters>) FileSystem.getReplicatedFiles(true);
            for(Map.Entry<String, FileParameters> entry: replicatedFiles.entrySet()){
                Path filepath = Path.of(NodeParameters.replicaFolder + "/" + entry.getKey());
                try {
                    var client = HttpClient.newHttpClient();
                    if (Objects.equals(entry.getValue().getLocalOnNode(), NodeParameters.previousID)) {
                        var request = HttpRequest.newBuilder(
                                        URI.create("http://" + IpTableCache.getInstance().getIp(NodeParameters.previousID) + ":8080/api/status"))
                                .build();
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            JSONParser parser = new JSONParser();
                            JSONObject json = (JSONObject) parser.parse(response.body());
                            // Adding to ip cache
                            int previousID = ((Long) json.get("previousNeighbor")).intValue();
                            FileSender.sendFile(String.valueOf(filepath), IpTableCache.getInstance().getIp(previousID).getHostAddress(), entry.getValue().getLocalOnNode(), "Owner");

                            var request2 = HttpRequest.newBuilder(
                                            URI.create("http://" + IpTableCache.getInstance().getIp(previousID) + ":8080/api/changeOwner?filename=" + entry.getKey()))
                                    .build();
                            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
                        }
                    } else {

                        FileSender.sendFile(String.valueOf(filepath), IpTableCache.getInstance().getIp(NodeParameters.previousID).getHostAddress(), entry.getValue().getLocalOnNode(), "Owner");
                        var request2 = HttpRequest.newBuilder(
                                        URI.create("http://" + IpTableCache.getInstance().getIp(NodeParameters.previousID) + ":8080/api/changeOwner?filename=" + entry.getKey()))
                                .build();
                        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
                    }
                } catch (IOException | ParseException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void deleteLocalFiles() {
        File dir = new File(NodeParameters.localFolder);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            HashMap<String, be.uantwerpen.node.fileSystem.FileParameters> localFiles = (HashMap<String, be.uantwerpen.node.fileSystem.FileParameters>) FileSystem.getLocalFiles();
            for (Map.Entry<String, FileParameters> entry: localFiles.entrySet()) {
                try {
                    var client = HttpClient.newHttpClient();
                    var request = HttpRequest.newBuilder(
                                    URI.create("http://" + IpTableCache.getInstance().getIp(entry.getValue().getReplicatedOnNode()) + ":8080/api/localDeletion?filename=" + entry.getKey()))
                            .POST(HttpRequest.BodyPublishers.ofString(""))
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }

    }
}
