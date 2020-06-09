package com.algo.statemachine.statemachine;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
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
	 * @param message  java.lang.String
	 */
	public StateException(State newState, String message) {
		super(message);
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
	 * @param newState State
	 */
	public void setNewState(State newState) {
		stateNewState = newState;
	}
}
