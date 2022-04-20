package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;

public abstract class State {
    protected LifeCycleController lifeCycleController;
    protected NodeParameters nodeParameters = new NodeParameters();

    public State(LifeCycleController lifeCycleController) {
        this.lifeCycleController = lifeCycleController;
        run();
    }

    public abstract void run();

}
