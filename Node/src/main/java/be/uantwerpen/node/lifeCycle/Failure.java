package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.lifeCycle.running.RunningRestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.asm.TypeReference;

import java.io.DataInput;
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
import java.util.TreeMap;

/**
 * The failure mode.
 * 1. If we detect a failure we will get here and report to the NameServer.
 * 2. Find a new route!!!
 */
public class Failure extends State {

    private int oldPrevNode;
    private int OldNextNode;


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

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create("http://"+NodeParameters.nameServerIp.getHostAddress() + ":8080/naming/failure?id=" + ID))
                .build();
        if(NodeParameters.DEBUG) {
            System.out.println(request);
        }

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        TreeMap<String, Integer> responseMap = new ObjectMapper().readValue( response.body(), TreeMap.class);

        int previousNode = responseMap.get("previous");
        int nextNode = responseMap.get("next");
        if(NodeParameters.DEBUG) {
            System.out.println(previousNode + " =prev, next= " + nextNode);
        }
        if(NodeParameters.DEBUG) {
            System.out.println(response.body());
        }
        /*JSONObject prevNode = new JSONObject();
        JSONObject nextNode = new JSONObject();
        RunningRestController.getStatus();
        prevNode.put("previousNeighbor", NodeParameters.nextID);

        RunningRestController.getStatus();
        nextNode.put("nextNeighbor", NodeParameters.previousID);

        nextNode.replace("previousNeighbor", NodeParameters.nextID, prevNode.values());
        prevNode.replace("nextNeighbor", NodeParameters.previousID, nextNode.values());


         */


    }



}
