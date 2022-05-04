package be.uantwerpen.node.lifeCycle.running;

import be.uantwerpen.node.NodeParameters;

import java.io.File;

public class FileAnalyzer extends Thread {

    public FileAnalyzer() {
    }

    public void run() {
        File dir = new File(NodeParameters.localFolder);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                // Do something with child
            }
        }
    }
}
