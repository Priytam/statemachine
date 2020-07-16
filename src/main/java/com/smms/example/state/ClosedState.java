package com.smms.example.state;

import com.smms.example.DoorStates;
import com.smms.example.StateFactory;
import com.smms.example.event.LockEvent;
import com.smms.example.event.OpenEvent;
import com.smms.statemachine.State;
import com.smms.statemachine.StateException;
import org.apache.log4j.Logger;

public class ClosedState extends AbstractState {
    private static final Logger log = Logger.getLogger(ClosedState.class);

    public ClosedState(int iType) {
        super(iType);
    }

    @Override
    public void onEntry(Object context, State fromState) throws StateException {
        log.info("entering in Closed state");
    }

    @Override
    protected State onEvent(Object context, Object theEvent) throws StateException {
        if (theEvent instanceof LockEvent) {
            return StateFactory.getInstance().getState(DoorStates.LOCKED);
        }
        if (theEvent instanceof OpenEvent) {
            return StateFactory.getInstance().getState(DoorStates.OPENED);
        }
        return null;
    }

    @Override
    public void onExit(Object context, State toState) throws StateException {
        log.info("exiting in Locked state");
    }
}
