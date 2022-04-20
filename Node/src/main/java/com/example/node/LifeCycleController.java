package com.example.node;

import com.example.node.lifeCycle.Discovery;
import com.example.node.lifeCycle.State;

public class LifeCycleController {
    private State currentState;

    public LifeCycleController() {
        this.currentState = new Discovery(this);
    }

    public void ChangeState(State newState) {
        this.currentState = newState;
    }

    public State getCurrentState() {
        return currentState;
    }
}
