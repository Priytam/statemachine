package com.smms.statemachine.iface;

import com.smms.memento.IMementoOriginator;
import com.smms.statemachine.State;

import java.util.Collection;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public interface IStateMachine extends IMementoOriginator<IStateMachine> {

	/**
	 * @param state The initial state of the machine and call onEntry of the initial state.
	 */
	void initState(State state);


	/**
	 * post an event to the state machine using the default machine way
	 *
	 * @param event the event to be posted to the queue
	 * @return true on success (in sync state machines it means there was actually state change)
	 */
	boolean postEvent(Object event);

	int[] getStateAsArray();

	Collection<Integer> getStateAsList();

	IState getState();

	/**
	 * Compare the state of the state machine to type
	 *
	 * @param type the desired value in State.getType()
	 * @return true if a match
	 */
	boolean validateState(int type);

	/**
	 * @param state {@link State}
	 */
	void recoverState(State state);

	/**
	 * record state entry/exit for time logging
	 *
	 * @param state {@link State}
	 */
	void recordStateEntry(State state);

	void recordStateExit(State state);

	/**
	 * Reset calculation of time in state
	 */
	void resetTimeTable();


	/**
	 * Returns the number of millisecond in a given state
	 *
	 * @param type int
	 * @return long
	 */
	long getTimeInStateMillis(int type);

}
