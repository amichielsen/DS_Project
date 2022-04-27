package be.uantwerpen.node.lifeCycle.running;



import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.cron.CronJobScheduler;
import be.uantwerpen.node.lifeCycle.State;

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
        //MulticastReceiver multicastReceiver = new MulticastReceiver();
        //CronJobScheduler cron = new CronJobScheduler(lifeCycleController);
        //cron.addCronJob(new SendCurrentStatus(), 60);
        System.out.println(getClass());
        new MulticastReceiver().start();
        new Thread(new CronJobScheduler(lifeCycleController)).start();
        System.out.println("gets here");
    }


}
