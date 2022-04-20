package be.uantwerpen.node;

import be.uantwerpen.node.lifeCycle.State;
import be.uantwerpen.node.lifeCycle.Discovery;

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
