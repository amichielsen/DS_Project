package be.uantwerpen.node.cron.jobs;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.cron.CronJob;

public class SendCurrentStatus extends CronJob {
    public SendCurrentStatus(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
    }

    @Override
    public void run() {

    }

    @Override
    public String getName() {
        return "SendCurrentStatusCron";
    }
}
