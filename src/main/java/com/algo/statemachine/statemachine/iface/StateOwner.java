package com.algo.statemachine.statemachine.iface;

import com.algo.statemachine.statemachine.AbstractStateMachine;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public interface StateOwner {
    AbstractStateMachine getStateMachine();
    Object getContext();
}
