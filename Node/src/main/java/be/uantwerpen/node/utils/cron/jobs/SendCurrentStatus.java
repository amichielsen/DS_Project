package be.uantwerpen.node.utils.cron.jobs;

import be.uantwerpen.node.lifeCycle.LifeCycleController;
import be.uantwerpen.node.utils.cron.CronJob;

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
