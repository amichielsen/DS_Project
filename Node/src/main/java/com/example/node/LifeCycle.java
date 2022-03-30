package com.example.node;

public interface LifeCycle {
    public void Discovery();
    public void Bootstrap();
    public void Running();
    public void Shutdown();
    public void Failure();
}
