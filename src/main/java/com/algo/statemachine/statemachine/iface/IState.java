package com.algo.statemachine.statemachine.iface;

import java.io.Serializable;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public interface IState extends Serializable, Cloneable {
	int getStateType();
}
