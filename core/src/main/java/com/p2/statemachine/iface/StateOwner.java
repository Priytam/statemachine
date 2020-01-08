package com.p2.statemachine.iface;

import com.p2.statemachine.AbstractStateMachine;

public interface StateOwner {
    AbstractStateMachine getStateMachine();
    Object getContext();
}
