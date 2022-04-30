package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.lifeCycle.running.RunningRestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The failure mode.
 * 1. If we detect a failure we will get here and report to the NameServer.
 * 2. Find a new route!!!
 */
public class Failure extends State {

    private int oldPrevNode;
    private int OldNextNode;
    private int failedID = 0;

    public Failure(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
    }
    public Failure(LifeCycleController lifeCycleController, int failedID) {
        super(lifeCycleController);
        this.failedID = failedID;
    }

    @Override
    public void run() {
            System.out.println("i failed :(");
        try {
            this.nodeFailure(this.failedID);
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

        HashMap<String, Integer> map = new HashMap<>();
        map.put("id", ID);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(map);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://" +NodeParameters.nameServerIp + ":8080/naming/failure"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject prevNode = new JSONObject();
        JSONObject nextNode = new JSONObject();
        RunningRestController.getStatus();
        prevNode.put("previousNeighbor", NodeParameters.nextID);

        RunningRestController.getStatus();
        nextNode.put("nextNeighbor", NodeParameters.previousID);

        nextNode.replace("previousNeighbor", NodeParameters.nextID, prevNode.values());
        prevNode.replace("nextNeighbor", NodeParameters.previousID, nextNode.values());



    }



}
