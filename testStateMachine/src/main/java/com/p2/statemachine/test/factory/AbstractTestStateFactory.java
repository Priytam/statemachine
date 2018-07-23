package com.p2.statemachine.test.factory;

import com.p2.statemachine.AbstractStateFactory;
import org.apache.log4j.Logger;

public abstract class AbstractTestStateFactory extends AbstractStateFactory {
    private static final Logger log = Logger.getLogger(AbstractTestStateFactory.class);
    private static AbstractTestStateFactory s_instance = null;

    public static AbstractTestStateFactory getInstance() {
        return s_instance;
    }

    public static void setInstance(AbstractTestStateFactory instance) {
        s_instance = instance;
    }
}
