package com.p2.statemachine;

import com.p2.statemachine.iface.StateOwner;
import com.p2.statemachine.timetable.ExpiryEvent;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ExpirableState extends State {

    private Timer timer;
    private StateOwner owner;

    protected void performSpecificOnGlobalEntry(StateOwner owner, State fromState) {
        timer = new Timer();
        this.owner = owner;
        schedule();
    }

    private void schedule() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    handleExpiryEvent(owner);
                } catch (StateException e) {
                    throw new RuntimeException(e);
                }
            }
        }, getExpiryTimeInMillis());
    }

    private void handleExpiryEvent(StateOwner owner) throws StateException {
        handleEvent(owner, new ExpiryEvent());
    }

    public abstract long getExpiryTimeInMillis();


    public final void performSpecificOnGlobalExit(StateOwner owner, State fromState) {
        timer.cancel();
    }

    public final void resetExpiry() {
        timer.cancel();
        schedule();
    }
}
