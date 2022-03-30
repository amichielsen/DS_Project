package com.example.node.lifeCycle;

import com.example.node.LifeCycleController;
import com.example.node.NodeParameters;

public abstract class State {
    protected LifeCycleController lifeCycleController;
    protected NodeParameters nodeParameters = new NodeParameters();

    public State(LifeCycleController lifeCycleController) {
        this.lifeCycleController = lifeCycleController;
    }

}
