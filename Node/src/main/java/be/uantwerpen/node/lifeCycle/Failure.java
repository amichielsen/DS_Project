package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.lifeCycle.running.RunningRestController;
import org.json.simple.JSONObject;

import java.net.Inet4Address;

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

    @Override
    public void run() {
            System.out.println("i failed :(");
    }
    /**
     * Get prev and next node of failed node.
     * @param Id Id of failed node
     * @param Ip Ip of failed node
     */
    public static void nodeFailure(int Id, Inet4Address Ip){


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
