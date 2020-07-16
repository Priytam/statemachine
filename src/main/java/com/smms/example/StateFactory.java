package com.smms.example;


import com.smms.example.state.ClosedState;
import com.smms.example.state.LockedState;
import com.smms.example.state.OpenedState;
import com.smms.statemachine.AbstractStateFactory;
import com.smms.statemachine.State;
import org.apache.log4j.Logger;

public class StateFactory extends AbstractStateFactory {
    private static final Logger log = Logger.getLogger(StateFactory.class);
    private static final StateFactory instance = new StateFactory();

    public static StateFactory getInstance() {
        return instance;
    }

    @Override
    public State getState(int iType) {
        switch (iType) {
            case DoorStates.OPENED:
                return new OpenedState(DoorStates.OPENED);
            case DoorStates.CLOSED:
                return new ClosedState(DoorStates.CLOSED);
            case DoorStates.LOCKED:
                return new LockedState(DoorStates.LOCKED);
        }
        return null;
    }
}
