package com.smms.example;

import com.smms.example.event.CloseEvent;
import com.smms.example.event.LockEvent;
import com.smms.example.event.OpenEvent;
import com.smms.example.event.UnlockEvent;
import com.smms.statemachine.StateMachine;
import com.smms.statemachine.SyncStateMachine;

public class StateHandler {
    public static final int MAX_EVENTS = 40;
    public static final int THREAD_POOL_SIZE = 3;
    private StateMachine stateMachine;

    public boolean init() {
        stateMachine = new StateMachine(new DummyLocker(), MAX_EVENTS, THREAD_POOL_SIZE);
        stateMachine.initState(StateFactory.getInstance().getState(DoorStates.OPENED));
        return true;
    }

    public boolean open() {
        //return stateMachine.postEvent(new OpenEvent());
        try {
            return stateMachine.postEventAndWait(new OpenEvent(), 2000);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean close() {
        //return stateMachine.postEvent(new CloseEvent());
        try {
            return stateMachine.postEventAndWait(new CloseEvent(), 2000);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean lock() {
        //return stateMachine.postEvent(new LockEvent());
        try {
            return stateMachine.postEventAndWait(new LockEvent(), 2000);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean unlock() {
        //return stateMachine.postEvent(new UnlockEvent());
        try {
            return stateMachine.postEventAndWait(new UnlockEvent(), 2000);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public String getState() {
        return DoorStates.getState(stateMachine.getState().getStateType());
    }

    public void shutDown() {
        stateMachine.shutDown();
    }

    public static class DummyLocker {

    }
}
