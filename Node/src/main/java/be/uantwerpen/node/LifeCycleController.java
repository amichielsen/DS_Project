package be.uantwerpen.node;

import be.uantwerpen.node.lifeCycle.State;
import be.uantwerpen.node.lifeCycle.DiscoveryBootstrap;
import be.uantwerpen.node.lifeCycle.running.Running;

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

    @Override
    public void run() {
        System.out.println("Started in DISCOVERY");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.currentState = new Running(this);
        currentState.run();
    }
}
