package com.p2.statemachine;

import com.p2.memento.IMemento;
import com.p2.statemachine.iface.IStateMachine;
import com.p2.statemachine.iface.IStateOwner;
import com.p2.statemachine.timetable.StateMachineTimeTable;
import com.p2.stopwatch.IStopWatch;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;


public abstract class AbstractStateMachine implements IStateMachine, Serializable {

	private static final long serialVersionUID = -7293614171793283971L;

	private final static Logger log = Logger.getLogger(AbstractStateMachine.class);

	private State m_state;
	private Object m_context;
	private StateMachineTimeTable m_statesTimeTable = new StateMachineTimeTable();

	protected AbstractStateMachine() {
		super();
	}

	public AbstractStateMachine(Object context) {
		setContext(context);
	}

	protected void setTimeTable(StateMachineTimeTable timeTable) {
		m_statesTimeTable = timeTable;
	}

	@Override
	public int[] getStateAsArray() {
		List<Integer> lstState = getState().getExactGlobalSetType();
		int[] arr = new int[lstState.size()];
		Iterator<Integer> iterState = lstState.iterator();
		int i = 0;
		while (iterState.hasNext()) {
			Integer iState = iterState.next();
			arr[i++] = iState.intValue();
		}
		return arr;
	}

	@Override
	public Collection<Integer> getStateAsList() {
		if (null == getState()) {
			return null;
		}
		List<Integer> lstState = getState().getExactGlobalSetType();
		return lstState;
	}

	@Override
	public void initState(State state) {
		setState(state);
		try {
			getState().onGlobalEntry(getContext(), null);
		} catch (StateException e1) {
			log.error("initState() - Failed to init state for " +
					getContext() + " the current state " + state.toString());
			throw new RuntimeException("Can't init " + e1.getMessage(), e1);
		}
	}

	@Override
	public void recoverState(State state) {
		setState(state);

		Object owner = getOwner();
		if (!(owner instanceof IStateOwner)) {
			// not job definition
			return;
		}

		state.onRestore((IStateOwner) owner);
	}

	protected Object getContext() {
		return m_context;
	}

	protected void setContext(Object context) {
		m_context = context;
	}

	protected Object getOwner() {
		return getContext();
	}

	protected void setOwner(Object owner) {
		setContext(owner);
	}

	@Override
	public State getState() {
		return m_state;
	}

	protected void setState(State state) {
		m_state = state;
	}

	private boolean validateState(State state, int type) {
		if (null == state) {
			return false;
		}
		return state.validateState(type);
	}

	@Override
	public boolean validateState(int type) {
		return validateState(getState(), type);
	}

	protected State onEvent(Object event) throws StateException {
		State newState = getState().handleEvent(getContext(), event);
		if (null != newState) {
			replaceState(newState);
		}
		return newState;
	}

	private State replaceState(State newState) throws StateException {
		try {
			State oldState = getState();
			oldState.onGlobalExit(getContext(), newState);
			setState(newState);
			newState.onGlobalEntry(getContext(), oldState);
			return newState;
		} catch (StateException e) {
			log.error("replaceState() - Failed to replace state for " + getContext() +
					" the current state " + getState().toString());
			throw e;
		}
	}

	@Override
	public final void recordStateEntry(State state) {
		m_statesTimeTable.recordEntry(state.getExactStateType());
	}

	@Override
	public final void recordStateExit(State state) {
		m_statesTimeTable.recordExit(state.getExactStateType());
	}

	@Override
	public final void resetTimeTable() {
		m_statesTimeTable.restartAll();
	}

	@Override
	public final long getTimeInStateMillis(int stateType) {
		return m_statesTimeTable.getTimeInStateMillis(stateType);
	}

	public final long getTimeInCurrentStateMillis() {
		return m_statesTimeTable.getTimeInCurrentStateMillis();
	}


	public final void setTimeInCurrentStateMillis(long startTime) {
		m_statesTimeTable.setTimeInCurrentStateMillis(startTime);
	}


	private Map<Integer, IStopWatch> externalizeTimeTable() {
		return m_statesTimeTable.toMap();
	}

	private void internalizeTimeTable(Map<Integer, IStopWatch> externalRep) {
		m_statesTimeTable.fromMap(externalRep);
	}

	@Override
	public final IMemento<IStateMachine> saveToMemento() {
		Memento memento = new Memento();
		memento.setTimeTable(externalizeTimeTable());
		memento.setStates(getStateAsArray());
		return memento;
	}

	@Override
	public final void restoreFromMemento(IMemento<IStateMachine> memento) {
		if (memento == null) {
			return;
		}

		Memento concreteMemento = (Memento) memento;
		internalizeTimeTable(concreteMemento.getTimeTable());
		// TODO use state factory to restore self
	}

	private static class Memento implements IMemento<IStateMachine> {
		private static final long serialVersionUID = -8129574563345689051L;

		private Map<Integer, IStopWatch> m_timeTable = new HashMap<Integer, IStopWatch>();
		private int[] m_states = null;

		public Memento() {
			super();
		}

		public void setStates(int[] states) {
			m_states = states;
		}

		public void setTimeTable(Map<Integer, IStopWatch> timeTable) {
			m_timeTable = timeTable;
		}

		public Map<Integer, IStopWatch> getTimeTable() {
			return m_timeTable;
		}
	}

	@Override
	public String toString() {
		return "AbstractStateMachine [" + (m_state != null ? "m_state=" + m_state + ", " : "") + (m_context != null ? "m_context=" + m_context + ", " : "")
				+ (m_statesTimeTable != null ? "m_statesTimeTable=" + m_statesTimeTable : "") + "]";
	}
}
