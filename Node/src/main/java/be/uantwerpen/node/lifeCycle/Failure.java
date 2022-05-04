package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.TreeMap;

/**
 * The failure mode.
 * 1. If we detect a failure we will get here and report to the NameServer.
 * 2. Find a new route!!!
 */
public class Failure extends State {

    public Failure(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
    }
    public Failure(LifeCycleController lifeCycleController, int failedID) {
        super(lifeCycleController, failedID);
    }

    @Override
    public void run() {
            System.out.println("i failed :(");
        try {
            this.nodeFailure(this.param);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get prev and next node of failed node.
     * @param ID Id of failed node
     */
    public void nodeFailure(int ID) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder().build();

        // create a request
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create("http://"+NodeParameters.nameServerIp.getHostAddress() + ":8080/naming/failure?id=" + ID))
                .build();
        if(NodeParameters.DEBUG) {
            System.out.println(request);
        }
        // use the client to send the request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        TreeMap<String, Integer> responseMap = new ObjectMapper().readValue( response.body(), TreeMap.class);

        if(responseMap.size() > 0) {
            int previousNode = responseMap.get("previous");
            int nextNode = responseMap.get("next");
            if (NodeParameters.DEBUG) {
                System.out.println(previousNode + " =prev, next= " + nextNode);
            }
            //update nodes
            updateNextIdOfPreviousNode(nextNode,previousNode);
            updatePreviousIdOfNextNode(previousNode, nextNode);

            if (NodeParameters.DEBUG) {
                System.out.println(response.body());
            }
        }
    }
    public String updateNextIdOfPreviousNode(Integer hostId, Integer nextHostId) throws IOException, InterruptedException {
        if (Objects.nonNull(nextHostId)) {

            // create a client
            var client = HttpClient.newHttpClient();

            // create a request to get Ip from Id
            var request = HttpRequest.newBuilder(
                            URI.create("http://"+NodeParameters.nameServerIp.getHostAddress() +":8080/naming/host2IP?host="+ hostId))
                    .build();

            // use the client to send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String hostIp = response.body();

            // create a request
            request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(""))
                    .uri(URI.create("http://" + hostIp + ":8080/api/updateNext?hostId=" + nextHostId))
                    .build();

            // use the client to send the request
            HttpResponse<String> responses = client.send(request, HttpResponse.BodyHandlers.ofString());

            // the response:
            System.out.println(responses.body());
            return responses.body();

        } else return "error: hostID is null";
    }

    public String updatePreviousIdOfNextNode(Integer hostId, Integer previousHostId) throws IOException, InterruptedException {
        if (Objects.nonNull(previousHostId)) {

            // create a client
            var client = HttpClient.newHttpClient();

            // create a request to get Ip from Id
            var request = HttpRequest.newBuilder(
                            URI.create("http://"+NodeParameters.nameServerIp.getHostAddress() +":8080/naming/host2IP?host="+ hostId))
                    .build();

            // use the client to send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String hostIp = response.body();

            // create a request
            request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(""))
                    .uri(URI.create("http://" + hostIp + ":8080/api/updatePrevious?hostId=" + previousHostId))
                    .build();

            // use the client to send the request
            HttpResponse<String> responses = client.send(request, HttpResponse.BodyHandlers.ofString());

            // the response:
            System.out.println(responses.body());
            return responses.body();

        } else return "error: hostID is null";
    }


}
