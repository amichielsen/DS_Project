package be.uantwerpen.node.cron;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.cron.jobs.PingNeighboringNode;
import be.uantwerpen.node.lifeCycle.State;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CronJobScheduler implements Runnable {
    private State stateStarted;
    private LifeCycleController lifeCycleController;
    private HashMap<CronJob,TimeDetails> allJobs = new HashMap<CronJob,TimeDetails>();
    public CronJobScheduler(LifeCycleController lifeCycleController) {
        this.lifeCycleController = lifeCycleController;
        this.stateStarted = lifeCycleController.getCurrentState();
        addCronJob(new PingNeighboringNode(lifeCycleController), 1);
    }

    public void addCronJob(CronJob job, Integer timeInBetweenJobs) {
        allJobs.put(job, new TimeDetails(LocalDateTime.now(), timeInBetweenJobs));
    }
    public void run() {
        System.out.println("[CRON] [Info] scheduler started");
        System.out.println(stateStarted);
        while (stateStarted == lifeCycleController.getCurrentState()) {
            System.out.println("loooop");
            for (Map.Entry<CronJob,TimeDetails> set : allJobs.entrySet()) {

                LocalDateTime shouldRunOn = set.getValue().getLastRan().plusSeconds(set.getValue().getRunEach());
                if ( shouldRunOn.isBefore(LocalDateTime.now())) {
                    new Thread(set.getKey()).start();
                    set.getValue().setLastRan(LocalDateTime.now());
                    // DEBUG
                    if (NodeParameters.DEBUG) {
                        System.out.println("["+set.getKey().getCronName()+"] is run on "+ set.getValue().getLastRan());
                    }
                }
            }
        }
    }
}
