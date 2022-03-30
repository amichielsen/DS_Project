package com.example.node.lifeCycle;

import com.example.node.LifeCycleController;

/**
 * The failure mode.
 * 1. If we detect a failure we will get here and report to the NameServer.
 * 2. Find a new route!!!
 */
public class Failure extends State {
    public Failure(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
    }
}
