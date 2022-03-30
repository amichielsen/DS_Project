package com.example.node.lifeCycle;

import com.example.node.LifeCycleController;

/**
 * This is the second state
 * 1. Nameserver will provide us with the parameters of our neighboring nodes
 * 2. We will contact these nodes. If one or more does not respond, we will notice the NameServer.
 * 3. The 2 nodes will be adopting us in the network.
 */
public class Bootstrap extends State {
    public Bootstrap(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
    }
}
