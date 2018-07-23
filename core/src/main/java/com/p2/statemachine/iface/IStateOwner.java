package com.p2.statemachine.iface;

public interface IStateOwner {
	IStateMachine getStateMachine();

	boolean validateState(int state);
}
