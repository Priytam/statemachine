package com.p2.statemachine.test.state;

import com.p2.statemachine.State;
import com.p2.statemachine.StateException;
import com.p2.statemachine.test.event.TestFaliedEvent;
import com.p2.statemachine.test.event.TestStartEvent;
import com.p2.statemachine.test.factory.AbstractTestStateFactory;
import org.apache.log4j.Logger;

public class TestWaitingState extends AbstractTestState {

    Logger logger = Logger.getLogger(TestWaitingState.class);

    public TestWaitingState(int iType) {
        super(iType);
    }

    @Override
    public void onEntry(Object context, State fromState) throws StateException {
        System.out.println("Inside onEntry of TestWaitingState");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {


        }
        System.out.println("done");
    }

    @Override
    protected State onEvent(Object context, Object theEvent) throws StateException {

        System.out.println("Inside onEvent of TestWaitingState");
        if(theEvent instanceof TestStartEvent) {
            return AbstractTestStateFactory.getInstance().getState(TestStateConstatnts.ST_RUNNING);
        }
        if(theEvent instanceof TestFaliedEvent) {
            return AbstractTestStateFactory.getInstance().getState(TestStateConstatnts.ST_NOT_INITILIZED);
        }
        return null;
    }

    @Override
    public void onExit(Object context, State toState) throws StateException {
        System.out.println("Inside onExit of TestWaitingState");

    }
}
