package be.uantwerpen.node.cron;

import java.time.LocalDateTime;

public class TimeDetails {
    private LocalDateTime lastRan;
    private Integer runEach;

    public TimeDetails(LocalDateTime lastRan, Integer runEach) {
        this.lastRan = lastRan;
        this.runEach = runEach;
    }

    public LocalDateTime getLastRan() {
        return lastRan;
    }

    public void setLastRan(LocalDateTime lastRan) {
        this.lastRan = lastRan;
    }

    public Integer getRunEach() {
        return runEach;
    }

    public void setRunEach(Integer runEach) {
        this.runEach = runEach;
    }
}
