package com.example.node.lifeCycle.running;



import com.example.node.LifeCycleController;
import com.example.node.cron.CronJobSchedular;
import com.example.node.cron.PingNeighboringNode;
import com.example.node.cron.SendCurrentStatus;
import com.example.node.lifeCycle.State;

/**
 * This is the "main" running state.
 * 1. We will respond to REST requests.
 * 2. We will adopt new nodes that come on our network.
 * 3. We will report failing nodes to the NameServer.
 * ! Send periodic pings to the neighbors to test their state
 * ! Ask periodic to the nameserver for the IP of our neighbors
 */

public class Running extends State {
    public Running(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
    }

    @Override
    public void run() {
        CronJobSchedular cron = new CronJobSchedular(lifeCycleController);
        cron.addCronJob(new PingNeighboringNode(), 60);
        cron.addCronJob(new SendCurrentStatus(), 60);
    }


}
