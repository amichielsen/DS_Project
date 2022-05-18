package be.uantwerpen.node.agents;

import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.fileSystem.FileParameters;
import be.uantwerpen.node.fileSystem.FileSystem;

import java.util.Map;
import java.util.stream.Collectors;

public class FailureAgent extends Agent {
    private int failedNode;
    public FailureAgent(int failedNode) {
        this.failedNode = failedNode;
    }

    @Override
    public void run() {



        // File has been uploaded to this node, is not a matching hash -> replicated instance has failed
        // 1. Find the new correct node
        // 2. Send the file to that instance
        // 3. Update list
        FileSystem.getLocalFiles().entrySet()
                                .stream()
                                .filter( e -> e.getValue().getReplicatedOnNode() == failedNode)
                                .forEach();


        for (Map.Entry<String, FileParameters> local : FileSystem.getLocalFiles().entrySet()) {

        }



        // File has been replicated to this node, is a matching hash -> local instance has failed
        // 1. Remove file
        for (Map.Entry<String, FileParameters> local : FileSystem.getLocalFiles().entrySet()) {

        }


        // File has been downloaded to this node -> delete

        /*
        for (Map.Entry<String, Map<String, Integer>> file : NodeParameters.bookkeeper.entrySet()) {
            for (Map.Entry<String, Integer> entry : file.getValue().entrySet()) {

                // File has been uploaded to this node, is not a matching hash -> replicated instance has failed
                // 1. Find the new correct node
                // 2. Send the file to that instance
                // 3. Update
                if (entry.getValue() == failedNode && entry.getKey() == "Local") {

                }

                else if (entry.getValue() == failedNode && entry.getKey() == "Replica") {

                }
            }

            System.out.println(file);
        }

         */

    }
}
