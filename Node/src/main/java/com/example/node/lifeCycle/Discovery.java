package com.example.node.lifeCycle;

import com.example.node.LifeCycleController;

/**
 * This is the first state
 * 1. Try to find the Nameserver -> we will send 5 broadcasts
 * 2. If no answer, retry after cooldown period
 */
public class Discovery extends State {
    public Discovery(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
    }
}
