package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.utils.NodeParameters;
import be.uantwerpen.node.utils.cache.IpTableCache;
import be.uantwerpen.node.utils.fileSystem.FileParameters;
import be.uantwerpen.node.utils.fileSystem.FileSystem;
import be.uantwerpen.node.lifeCycle.running.services.FileSender;
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
 * Files are also sent to other nodes
 * Now we can peacefully go to sleep...
 */
public class Shutdown extends State {
    public Shutdown(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
    }

    @Override
    public void run() {

        try {
            this.removeFromNS();
            String previousIp = IpTableCache.getInstance().getIp(NodeParameters.previousID).getHostAddress();
            String nextIp = IpTableCache.getInstance().getIp(NodeParameters.nextID).getHostAddress();
            updateNextIdOfPreviousNode(previousIp, NodeParameters.nextID);
            updatePreviousIdOfNextNode(nextIp, NodeParameters.previousID);
            this.sendFilesToPrevious();
            this.deleteLocalFiles();
            System.exit(0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Remove this node from the NameServer
     */
    public void removeFromNS(){
        HttpClient client = HttpClient.newHttpClient();

        // create a request
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create("http:/" + NodeParameters.nameServerIp + ":8080/naming/Id?Id="+NodeParameters.id))
                .DELETE()
                .build();

        // use the client to send the request
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(NodeParameters.DEBUG) System.out.println("Deletion on nameserver was a " + response.body() );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Contact the previous node to update its next node
     * @param hostIp IP of previous node
     * @param nextHostId ID of next node
     * @return Status message
     */
    public String updateNextIdOfPreviousNode(String hostIp, Integer nextHostId) throws IOException, InterruptedException {
        if (Objects.nonNull(nextHostId)) {
            // create a client
            HttpClient client = HttpClient.newHttpClient();

            // create a request
            HttpRequest request = HttpRequest.newBuilder(
                            URI.create("http://" + hostIp + ":8080/api/updateNext?hostId=" + nextHostId))
                    .PUT(HttpRequest.BodyPublishers.ofString(""))
                    .build();

            // use the client to send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // the response:
            if(NodeParameters.DEBUG) System.out.println("[SD] " + response.body());
            return response.body();

        } else return "error: hostID is null";
    }

    /**
     * Contact the Next node to update its previous node
     * @param hostIp IP of next node
     * @param previousHostId ID of previous node
     * @return Status message
     */
    public String updatePreviousIdOfNextNode(String hostIp, Integer previousHostId) throws IOException, InterruptedException {
        if (Objects.nonNull(previousHostId)) {
            // create a client
            HttpClient client = HttpClient.newHttpClient();

            // create a request
            HttpRequest request = HttpRequest.newBuilder(
                            URI.create("http://" + hostIp + ":8080/api/updatePrevious?hostId=" + previousHostId))
                    .PUT(HttpRequest.BodyPublishers.ofString(""))
                    .build();

            // use the client to send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // the response:
            if(NodeParameters.DEBUG) System.out.println("[SD] " + response.body());
            return response.body();

        } else return "error: hostID is null";
    }


    /**
     * Send all replicated files on this node to the previous one
     * OR to the the previous of the previous in case the previous node is the owner
     */
    public void sendFilesToPrevious() {
        File dir = new File(NodeParameters.replicaFolder);
        File[] directoryListing = dir.listFiles();

        if (directoryListing == null) return;// Directory is empty -> return

        HashMap<String, FileParameters> replicatedFiles = (HashMap<String, FileParameters>) FileSystem.getReplicatedFiles(true);

        for(Map.Entry<String, FileParameters> entry: replicatedFiles.entrySet()){
            Path filepath = Path.of(NodeParameters.replicaFolder + "/" + entry.getKey());
            try {
                // Local is on previous ID
                if (Objects.equals(entry.getValue().getLocalOnNode(), NodeParameters.previousID)) {
                    HttpRequest request = HttpRequest.newBuilder(
                                    URI.create("http:/" + IpTableCache.getInstance().getIp(NodeParameters.previousID) + ":8080/api/neighbours"))
                            .build();
                    System.out.println(request);
                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());

                    if (response.statusCode() != 200) return;

                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(response.body());
                    // Adding to ip cache
                    int previousID = ((Long) json.get("previousNeighbor")).intValue();
                    if(NodeParameters.DEBUG) System.out.println("[SD] Filepath: "+filepath);
                    FileSender.sendFile(String.valueOf(filepath), IpTableCache.getInstance().getIp(previousID).getHostAddress(), entry.getValue().getLocalOnNode(), "Owner");

                    HttpRequest request2 = HttpRequest.newBuilder(
                                    URI.create("http:/" + IpTableCache.getInstance().getIp(previousID) + ":8080/api/changeOwner"))
                            .PUT(HttpRequest.BodyPublishers.ofString(entry.getKey()))
                            .build();
                    if(NodeParameters.DEBUG)
                        if(NodeParameters.DEBUG) System.out.println("[SD] Request change owner: " +request2);
                    HttpResponse<String> response2 = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());
                    return;

                }

                // All other cases
                if(NodeParameters.DEBUG) System.out.println("[SD] Filepath (other): "+filepath);
                FileSender.sendFile(String.valueOf(filepath), IpTableCache.getInstance().getIp(NodeParameters.previousID).getHostAddress(), entry.getValue().getLocalOnNode(), "Owner");
                HttpRequest request2 = HttpRequest.newBuilder(
                                URI.create("http:/" + IpTableCache.getInstance().getIp(NodeParameters.previousID) + ":8080/api/changeOwner"))
                        .PUT(HttpRequest.BodyPublishers.ofString(entry.getKey()))
                        .build();
                if(NodeParameters.DEBUG) System.out.println("[SD] Request change owner: " +request2);
                HttpResponse<String> response2 = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());
                if(NodeParameters.DEBUG) System.out.println("[SD] Response: " + response2);
            } catch (IOException | ParseException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Delete all local files and let the owner know
     */
    public void deleteLocalFiles() {
        File dir = new File(NodeParameters.localFolder);
        File[] directoryListing = dir.listFiles();

        if (directoryListing == null) return; // Directory is empty -> return

        HashMap<String, FileParameters> localFiles = (HashMap<String, FileParameters>) FileSystem.getLocalFiles();
        for (Map.Entry<String, FileParameters> entry: localFiles.entrySet()) {
            try {
                if(entry.getValue().getReplicatedOnNode() != NodeParameters.id) {
                    HttpRequest request = HttpRequest.newBuilder(
                                    URI.create("http:/" + IpTableCache.getInstance().getIp(entry.getValue().getReplicatedOnNode()) + ":8080/api/localDeletion?filename=" + entry.getKey()))
                            .POST(HttpRequest.BodyPublishers.ofString(""))
                            .build();
                    if(NodeParameters.DEBUG) System.out.println("[SD] " + request);
                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                }
                } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
