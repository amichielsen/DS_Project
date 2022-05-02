package be.uantwerpen.node.cron.jobs;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.cron.CronJob;
import be.uantwerpen.node.lifeCycle.Failure;
import be.uantwerpen.node.lifeCycle.Shutdown;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class PingNeighboringNode extends CronJob {
    private NodeParameters nodeParameters;

    public PingNeighboringNode(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
        this.nodeParameters = NodeParameters.getInstance();
    }

    @Override
    public void run() {
        // !! Toevoegen van failure data -> veranderen van state verbeteren
        if (Objects.equals(nodeParameters.getNextID(), nodeParameters.getId()) | Objects.equals(nodeParameters.getPreviousID(), nodeParameters.getId())) {
            return;
        }
        // Previous
        try {
            URL previous = new URL("http://"+nodeParameters.getIP(nodeParameters.getPreviousID())+":8888/api/status");
            System.out.println(previous);
            HttpURLConnection previousConnection = (HttpURLConnection) previous.openConnection();
            previousConnection.setRequestMethod("GET");

            if (previousConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("["+getName()+"] [Error] previous node send non 200 code (likely shutting down/busy)");
                lifeCycleController.ChangeState(new Failure(lifeCycleController, nodeParameters.getPreviousID()));
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("["+getName()+"] [Error] connection error with previous node (likely offline)");
            lifeCycleController.ChangeState(new Failure(lifeCycleController, nodeParameters.getPreviousID()));
            return;
            //throw new RuntimeException(e);
        }

        // Next
        try {
            URL next = new URL("http://"+nodeParameters.getIP(nodeParameters.getNextID())+":8888/api/status");
            HttpURLConnection nextConnection = (HttpURLConnection) next.openConnection();
            nextConnection.setRequestMethod("GET");

            if (nextConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("["+getName()+"] [Error] next node send non 200 code (likely shutting down/busy)");
                lifeCycleController.ChangeState(new Failure(lifeCycleController, nodeParameters.getNextID()));

            }
        } catch (IOException e) {
            System.out.println("["+getName()+"] [Error] connection error with next node (likely offline)");
            lifeCycleController.ChangeState(new Failure(lifeCycleController, nodeParameters.getNextID()));
            //throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "PingNeighboringNodeCron";
    }
}
