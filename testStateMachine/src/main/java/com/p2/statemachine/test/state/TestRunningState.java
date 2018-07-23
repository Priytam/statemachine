package com.p2.statemachine.test.state;

import com.p2.statemachine.State;
import com.p2.statemachine.StateException;

public class TestRunningState extends AbstractTestState {

    public TestRunningState(int iType) {
        super(iType);
    }

    @Override
    public void onEntry(Object context, State fromState) throws StateException {
        System.out.println("Inside onEntry of Running state");

    }

    @Override
    protected State onEvent(Object context, Object theEvent) throws StateException {
        System.out.println("Inside of onEvent of Running state");
        return null;
    }

    @Override
    public void onExit(Object context, State toState) throws StateException {

    }
}
