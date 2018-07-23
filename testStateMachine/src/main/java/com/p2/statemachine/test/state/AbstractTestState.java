package com.p2.statemachine.test.state;

import com.p2.statemachine.State;
import com.p2.statemachine.StateException;
import org.apache.log4j.Logger;

public abstract class AbstractTestState extends State {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(AbstractTestState.class);

    public AbstractTestState(int iType) {
        super();
        setStateType(iType);
    }

    @Override
    public State completeHandleEvent(Object context, Object theEvent) throws StateException {
        log.info(" calling  completeHandleEvent");
        return null;
    }

    @Override
    protected void doConstantly(Object context) throws StateException {
        log.info(" doing constantly");
    }

    @Override
    protected boolean isSelfState(int stateType) {
        return stateType == getStateType();
    }

    @Override
    protected boolean isSubStateSupported(State newState) {
        return false;
    }


    @Override
    protected Object onRequest(Object context, Object theRequest) throws StateException {
        log.info(" calling  onRequest");
        return null;
    }
}
