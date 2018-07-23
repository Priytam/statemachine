package com.p2.statemachine;

import com.p2.statemachine.iface.IState;
import com.p2.statemachine.iface.IStateMachine;
import com.p2.statemachine.iface.IStateOwner;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Definition:
 * A state represents a condition or situation during the 
 * life of an object during which it satisfies some condition 
 * or waits for some event. Each state represents the cumulative 
 * history of its behavior.
 *
 * Role: 
 * it's rule is to ensure correctance behaviour.
 * 
 *
 * Responsability:
 * it's responsability is to know what are the action to be act
 *  in diffrent event, the state should know according to event
 *  what is the right state to move to. The state should know who
 *  are the sub-state that he can hold. 
 *  The state should performe some action when it enter/exit from 
 *  the state.
 * 
 */
abstract public class State implements IState {
	transient private static Logger log = Logger.getLogger(State.class);
	// holding all sub states
	protected Vector<State> m_vecSubStates = new Vector<State>();
	private final static long serialVersionUID = 3338576081780974136L;
	private int m_iStateType;

	/**
	 * State constructor
	 */
	public State() {
		super();
	}

	/**
	 * add state to sub state vector
	 *
	 * @return :
	 * @throws :
	 * @param: State
	 * @see :
	 */
	public void addSubState(State subState) {
		m_vecSubStates.addElement(subState);
	}

	/**
	 * Description; change the state to new state.
	 * this method call onGlobalExit from the current state, change
	 * the state to new state and then call onGlobalEntry on the new state.
	 * As a result of this call the state will be updated with the new state
	 * <p>
	 *
	 * @param context  Object
	 * @param newState
	 * @param index    int
	 */
	public void changeState(Object context, State newState, int index) throws StateException {

		if (log.isDebugEnabled()) {
			log.debug("changeState: current="
					+ this.toString()
					+ " replace state to" + newState.toString());
		}

		State oldState = m_vecSubStates.get(index);
		oldState.onGlobalExit(context, newState);
		m_vecSubStates.set(index, newState);
		newState.onGlobalEntry(context, oldState);
	}

	/**
	 * clone - clone state vector.
	 *
	 * @return java.lang.Object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		Object o = null;
		try {
			o = super.clone();
			((State) o).m_vecSubStates = (Vector<State>) m_vecSubStates.clone();
		} catch (CloneNotSupportedException ex) {
			log.warn("clone(): CloneNotSupported");
		}
		return o;
	}

	/**
	 * do something after all sub states has been changed.
	 * the method returns a new state if it needs to be replaced.
	 *
	 * @return :
	 * @throws :
	 * @param:
	 * @see :
	 */
	abstract public State completeHandleEvent(Object context, Object theEvent) throws StateException;

	/**
	 * delete a state from then vector
	 */
	public void deleteSubState(State theState) {
		m_vecSubStates.removeElement(theState);
	}

	/**
	 * The task must be performed while in the state.
	 * <p>
	 * Each inherint state should decide what
	 */
	protected abstract void doConstantly(Object context) throws StateException;

	/**
	 * Insert the method's description here.
	 *
	 * @return int[]
	 */
	public List<Integer> getGlobalSetType() {
		if (log.isDebugEnabled() ) {
			log.debug("getGlobalSetType: " + this.toString());
		}
		List<Integer> l = new ArrayList<Integer>();
		List<Integer> lSubState;
		// do on exit from the current state and go on the vector
		// go through the vector and call to on Exit
		for (int i = 0; i < m_vecSubStates.size(); i++) {
			lSubState = m_vecSubStates.elementAt(i).getGlobalSetType();
			l.addAll(lSubState);
		}
		Integer i = Integer.valueOf(getStateType());
		l.add(i);
		return l;
	}

	/**
	 * Insert the method's description here.
	 *
	 * @return int
	 */
	@Override
	public int getStateType() {
		return m_iStateType;
	}

	/**
	 * This method returns the sub-states vector
	 *
	 * @return : java.util.Vector
	 * @throws :
	 * @param:
	 * @see :
	 */
	public Vector<State> getVecSubStates() {
		return m_vecSubStates;
	}

	/**
	 * Description:
	 * handle event get an event, and check what is the action that need to
	 * be take according to the result of the onEvent(), if the onEvent return
	 * new state it means that we need to change the state to the new state, before
	 * doing so we should check if the substate is supported by us.
	 *
	 * @param context  java.lang.Object
	 * @param owner
	 * @param event    java.lang.Object
	 * @param index    int
	 * @return State
	 */
	protected State handleEvent(Object context, State owner, Object event, int index) throws StateException {
		if (log.isDebugEnabled()) {
			log.debug("handleEvent (context,owner,event,index) - "
					+ this.toString() + ": event=" + event.toString());
		}
		Vector<State> vecSubStates = new Vector<State>(m_vecSubStates);
		State newState = null;
		try {
			// check if the current state now what to do with this event.
			newState = onEvent(context, event);
		} catch (StateException e) {
			log.info("Exception: got exception from " +
					this.toString() + "(onEvent): " + event + " exception: " + e + " context: " + context);
			throw e;
			//return null;
		}
		// if the new state is null it means that you need to go throw
		// our sub state
		if (null == newState) {
			// go recursive till some now what to do with
			// the new state
			Enumeration<State> e = vecSubStates.elements();
			int subIndex = 0;
			while (e.hasMoreElements()) {
				State subState = e.nextElement();
				newState = subState.handleEvent(context, this, event, subIndex);
				if (log.isDebugEnabled()) {
					log.debug("handleEvent(4 params): checking vector - "
							+ subState.toString());
				}
				if (null != newState) {
					// substate return a state check if this state
					// is supported by us if we do change the state.
					if (isSubStateSupported(newState)) {
						changeState(context, newState, subIndex);
					}
					// return the new state.
					else {
						return newState;
					}
				}
				subIndex++;
			}
			// no new state.
			try {
				newState = completeHandleEvent(context, event);
			} catch (StateException ex) {
				log.info("Exception: got exception from " +
						this.toString() + "(completeHandleEvent): " + ex);
				return null;
			}
			return newState;
		}
		// get a new state by sub state check if this state is supported
		// by us if we de change the state
		else {
			return newState;
		}
	}

	/**
	 * Definition:
	 * This method is for the StateOwner.
	 * <p>
	 * Creation date: (03/15/2001 3:06:49 PM)
	 *
	 * @param context java.lang.Object
	 * @param event
	 */
	public State handleEvent(Object context, Object event) throws StateException {
		if (log.isDebugEnabled()) {
			log.debug("handleEvent (context,event) - "
					+ this.toString() + ": event="
					+ event.toString());
		}

		Vector<State> vecSubStates = new Vector<State>(m_vecSubStates);
		State newState = null;
		try {
			// check if the current state know what to do with this event.
			newState = onEvent(context, event);
		} catch (StateException e) {
			log.info("Exception: got exception from " +
					this.toString() + "(onEvent): " + e);
			throw e;
		}
		if (null != newState) {
			return newState;
		}

		Enumeration<State> e = vecSubStates.elements();
		int subIndex = 0;
		while (e.hasMoreElements()) {
			State subState = e.nextElement();
			newState = subState.handleEvent(context, this, event, subIndex);
			if (null != newState) {
				if (isSubStateSupported(newState)) {
					changeState(context, newState, subIndex);
				} else {
					return newState;
				}
			}
			subIndex++;
		}
		// no new state.
		try {
			newState = completeHandleEvent(context, event);
		} catch (StateException ex) {
			log.info("Exception: got exception from " +
					this.toString() + "(completeHandleEvent): " + ex);
			return null;
		}
		return newState;
	}

	/**
	 * Description:
	 * handle request method is a recursive method that get a request
	 * and return the result in Object. - each state should implement the
	 * onRequest.
	 *
	 * @param context java.lang.Object
	 * @param owner
	 */
	public Object handleRequest(Object context, State owner, Object request) {
		Object answer = null;
		try {
			// check if we know the answer.
			answer = onRequest(context, request);
		} catch (Exception e) {
			log.error("handleRequest() - get exception while handle request", e);
		}
		if (null == answer) {
			// go recursive till some one knows how to handle the request.
			Enumeration<State> e = m_vecSubStates.elements();
			while (e.hasMoreElements()) {
				State subState = e.nextElement();
				answer = subState.handleRequest(context, this, request);
				if (null != answer) {
					return answer;
				}
			}
			return null;
		} else {
			return answer;
		}
	}

	/**
	 * validate the current state return true if the current type(our type)
	 * is equal to required type.
	 *
	 * @param stateType int
	 * @return boolean
	 */
	protected abstract boolean isSelfState(int stateType);

	/**
	 * return true if the newState can be one of the subStates.
	 * Creation date: (03/18/2001 2:47:06 PM)
	 *
	 * @param newState
	 * @return boolean
	 */
	abstract protected boolean isSubStateSupported(State newState);

	/**
	 * The "task" must be performed when enter to this state.
	 *
	 * @param fromState
	 */
	public abstract void onEntry(Object context, State fromState) throws StateException;

	/**
	 * performe a "task" when getting an event, not all event should be takecare.
	 * if according to the event it needs to replace the state it return the new state.
	 */
	protected abstract State onEvent(Object context, Object theEvent) throws StateException;

	/**
	 * The "task" must be performed when exit from this state.*
	 * @param toState
	 */
	public abstract void onExit(Object context, State toState) throws StateException;

	/**
	 * call recursive to onEntry.*
	 * @param fromState
	 * @param context
	 */
	public final void onGlobalEntry(Object context, State fromState) throws StateException {
		if (log.isDebugEnabled()) {
			log.debug("onGlobalEntry: " + this.toString() + " context: " + context);
		}

		// update state machine with entry/exit
		// TODO remove this RTTI for better performance
		if (context instanceof IStateOwner) {
			IStateOwner stateOwner = (IStateOwner) context;
			IStateMachine stateMachine = stateOwner.getStateMachine();
			stateMachine.recordStateEntry(this);
		}

		// do on exit from the current state and go on the vector
		onEntry(context, fromState);
		// go through the vector and call to on Exit
		for (int i = 0; i < m_vecSubStates.size(); i++) {
			m_vecSubStates.elementAt(i).onGlobalEntry(context, fromState);
		}
	}

	/**
	 * Call recursive to onExit
	 *
	 * @param newState
	 * @param context
	 */
	public final void onGlobalExit(Object context, State newState) throws StateException {
		if (log.isDebugEnabled()) {
			log.debug("onGlobalExit: " + this.toString() + " context " + context);
		}
		// go recursivlly down the vector
		for (int i = 0; i < m_vecSubStates.size(); i++) {
			m_vecSubStates.elementAt(i).onGlobalExit(context, newState);
		}

		// do "onExit" from the current state
		onExit(context, newState);

		// update state machine with entry/exit
		// TODO remove this RTTI better performance
		if (context instanceof IStateOwner) {
			IStateOwner stateOwner = (IStateOwner) context;
			IStateMachine stateMachine = stateOwner.getStateMachine();
			stateMachine.recordStateExit(this);
		}
	}

	public void onRestore(IStateOwner owner) {
		for (Object oState : m_vecSubStates) {
			State state = (State) oState;
			state.onRestore(owner);
		}
		IStateMachine stateMachine = owner.getStateMachine();
		stateMachine.recordStateEntry(this);
	}

	protected abstract Object onRequest(Object context, Object theRequest) throws StateException;

	public void setStateType(int newStateType) {
		m_iStateType = newStateType;
	}

	public void setSubState(State newState) {
		m_vecSubStates.removeAllElements();
		m_vecSubStates.addElement(newState);
	}

	public void setVecSubStates(Vector<State> newVecSubStates) {
		m_vecSubStates = newVecSubStates;
	}

	@Override
	public String toString() {
		// check our state and if false go recursive to sub state and check
		StringBuilder val = new StringBuilder(this.getClass().getName());
		for (int i = 0; i < m_vecSubStates.size(); i++) {
			val.append("/" + m_vecSubStates.elementAt(i).toString());
		}
		return val.toString();
	}

	public String toStringShort() {
		// check our state and if false go recursive to sub state and check
		StringBuilder val = new StringBuilder(toShortName(this.getClass().getName()));
		for (int i = 0; i < m_vecSubStates.size(); i++) {
			val.append("/" + toShortName(m_vecSubStates.elementAt(i).toString()));
		}
		return val.toString();
	}

	private String toShortName(String name) {
		if (name.contains(".")) {
			return name.substring(name.lastIndexOf(".") + 1);
		}
		return name;
	}

	/**
	 * recursively method that validate if current state or one of the
	 * sub states is the requested type.
	 * @param stateType int
	 * @return boolean
	 */
	public boolean validateState(int stateType) {
		// check our state and if false go recursive to sub state and check
		if (isSelfState(stateType)) {
			return true;
		}
		for (int i = 0; i < m_vecSubStates.size(); i++) {
			boolean retVal = m_vecSubStates.elementAt(i).validateState(stateType);
			if (true == retVal) {
				return true;
			}
		}
		return false;
	}

	public final void onEntry(Object context) throws StateException {
	}

	public final void onExit(Object context) throws StateException {
	}

	public List<Integer> getExactGlobalSetType() {
		List<Integer> l = new ArrayList<Integer>();
		List<Integer> lsubstate;
		// do on exit from the current state and go on the vector
		// go through the vector and call to on Exit
		for (int i = 0; i < m_vecSubStates.size(); i++) {
			lsubstate = m_vecSubStates.elementAt(i).getExactGlobalSetType();
			l.addAll(lsubstate);
		}
		Integer i = Integer.valueOf(getExactStateType());
		l.add(i);
		return l;
	}

	/**
	 * Some states do not have a unique type, so here we allow them to specify
	 * a unique id, that will allow their re-construction when coming up from
	 * persistency.
	 * States that do not have a unique type should override this method.
	 *
	 * @return
	 */
	protected int getExactStateType() {
		return getStateType();
	}

}
