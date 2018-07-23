package com.p2.statemachine.test.state;

import com.p2.statemachine.State;
import com.p2.statemachine.StateException;
import com.p2.statemachine.test.event.TestWakeUpEvent;
import com.p2.statemachine.test.factory.AbstractTestStateFactory;
import org.apache.log4j.Logger;


public class TestNotInitState extends AbstractTestState {

    Logger logger = Logger.getLogger(TestNotInitState.class);
    public TestNotInitState(int iType) {
        super(iType);
    }

    @Override
    public void onEntry(Object context, State fromState) throws StateException {
        System.out.println("Inside onEntry of TestNotInitState");

    }

    @Override
    protected State onEvent(Object context, Object theEvent) throws StateException {

        System.out.println("Inside onEvent of TestNotInitState");
        if(theEvent instanceof TestWakeUpEvent) {
            return AbstractTestStateFactory.getInstance().getState(TestStateConstatnts.ST_WAITING);
        }
        return null;
    }

    @Override
    public void onExit(Object context, State toState) throws StateException {
        System.out.println("Inside onExit of TestNotInitState");

    }
}
