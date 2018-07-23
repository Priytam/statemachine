package com.p2.statemachine.test.factory;

import com.p2.statemachine.State;
import com.p2.statemachine.test.state.TestStateConstatnts;
import com.p2.statemachine.test.state.TestFinishedState;
import com.p2.statemachine.test.state.TestNotInitState;
import com.p2.statemachine.test.state.TestRunningState;
import com.p2.statemachine.test.state.TestWaitingState;
import org.apache.log4j.Logger;

public class TestStateFactory extends AbstractTestStateFactory {
    private static final Logger log = Logger.getLogger(TestStateFactory.class);

    @Override
    public State getState(int iType)
    {
        switch (iType)
        {
            case TestStateConstatnts.ST_NOT_INITILIZED:
                return new TestNotInitState(TestStateConstatnts.ST_NOT_INITILIZED);

            case TestStateConstatnts.ST_WAITING:
                return new TestWaitingState(TestStateConstatnts.ST_WAITING);

            case TestStateConstatnts.ST_RUNNING:
                return new TestRunningState(TestStateConstatnts.ST_RUNNING);

            case TestStateConstatnts.ST_STOPPED:
                return new TestFinishedState(TestStateConstatnts.ST_STOPPED);
        }
        return null;
    }
}
