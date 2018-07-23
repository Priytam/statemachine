package com.p2.statemachine.iface;

import java.io.Serializable;

public interface IState extends Serializable, Cloneable {
	int getStateType();
}
