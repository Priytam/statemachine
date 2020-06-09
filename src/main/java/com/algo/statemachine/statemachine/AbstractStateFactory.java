package com.algo.statemachine.statemachine;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public abstract class AbstractStateFactory {
    transient private static Logger log = Logger.getLogger(AbstractStateFactory.class);

    public abstract State getState(int iType);

    public State getState(List<Integer> lstReadState) {
        if (null != lstReadState) {
            if (log.isDebugEnabled()) {
                log.debug("- called with " + lstReadState);
            }

            Collections.reverse(lstReadState);

            if (log.isDebugEnabled()) {
                log.debug("- after reverse " + lstReadState);
            }

            State[] stack = new State[lstReadState.size()];

            int iStackHead = 0;
            State stMainState = null;

            for (int iIndex = 0; iIndex < lstReadState.size(); iIndex++) {
                Integer iState = lstReadState.get(iIndex);
                State st = getState(iState);

                if (null == st) {
                    log.error("- couldn't generate state for " + iState);
                    continue;
                }

                if (null != st.getVecSubStates()) {
                    st.getVecSubStates().clear();
                }

                if (log.isDebugEnabled()) {
                    log.debug("- will check for state " + st + ", iStackHead = " + iStackHead + ", iState = " + iState + ", iIndex = " + iIndex);
                }

                if (null == stMainState) {
                    stMainState = st;
                    stack[iStackHead] = st;
                    iStackHead++;
                } else {
                    int iNewStackSize = 0;
                    for (int i = iStackHead - 1; i >= 0; i--) {
                        if (log.isDebugEnabled()) {
                            log.debug(" - will check if " + st + " is a substate of " + stack[i]);
                        }

                        if (stack[i].isSubStateSupported(st)) {
                            stack[i].addSubState(st);
                            stack[i + 1] = st;
                            iNewStackSize = i + 2;
                            break;
                        }
                    }
                    iStackHead = iNewStackSize;
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("- returning " + stMainState);
            }

            return stMainState;
        }

        return null;
    }
}
