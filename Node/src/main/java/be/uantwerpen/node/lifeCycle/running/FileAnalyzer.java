package be.uantwerpen.node.lifeCycle.running;

import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.lifeCycle.running.services.ReplicationService;

import java.io.File;

/**
 * Responsible for checking files present on system at startup
 * Hands them over to replication service for further processing
 */
public class FileAnalyzer extends Thread {

    public FileAnalyzer() {
    }

    public void run() {
        File dir = new File(NodeParameters.localFolder);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                ReplicationService replicationService = new ReplicationService(child.toPath());
                replicationService.start();
            }
        }
    }
}
