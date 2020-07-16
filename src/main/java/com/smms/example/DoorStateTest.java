package com.smms.example;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class DoorStateTest {

    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }
    public static void main(String[] args) {

        // begin
        StateHandler stateHandler = new StateHandler();
        stateHandler.init();
        System.out.println("Initially locker was  -> " +  stateHandler.getState());

        // try closing locker
        stateHandler.close();
        System.out.println("Locker is  -> " +  stateHandler.getState());


        // try opening again
        stateHandler.open();
        System.out.println("Locker is  -> " +  stateHandler.getState());

        // try locking on open nothing will happen
        stateHandler.lock();
        System.out.println("Locker is  -> " +  stateHandler.getState());


        // try close and lock
        stateHandler.close();
        stateHandler.lock();
        System.out.println("Locker is  -> " +  stateHandler.getState());

        stateHandler.unlock();
        System.out.println("Locker is  -> " +  stateHandler.getState());
        stateHandler.shutDown();
    }
}
