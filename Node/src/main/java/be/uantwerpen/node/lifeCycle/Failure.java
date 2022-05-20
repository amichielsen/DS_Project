package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.agents.FailureAgent;
import be.uantwerpen.node.lifeCycle.running.Running;
import ch.qos.logback.core.pattern.parser.Node;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.internal.runners.statements.Fail;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

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
public class Failure{

    private int failedID;
    private static final Failure instance = new Failure();
    private Failure() {
        this.failedID = 0;
    }

    public static Failure getInstance(){
        return instance;
    }

    /**
     * Get prev and next node of failed node.
     * @param ID Id of failed node
     */
    public void nodeFailure(int ID)  {
        // Getting the Agent ready to fix all nodes -> will go round
        if (ID == NodeParameters.previousID && ID != NodeParameters.nextID) new FailureAgent(ID).run();

        // create a request
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create("http://"+NodeParameters.nameServerIp.getHostAddress() + ":8080/naming/failure?id=" + ID))
                .build();
        if(NodeParameters.DEBUG) {
            System.out.println(request);
        }
        // use the client to send the request
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        TreeMap<String, Integer> responseMap = null;
        try {
            responseMap = new ObjectMapper().readValue( response.body(), TreeMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if(responseMap.size() > 0) {
            int previousNode = responseMap.get("previous");
            int nextNode = responseMap.get("next");
            if (NodeParameters.DEBUG) {
                System.out.println(previousNode + " =prev, next= " + nextNode);
            }
            //update nodes
            try{
            if(previousNode == NodeParameters.id){
                if(NodeParameters.DEBUG){
                    System.out.println("I'm next ");
                }
                NodeParameters.nextID = nextNode;
                updatePreviousIdOfNextNode(previousNode, nextNode);
            } else if (nextNode == NodeParameters.id) {
                if(NodeParameters.DEBUG){
                    System.out.println("I'm prev ");
                }
                NodeParameters.previousID = previousNode;
                updateNextIdOfPreviousNode(nextNode, previousNode);
            }
            else {
                if(NodeParameters.DEBUG){
                    System.out.println("Shouldn't come here");
                }
                updateNextIdOfPreviousNode(nextNode, previousNode);
                updatePreviousIdOfNextNode(previousNode, nextNode);
            }
            }catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return;
            }
            if (NodeParameters.DEBUG) {
                System.out.println(response.body());
            }
        }
    }
    public String updateNextIdOfPreviousNode(Integer nextNode, Integer prevNode) throws IOException, InterruptedException {
        if (Objects.nonNull(nextNode)) {

            // create a client
            var client = HttpClient.newHttpClient();

            // create a request to get Ip from Id
            var request = HttpRequest.newBuilder(
                            URI.create("http://"+NodeParameters.nameServerIp.getHostAddress() +":8080/naming/host2IP?host="+ prevNode))
                    .build();

            if(NodeParameters.DEBUG){
                System.out.println(request);
            }

            // use the client to send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String hostIp = response.body();

            if(NodeParameters.DEBUG){
                System.out.println("Next: " + hostIp);
            }

            // create a request
            request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(""))
                    .uri(URI.create("http://" + hostIp + ":8080/api/updateNext?hostId=" + nextNode))
                    .build();

            // use the client to send the request
            HttpResponse<String> responses = client.send(request, HttpResponse.BodyHandlers.ofString());

            // the response:
            System.out.println(responses.body());
            return responses.body();

        } else return "error: hostID is null";
    }

    public String updatePreviousIdOfNextNode(Integer prevNode, Integer nextNode) throws IOException, InterruptedException {
        if (Objects.nonNull(prevNode)) {

            // create a client
            var client = HttpClient.newHttpClient();

            // create a request to get Ip from Id
            var request = HttpRequest.newBuilder(
                            URI.create("http://"+NodeParameters.nameServerIp.getHostAddress() +":8080/naming/host2IP?host="+ nextNode))
                    .build();

            // use the client to send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String hostIp = response.body();

            // create a request
            request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(""))
                    .uri(URI.create("http://" + hostIp + ":8080/api/updatePrevious?hostId=" + prevNode))
                    .build();

            // use the client to send the request
            HttpResponse<String> responses = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(NodeParameters.DEBUG){
                System.out.println("Previous: " + hostIp);
            }
            // the response:
            System.out.println(responses.body());
            return responses.body();

        } else return "error: hostID is null";
    }

    public void setFailedID(int failedID) {
        this.failedID = failedID;
    }
}
