package be.uantwerpen.node;

import be.uantwerpen.node.lifeCycle.State;
import be.uantwerpen.node.lifeCycle.DiscoveryBootstrap;

public class LifeCycleController implements Runnable {
    private State currentState;

    public LifeCycleController() {

    }

    public void ChangeState(State newState) {
        this.currentState = newState;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void run() {
        this.currentState = new DiscoveryBootstrap(this);
        currentState.run();
    }
}
