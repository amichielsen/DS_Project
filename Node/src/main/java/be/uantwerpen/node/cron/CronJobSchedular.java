package be.uantwerpen.node.cron;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.lifeCycle.State;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CronJobSchedular {
    private State stateStarted;
    private LifeCycleController lifeCycleController;
    private HashMap<CronJob,TimeDetails> allJobs = new HashMap<CronJob,TimeDetails>();
    public CronJobSchedular(LifeCycleController lifeCycleController) {
        this.lifeCycleController = lifeCycleController;
        this.stateStarted = lifeCycleController.getCurrentState();
    }

    public void addCronJob(CronJob job, Integer timeInBetweenJobs) {
        allJobs.put(job, new TimeDetails(LocalDateTime.now(), timeInBetweenJobs));
    }
    public void run() {
        while (stateStarted == lifeCycleController.getCurrentState()) {
            for (Map.Entry<CronJob,TimeDetails> set : allJobs.entrySet()) {
                LocalDateTime shouldRunOn = set.getValue().getLastRan().plusSeconds(set.getValue().getRunEach());
                if ( shouldRunOn.isBefore(LocalDateTime.now())) {
                    set.getKey().run();
                    set.getValue().setLastRan(LocalDateTime.now());
                    // DEBUG
                    if (NodeParameters.DEBUG) {
                        System.out.println("["+set.getKey().getName()+"] is run on "+ set.getValue().getLastRan());
                    }
                }
            }
        }
    }
}
