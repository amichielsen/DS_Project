package be.uantwerpen.node.agents;

import be.uantwerpen.node.NodeParameters;

import java.util.Map;

public class FailureAgent extends Agent {
    private int failedNode;
    public FailureAgent(int failedNode) {
        this.failedNode = failedNode;
    }

    @Override
    public void run() {
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
