package com.smms.statemachine;

import com.smms.memento.IMemento;
import com.smms.statemachine.iface.IStateMachine;
import com.smms.statemachine.iface.StateOwner;
import com.smms.statemachine.timetable.StateMachineTimeTable;
import com.smms.stopwatch.IStopWatch;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public abstract class AbstractStateMachine implements IStateMachine, Serializable {

    private static final long serialVersionUID = -7293614171793283971L;
    private final static Logger log = Logger.getLogger(AbstractStateMachine.class);
    private State state;
    private Object context;
    private StateMachineTimeTable statesTimeTable = new StateMachineTimeTable();
    private StateOwner owner;

    protected AbstractStateMachine() {
        super();
    }

    public AbstractStateMachine(Object context) {
        setContext(context);
        AbstractStateMachine machine = this;
        setOwner(new StateOwner() {
            @Override
            public AbstractStateMachine getStateMachine() {
                return machine;
            }

            @Override
            public Object getContext() {
                return context;
            }
        });
    }

    protected void setTimeTable(StateMachineTimeTable timeTable) {
        statesTimeTable = timeTable;
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
            getState().onGlobalEntry(getOwner(), null);
        } catch (StateException e1) {
            log.error("initState() - Failed to init state for " + getContext() + " the current state " + state.toString());
            throw new RuntimeException("Can't init " + e1.getMessage(), e1);
        }
    }

    @Override
    public void recoverState(State state) {
        setState(state);
        state.onRestore(getOwner());
    }

    protected Object getContext() {
        return context;
    }

    protected void setContext(Object context) {
        this.context = context;
    }

    protected StateOwner getOwner() {
        return owner;
    }

    protected void setOwner(StateOwner owner) {
        this.owner = owner;
    }

    @Override
    public State getState() {
        return state;
    }

    protected void setState(State state) {
        this.state = state;
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
        State newState = getState().handleEvent(getOwner(), event);
        if (null != newState) {
            replaceState(newState);
        }
        return newState;
    }

    private State replaceState(State newState) throws StateException {
        try {
            State oldState = getState();
            oldState.onGlobalExit(getOwner(), newState);
            setState(newState);
            newState.onGlobalEntry(getOwner(), oldState);
            return newState;
        } catch (StateException e) {
            log.error("replaceState() - Failed to replace state for " + getContext() +
                    " the current state " + getState().toString());
            throw e;
        }
    }

    @Override
    public final void recordStateEntry(State state) {
        statesTimeTable.recordEntry(state.getExactStateType());
    }

    @Override
    public final void recordStateExit(State state) {
        statesTimeTable.recordExit(state.getExactStateType());
    }

    @Override
    public final void resetTimeTable() {
        statesTimeTable.restartAll();
    }

    @Override
    public final long getTimeInStateMillis(int stateType) {
        return statesTimeTable.getTimeInStateMillis(stateType);
    }

    public final long getStartTimeInCurrentStateMillis() {
        return statesTimeTable.getStartTimeInCurrentStateMillis();
    }


    public final void setStartTimeInCurrentStateMillis(long startTime) {
        statesTimeTable.setStartTimeInCurrentStateMillis(startTime);
    }

    private Map<Integer, IStopWatch> externalizeTimeTable() {
        return statesTimeTable.toMap();
    }

    private void internalizeTimeTable(Map<Integer, IStopWatch> externalRep) {
        statesTimeTable.fromMap(externalRep);
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
        return "AbstractStateMachine [" + (state != null ? "state=" + state + ", " : "") + (context != null ? "context=" + context + ", " : "")
                + (statesTimeTable != null ? "statesTimeTable=" + statesTimeTable : "") + "]";
    }
}
