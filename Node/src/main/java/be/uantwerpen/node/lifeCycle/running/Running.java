package be.uantwerpen.node.lifeCycle.running;



import be.uantwerpen.node.lifeCycle.LifeCycleController;
import be.uantwerpen.node.utils.NodeParameters;
import be.uantwerpen.node.agents.SyncAgent;
import be.uantwerpen.node.utils.cron.CronJobSchedular;
import be.uantwerpen.node.utils.cron.jobs.PingNeighboringNode;
import be.uantwerpen.node.lifeCycle.State;
import be.uantwerpen.node.lifeCycle.running.services.FileReceiver;
import be.uantwerpen.node.lifeCycle.running.services.LocalFolderWatchdog;
import be.uantwerpen.node.lifeCycle.running.services.MulticastReceiver;

import java.io.File;
import java.util.Objects;

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
        FileReceiver receiver = new FileReceiver();
        receiver.start();
        File localFolder = new File("/root/data/local");
        localFolder.mkdirs();
        NodeParameters.localFolder = localFolder.getPath();
        File replicaFolder = new File("/root/data/replica");
        replicaFolder.mkdirs();
        NodeParameters.replicaFolder = replicaFolder.getPath();
        File[] filesList = replicaFolder.listFiles();
        assert filesList != null;
        for(File file : filesList)
                file.delete();
        FileAnalyzer.run();
        LocalFolderWatchdog folderWatchdogLocal = new LocalFolderWatchdog(localFolder.getPath());
        folderWatchdogLocal.start();
        if(Objects.equals(NodeParameters.previousID, NodeParameters.nextID)) new SyncAgent().run();
        CronJobSchedular cron = new CronJobSchedular(lifeCycleController);
        cron.addCronJob(new PingNeighboringNode(lifeCycleController), 1);
        cron.run();
    }


}
