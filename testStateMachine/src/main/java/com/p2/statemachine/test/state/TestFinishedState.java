package com.p2.statemachine.test.state;

import com.p2.statemachine.State;
import com.p2.statemachine.StateException;

public class TestFinishedState extends AbstractTestState {

    public TestFinishedState(int iType) {
        super(iType);
    }

    @Override
    public void onEntry(Object context, State fromState) throws StateException {

    }

    @Override
    protected State onEvent(Object context, Object theEvent) throws StateException {
        return null;
    }

    @Override
    public void onExit(Object context, State toState) throws StateException {

    }
}
