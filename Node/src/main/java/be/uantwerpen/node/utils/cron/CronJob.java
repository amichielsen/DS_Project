package be.uantwerpen.node.utils.cron;

import be.uantwerpen.node.lifeCycle.LifeCycleController;

public abstract class CronJob implements Runnable {
    protected LifeCycleController lifeCycleController;
    public CronJob(LifeCycleController lifeCycleController) {
        this.lifeCycleController = lifeCycleController;
    }

    abstract public void run();

    abstract public String getName();
}
