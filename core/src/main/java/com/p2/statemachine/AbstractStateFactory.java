package com.p2.statemachine;

import org.apache.log4j.Logger;

/**
 *
 */
public abstract class AbstractStateFactory {
	transient private static Logger log = Logger.getLogger(AbstractStateFactory.class);

	public abstract State getState(int iType);
}
