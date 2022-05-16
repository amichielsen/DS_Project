package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.cache.IpTableCache;
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
import java.util.HashMap;
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
            var previousIp = getIPfromHostId(NodeParameters.getInstance().getPreviousID());
            var nextIp = getIPfromHostId(NodeParameters.getInstance().getNextID());
            updateNextIdOfPreviousNode(previousIp, NodeParameters.nextID);
            updatePreviousIdOfNextNode(nextIp, NodeParameters.previousID);
            this.sendFilesToPrevious();
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


    //remove itself from Naming server map


    public String getIPfromHostId(Integer hostId) throws IOException, InterruptedException {

        if (Objects.nonNull(hostId)) {
            // create a client
            var client = HttpClient.newHttpClient();

            // create a request
            var request = HttpRequest.newBuilder(
                            URI.create("http://" + NodeParameters.nameServerIp.getHostAddress() + ":8080/naming/host2IP?host=" + hostId))
                    .build();

            // use the client to send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // the response:
            System.out.println(response.body());
            return response.body();

        } else return "localhost";
    }


    //File handling
    public void sendFilesToPrevious() {
        File dir = new File(NodeParameters.replicaFolder);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                HashMap<String, Integer> fileInfo = (HashMap<String, Integer>) NodeParameters.bookkeeper.get(child.getName());
                try {
                    var client = HttpClient.newHttpClient();
                    if (Objects.equals(fileInfo.get("Local"), NodeParameters.previousID)) {
                        var request = HttpRequest.newBuilder(
                                        URI.create("http://" + IpTableCache.getInstance().getIp(NodeParameters.previousID) + ":8080/ipa/status"))
                                .build();
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            JSONParser parser = new JSONParser();
                            JSONObject json = (JSONObject) parser.parse(response.body());
                            // Adding to ip cache
                            int previousID = ((Long) json.get("previousNeighbor")).intValue();
                            FileSender.sendFile(child.getPath(), IpTableCache.getInstance().getIp(previousID).getHostAddress(), fileInfo.get("Local"));
                            var request2 = HttpRequest.newBuilder(
                                            URI.create("http://" + IpTableCache.getInstance().getIp(NodeParameters.previousID) + ":8080/ipa/addLogEntry?filename=" + child.getName() + "?log=" + fileInfo))
                                    .build();
                            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
                        }
                    } else {
                        FileSender.sendFile(child.getPath(), IpTableCache.getInstance().getIp(NodeParameters.previousID).getHostAddress(), fileInfo.get("Local"));
                        var request2 = HttpRequest.newBuilder(
                                        URI.create("http://" + IpTableCache.getInstance().getIp(NodeParameters.previousID) + ":8080/ipa/addLogEntry?filename=" + child.getName() + "?log=" + fileInfo))
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
            for (File child : directoryListing) {
                try {
                    var client = HttpClient.newHttpClient();
                    var request = HttpRequest.newBuilder(
                                    URI.create("http://" + IpTableCache.getInstance().getIp(NodeParameters.previousID) + ":8080/ipa/localDeletion?filename=" + child.getName()))
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
