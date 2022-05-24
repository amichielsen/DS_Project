package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.utils.NodeParameters;

public abstract class State {
    protected final LifeCycleController lifeCycleController;
    protected NodeParameters nodeParameters = NodeParameters.getInstance();
    protected int param;

    public State(LifeCycleController lifeCycleController) {
        System.out.println("[STATE] [Info] started in mode: "+ getClass().getSimpleName());
        this.lifeCycleController = lifeCycleController;
        run();
    }

    public State(LifeCycleController lifeCycleController, int param, State oldstate){
        System.out.println("[STATE] [Info] started in mode: "+ getClass().getSimpleName());
        this.lifeCycleController = lifeCycleController;
        this.param = param;
        run();
    }

    public abstract void run();
}
