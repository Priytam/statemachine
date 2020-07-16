package com.smms.example.state;

import com.smms.example.DoorStates;
import com.smms.example.StateFactory;
import com.smms.example.event.CloseEvent;
import com.smms.statemachine.State;
import com.smms.statemachine.StateException;
import org.apache.log4j.Logger;

public class OpenedState extends AbstractState {
    private static final Logger log = Logger.getLogger(OpenedState.class);

    public OpenedState(int iType) {
        super(iType);
    }

    @Override
    public void onEntry(Object context, State fromState) throws StateException {
        log.info("entering in Opened state");
    }

    @Override
    protected State onEvent(Object context, Object theEvent) throws StateException {
        if (theEvent instanceof CloseEvent) {
            return StateFactory.getInstance().getState(DoorStates.CLOSED);
        }
        return null;
    }

    @Override
    public void onExit(Object context, State toState) throws StateException {
        log.info("exiting in Opened state");
    }
}
