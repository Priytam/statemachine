package com.p2.statemachine.test;

import com.p2.statemachine.StateMachine;
import com.p2.statemachine.test.event.TestStartEvent;
import com.p2.statemachine.test.event.TestWakeUpEvent;
import com.p2.statemachine.test.factory.AbstractTestStateFactory;
import com.p2.statemachine.test.factory.TestStateFactory;
import com.p2.statemachine.test.state.TestStateConstatnts;

public class TestStateMain {

    public static void main(String[] args) {
        AbstractTestStateFactory.setInstance(new TestStateFactory());
        StateMachine stateMachine = new StateMachine(new Object(), 1000, 5);
        stateMachine.initState(AbstractTestStateFactory.getInstance().getState(TestStateConstatnts.ST_NOT_INITILIZED));
        stateMachine.postEvent(new TestWakeUpEvent());
        stateMachine.postEvent(new TestStartEvent());
    }
}
