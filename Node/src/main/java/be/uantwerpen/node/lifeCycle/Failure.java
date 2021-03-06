package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.utils.NodeParameters;
import be.uantwerpen.node.agents.FailureAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    public void nodeFailure(int ID) {
        if (ID == NodeParameters.previousID) //Only next node can start failure sequence
        {
            this.failureHandler(ID);
            Thread failureThread = new Thread(new FailureAgent(ID));
            failureThread.start();
        }
    }


    private void failureHandler(int ID){
        // create a request
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create("http://"+NodeParameters.nameServerIp.getHostAddress() + ":8080/naming/failure?id=" + ID))
                .build();
        if(NodeParameters.DEBUG) {
            System.out.println("[Failure] "+request);
        }
        // use the client to send the request
        HttpResponse<String> response;
        try {
            response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        TreeMap<String, Integer> responseMap;
        try {
            responseMap = new ObjectMapper().readValue( response.body(), TreeMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if(responseMap.size() == 0) return; // Response is empty, no need to compute

        int previousNode = responseMap.get("previous");
        int nextNode = responseMap.get("next");
        if (NodeParameters.DEBUG) System.out.println("[Failure] "+ previousNode + " =prev, next= " + nextNode);
        //update nodes
        try{
            if(previousNode == NodeParameters.id){
                if(NodeParameters.DEBUG) System.out.println("[Failure] I'm next ");
                NodeParameters.nextID = nextNode;
                updatePreviousIdOfNextNode(previousNode, nextNode);
            } else if (nextNode == NodeParameters.id) {
                if(NodeParameters.DEBUG) System.out.println("[Failure] I'm prev ");
                NodeParameters.previousID = previousNode;
                updateNextIdOfPreviousNode(nextNode, previousNode);
            }
            else {
                if(NodeParameters.DEBUG) System.out.println("Shouldn't come here");

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

    public void updateNextIdOfPreviousNode(Integer nextNode, Integer prevNode) throws IOException, InterruptedException {
        if (nextNode == null) return;

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

        if(NodeParameters.DEBUG) System.out.println("Next: " + hostIp);

        // create a request
        request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create("http://" + hostIp + ":8080/api/updateNext?hostId=" + nextNode))
                .build();

        // use the client to send the request
        HttpResponse<String> responses = client.send(request, HttpResponse.BodyHandlers.ofString());

        // the response:
        if(NodeParameters.DEBUG) System.out.println("[Failure] " + responses.body());
        responses.body();
    }

    public void updatePreviousIdOfNextNode(Integer prevNode, Integer nextNode) throws IOException, InterruptedException {
        if (prevNode == null) return;
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


        // the response:
        if(NodeParameters.DEBUG) System.out.println("[Failure] Previous: " + hostIp);
        if(NodeParameters.DEBUG) System.out.println("[Failure] " +responses.body());
    }

    public void setFailedID(int failedID) {
        this.failedID = failedID;
    }
}
