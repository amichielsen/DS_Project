package com.example.node.lifeCycle;

import com.example.node.LifeCycleController;

public abstract class State {
    protected LifeCycleController lifeCycleController;

    public State(LifeCycleController lifeCycleController) {
        this.lifeCycleController = lifeCycleController;
    }

}
