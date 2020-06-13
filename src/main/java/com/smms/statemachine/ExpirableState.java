package com.smms.statemachine;


import com.smms.statemachine.iface.StateOwner;
import com.smms.statemachine.timetable.ExpiryEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public abstract class ExpirableState extends State {

    private Timer timer;
    private StateOwner owner;

    protected void performSpecificOnGlobalEntry(StateOwner owner, State fromState) {
        timer = new Timer();
        this.owner = owner;
        schedule(0);
    }

    private void schedule(long millisSpent) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    handleExpiryEvent(owner);
                } catch (StateException e) {
                    throw new RuntimeException(e);
                }
            }
        }, getExpiryTimeInMillis() - millisSpent);
    }

    private void handleExpiryEvent(StateOwner owner) throws StateException {
        for (Object oState : vecSubStates) {
            State state = (State) oState;
            state.handleEvent(owner, new ExpiryEvent());
        }
        handleEvent(owner, new ExpiryEvent());
    }

    public abstract long getExpiryTimeInMillis();


    public final void performSpecificOnGlobalExit(StateOwner owner, State fromState) {
        timer.cancel();
    }

    public final void resetExpiry() {
        timer.cancel();
        schedule(0);
    }

    public void onRestore(StateOwner owner) {
        super.onRestore(owner);
        try {
            long timeSpent = System.currentTimeMillis() - owner.getStateMachine().getStartTimeInCurrentStateMillis();
            if (getExpiryTimeInMillis() - timeSpent <= 0) {
                handleExpiryEvent(owner);
            }
            schedule(timeSpent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
