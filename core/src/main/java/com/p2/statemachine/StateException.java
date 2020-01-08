package com.p2.statemachine;


/**
 * Definition:
 * StateException define a exception that occured while performing
 * an action on the state.
 */
public class StateException extends Exception {
	private final static long serialVersionUID = 7324289884764967860L;
	private State stateNewState = null;
	public Throwable detail;

	/**
	 * StateException constructor comment.
	 */
	public StateException() {
		super();
	}

	/**
	 * Insert the method's description here.
	 *
	 * @param newState
	 * @param messgae  java.lang.String
	 */
	public StateException(State newState, String messgae) {
		super(messgae);
		setNewState(newState);
	}

	/**
	 * Insert the method's description here.
	 *
	 * @param str java.lang.String
	 */
	public StateException(String str) {
		super(str);
	}

	/**
	 * Insert the method's description here.
	 *
	 * @param t       java.lang.Throwable
	 * @param message java.lang.String
	 */
	public StateException(Throwable t, String message) {
		super(message, t);
		detail = t;
	}

	/**
	 * Insert the method's description here.
	 *
	 * @return State
	 */
	public State getNewState() {
		return stateNewState;
	}

	/**
	 * Insert the method's description here.
	 *
	 * @param newNewState State
	 */
	public void setNewState(State newNewState) {
		stateNewState = newNewState;
	}
}
