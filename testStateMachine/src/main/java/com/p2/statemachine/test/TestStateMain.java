package com.p2.statemachine.test;

import com.p2.statemachine.State;
import com.p2.statemachine.StateMachine;
import com.p2.statemachine.test.event.TestStartEvent;
import com.p2.statemachine.test.event.TestWakeUpEvent;
import com.p2.statemachine.test.factory.AbstractTestStateFactory;
import com.p2.statemachine.test.factory.TestStateFactory;
import com.p2.statemachine.test.state.TestStateConstatnts;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TestStateMain {

    public static void main(String[] args) {
        AbstractTestStateFactory.setInstance(new TestStateFactory());
        StateMachine stateMachine = new StateMachine(new Object(), 1000, 5);
        stateMachine.initState(AbstractTestStateFactory.getInstance().getState(TestStateConstatnts.ST_NOT_INITILIZED));
        stateMachine.postEvent(new TestWakeUpEvent());
        stateMachine.postEvent(new TestStartEvent());

        /*
         *
         *Test system timings
         * */

        try {
            Thread.sleep(1000 * 5);
        } catch (Exception e) {

        }
        long timeInCurrentStateMillis = stateMachine.getTimeInCurrentStateMillis();
        System.out.println(System.currentTimeMillis() - timeInCurrentStateMillis);
        long timeInStateMillis = stateMachine.getTimeInStateMillis(TestStateConstatnts.ST_WAITING);
        System.out.println(timeInStateMillis);


        /*
         * Test recovery of state
         * */

        List<Integer> stateAsList = (List<Integer>) stateMachine.getStateAsList();
        StateMachine newStateMachine = new StateMachine(new Object(), 1000, 5);
        State state = AbstractTestStateFactory.getInstance().getStateAfterReadingFromPersistency(stateAsList);
        newStateMachine.recoverState(state);
        newStateMachine.postEvent(new TestStartEvent());

    }
}
