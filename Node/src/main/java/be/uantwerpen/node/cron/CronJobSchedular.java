package be.uantwerpen.node.cron;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.lifeCycle.State;

public class CronJobSchedular {
    private State stateStarted;
    private LifeCycleController lifeCycleController;
    public CronJobSchedular(LifeCycleController lifeCycleController) {
        this.lifeCycleController = lifeCycleController;
        this.stateStarted = lifeCycleController.getCurrentState();
    }

    public void addCronJob(CronJobHandler job, Integer timeInBetweenJobs) {

    }
    public void run() {
        while (stateStarted == lifeCycleController.getCurrentState()) {
            //System.out.println("Cron has run");
        }
    }
}
