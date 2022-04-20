package com.example.node.cron;

public abstract class CronJobHandler implements Runnable {
    public CronJobHandler() {
    }

    abstract public void run();
}
