package be.uantwerpen.node.utils.cron.jobs;

import be.uantwerpen.node.lifeCycle.LifeCycleController;
import be.uantwerpen.node.utils.NodeParameters;
import be.uantwerpen.node.utils.cron.CronJob;
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
        for (int i = 0; i < 6; i++) {
            try {
                ping(nodeParameters.getPreviousID());
                break;
            } catch (InterruptedException | IOException e) {
                if (i < 4) {
                    try {
                        if(NodeParameters.DEBUG) System.out.println("[Ping] Try: " + i + " For node: " + nodeParameters.getPreviousID());
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    e.printStackTrace();
                    System.out.println("[" + getName() + "] [Error] connection error with previous node (likely offline)");
                    //Failure failure = new Failure();
                    Failure.getInstance().nodeFailure(nodeParameters.getPreviousID());
                    return;
                }
                //throw new RuntimeException(e);
            }
        }
        if (Objects.equals(nodeParameters.getNextID(), nodeParameters.getPreviousID())) {
            return;
        }

        for (int i = 0; i < 6; i++) {
            // Next
            try {
                ping(nodeParameters.getNextID());
                break;
            } catch (InterruptedException | IOException e) {
                if (i < 4) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    System.out.println("[" + getName() + "] [Error] connection error with next node (likely offline)");
                    Failure.getInstance().nodeFailure(nodeParameters.getNextID());
                    return;
                    //throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "PingNeighboringNodeCron";
    }

    private void ping(int ID) throws IOException, InterruptedException {
        String ip = nodeParameters.getIP(ID).getHostAddress();
        if(NodeParameters.DEBUG) System.out.println("[Ping] ID: "+ ID + " IP: " + ip);
        var client = HttpClient.newHttpClient();

        var request = HttpRequest.newBuilder(
                        URI.create("http://"+ip+":8080/api/status"))
                .build();

        if(NodeParameters.DEBUG) System.out.println("[Ping] Request: " + request);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        if (response.statusCode() != 200) {
            System.out.println("["+getName()+"] [Error] node send non 200 code (likely shutting down/busy)");
            if (nodeParameters.getFailedNext() > NodeParameters.FAILURE_TRESHOLD) {
                Failure.getInstance().nodeFailure(ID);
                nodeParameters.resFailedNext();
            }
            else
                nodeParameters.incFailedNext();
        }
    }
}
