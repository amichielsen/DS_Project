package be.uantwerpen.node.cron.jobs;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.cron.CronJob;
import be.uantwerpen.node.lifeCycle.Failure;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class PingNeighboringNode extends CronJob {
    private final NodeParameters nodeParameters;

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
            var client = HttpClient.newHttpClient();

            var request = HttpRequest.newBuilder(
                URI.create("http://"+nodeParameters.getIP(nodeParameters.getPreviousID()).getHostAddress()+":8888/api/status"))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("["+getName()+"] [Error] previous node send non 200 code (likely shutting down/busy)");
                if (nodeParameters.getFailedPrevious() > NodeParameters.FAILURE_TRESHOLD) {
                    lifeCycleController.ChangeState(new Failure(lifeCycleController, nodeParameters.getPreviousID()));
                    nodeParameters.resFailedPrevious();
                }
                else
                    nodeParameters.incFailedPrevious();
                return;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            System.out.println("["+getName()+"] [Error] connection error with previous node (likely offline)");
            System.out.println(nodeParameters.getPreviousID());
            lifeCycleController.ChangeState(new Failure(lifeCycleController, nodeParameters.getPreviousID()));
            return;
            //throw new RuntimeException(e);
        }

        // Next
        try {
            var client = HttpClient.newHttpClient();

            var request = HttpRequest.newBuilder(
                URI.create("http://"+nodeParameters.getIP(nodeParameters.getNextID()).getHostAddress()+":8888/api/status"))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() != 200) {
                System.out.println("["+getName()+"] [Error] next node send non 200 code (likely shutting down/busy)");
                if (nodeParameters.getFailedNext() > NodeParameters.FAILURE_TRESHOLD) {
                    lifeCycleController.ChangeState(new Failure(lifeCycleController, nodeParameters.getNextID()));
                    nodeParameters.resFailedNext();
                }
                else
                    nodeParameters.incFailedNext();
            }
        } catch (InterruptedException | IOException e) {
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
