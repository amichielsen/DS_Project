package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;

public abstract class State {
    protected LifeCycleController lifeCycleController;
    protected NodeParameters nodeParameters = NodeParameters.getInstance();

    public State(LifeCycleController lifeCycleController) {
        System.out.println("[STATE] [Info] started in mode: "+ getClass().getSimpleName());
        this.lifeCycleController = lifeCycleController;
        run();
    }

    public abstract void run();
}
