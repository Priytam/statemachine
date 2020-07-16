package com.smms.example.state;

import com.smms.statemachine.State;
import org.apache.log4j.Logger;

public abstract class AbstractState extends State {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(AbstractState.class);

    public AbstractState(int iType) {
        super();
        setStateType(iType);
    }

    @Override
    public State completeHandleEvent(Object context, Object theEvent) {
        log.info(" calling  completeHandleEvent");
        return null;
    }

    @Override
    protected void doConstantly(Object context) {
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
    protected Object onRequest(Object context, Object theRequest) {
        log.info(" calling  onRequest");
        return null;
    }
}
