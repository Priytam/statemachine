package com.algo.statemachine.statemachine;

import java.io.Serializable;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public class TimeInState implements Serializable {
    private static final long serialVersionUID = 1L;
    private long lStateEntryTime;
    private long lStateExitTime;
    private long lStateTotalTime;


    public TimeInState(long start, long end, long total) {
        lStateEntryTime = start;
        lStateExitTime = end;
        lStateTotalTime = total;
    }

    @Override
    public String toString() {
        return "TimeInState [lStateEntryTime=" + lStateEntryTime + ", lStateExitTime=" + lStateExitTime + ", lStateTotalTime=" + lStateTotalTime + "]";
    }

    public TimeInState(long start, long end) {
        this(start, end, 0L);
    }

    public TimeInState(long start) {
        this(start, 0L, 0L);
    }

    public TimeInState() {
        this(0L, 0L, 0L);
    }

    public long getStateEntryTime() {
        return lStateEntryTime;
    }

    public void setStateEntryTime(long start) {
        lStateEntryTime = start;
    }

    public long getStateExitTime() {
        return lStateExitTime;
    }

    public void setStateExitTime(long end) {
        lStateExitTime = end;
    }

    public long getStateTotalTime() {
        return lStateTotalTime;
    }

    public void setStateTotalTime(long total) {
        lStateTotalTime = total;
    }
}
