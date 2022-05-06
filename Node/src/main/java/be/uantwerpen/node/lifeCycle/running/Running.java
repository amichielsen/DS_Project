package be.uantwerpen.node.lifeCycle.running;



import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.cron.CronJobSchedular;
import be.uantwerpen.node.cron.jobs.PingNeighboringNode;
import be.uantwerpen.node.lifeCycle.State;
import be.uantwerpen.node.lifeCycle.running.services.FolderWatchdog;
import be.uantwerpen.node.lifeCycle.running.services.MulticastReceiver;
import be.uantwerpen.node.lifeCycle.running.services.ReplicationService;

import java.io.File;

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
        MulticastReceiver multicastReceiver = new MulticastReceiver();
        multicastReceiver.start();
        File localFolder = new File("/root/data/local");
        System.out.println(localFolder.mkdir());
        File replicaFolder = new File("/root/data/replica");
        replicaFolder.mkdir();
        FolderWatchdog folderWatchdogLocal = new FolderWatchdog(localFolder.getPath());
        folderWatchdogLocal.start();
        FolderWatchdog folderWatchdogReplica = new FolderWatchdog(replicaFolder.getPath());
        folderWatchdogReplica.start();
        FileReceiver receiver = new FileReceiver();
        receiver.start();
        CronJobSchedular cron = new CronJobSchedular(lifeCycleController);
        cron.addCronJob(new PingNeighboringNode(lifeCycleController), 1);
        //cron.addCronJob(new SendCurrentStatus(), 60);
        cron.run();
    }


}
