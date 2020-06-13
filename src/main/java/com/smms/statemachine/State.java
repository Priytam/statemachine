package com.smms.statemachine;

import com.smms.statemachine.iface.IState;
import com.smms.statemachine.iface.IStateMachine;
import com.smms.statemachine.iface.StateOwner;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
/**
 * Definition:
 * A state represents a condition or situation during the
 * life of an object during which it satisfies some condition
 * or waits for some event. Each state represents the cumulative
 * history of its behavior.
 * <p>
 * Role:
 * it's rule is to ensure conductance behaviour.
 * <p>
 * <p>
 * Responsibility:
 * it's responsibility is to know what are the action to be act
 * in different event, the state should know according to event
 * what is the right state to move to. The state should know who
 * are the sub-state that he can hold.
 * The state should perform some action when it enter/exit from
 * the state.
 */
abstract public class State implements IState {
    final transient private static Logger log = Logger.getLogger(State.class);
    private final static long serialVersionUID = 3338576081780974136L;
    // holding all sub states
    protected Vector<State> vecSubStates = new Vector<State>();
    private int iStateType;

    /**
     * State constructor
     */
    public State() {
        super();
    }

    /**
     * add state to sub state vector
     *
     * @param subState State
     */
    public void addSubState(State subState) {
        vecSubStates.addElement(subState);
    }

    /**
     * Description; change the state to new state.
     * this method call onGlobalExit from the current state, change
     * the state to new state and then call onGlobalEntry on the new state.
     * As a result of this call the state will be updated with the new state
     * <p>
     *
     * @param stateOwner Object
     * @param newState   State
     * @param index      int
     */
    public void changeState(StateOwner stateOwner, State newState, int index) throws StateException {

        if (log.isDebugEnabled()) {
            log.debug("changeState: current="
                    + this.toString()
                    + " replace state to" + newState.toString());
        }

        State oldState = vecSubStates.get(index);
        oldState.onGlobalExit(stateOwner, newState);
        vecSubStates.set(index, newState);
        newState.onGlobalEntry(stateOwner, oldState);
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
            ((State) o).vecSubStates = (Vector<State>) vecSubStates.clone();
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
        vecSubStates.removeElement(theState);
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
        if (log.isDebugEnabled()) {
            log.debug("getGlobalSetType: " + this.toString());
        }
        List<Integer> l = new ArrayList<Integer>();
        List<Integer> lSubState;
        // do on exit from the current state and go on the vector
        // go through the vector and call to on Exit
        for (int i = 0; i < vecSubStates.size(); i++) {
            lSubState = vecSubStates.elementAt(i).getGlobalSetType();
            l.addAll(lSubState);
        }
        Integer i = getStateType();
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
        return iStateType;
    }

    /**
     * This method returns the sub-states vector
     *
     * @return : java.util.Vector
     */
    public Vector<State> getVecSubStates() {
        return vecSubStates;
    }

    /**
     * Description:
     * handle event get an event, and check what is the action that need to
     * be take according to the result of the onEvent(), if the onEvent return
     * new state it means that we need to change the state to the new state, before
     * doing so we should check if the substate is supported by us.
     *
     * @param stateOwner java.lang.Object
     * @param state      State
     * @param event      java.lang.Object
     * @param index      int
     * @return State
     */
    protected State handleEvent(StateOwner stateOwner, State state, Object event, int index) throws StateException {
        if (log.isDebugEnabled()) {
            log.debug("handleEvent (context,owner,event,index) - " + this.toString() + ": event=" + event.toString());
        }
        Vector<State> vecSubStates = new Vector<State>(this.vecSubStates);
        State newState = null;
        try {
            // check if the current state now what to do with this event.
            newState = onEvent(stateOwner.getContext(), event);
        } catch (StateException e) {
            log.info("Exception: got exception from " + this.toString() + "(onEvent): " + event + " exception: " + e + " context: " + stateOwner.getContext());
            throw e;
            //return null;
        }

        // get a new state by sub state check if this state is supported
        // by us if we de change the state
        if (null != newState) {
            return newState;

        }
        // if the new state is null it means that you need to go throw
        // our sub state
        // go recursive till some now what to do with
        // the new state
        Enumeration<State> e = vecSubStates.elements();
        int subIndex = 0;
        while (e.hasMoreElements()) {
            State subState = e.nextElement();
            newState = subState.handleEvent(stateOwner, this, event, subIndex);
            if (log.isDebugEnabled()) {
                log.debug("handleEvent(4 params): checking vector - "
                        + subState.toString());
            }
            if (null != newState) {
                // substate return a state check if this state
                // is supported by us if we do change the state.
                if (isSubStateSupported(newState)) {
                    changeState(stateOwner, newState, subIndex);
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
            newState = completeHandleEvent(stateOwner.getContext(), event);
        } catch (StateException ex) {
            log.info("Exception: got exception from " + this.toString() + "(completeHandleEvent): " + ex);
            return null;
        }
        return newState;
    }

    /**
     * Definition:
     * This method is for the StateOwner.
     * <p>
     * Creation date: (03/15/2001 3:06:49 PM)
     *
     * @param owner StateOwner
     * @param event Object
     */
    public State handleEvent(StateOwner owner, Object event) throws StateException {
        if (log.isDebugEnabled()) {
            log.debug("handleEvent (context,event) - " + this.toString() + ": event=" + event.toString());
        }

        Vector<State> vecSubStates = new Vector<State>(this.vecSubStates);
        State newState = null;
        try {
            // check if the current state know what to do with this event.
            newState = onEvent(owner.getContext(), event);
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
            newState = subState.handleEvent(owner, this, event, subIndex);
            if (null != newState) {
                if (isSubStateSupported(newState)) {
                    changeState(owner, newState, subIndex);
                } else {
                    return newState;
                }
            }
            subIndex++;
        }
        // no new state.
        try {
            newState = completeHandleEvent(owner.getContext(), event);
        } catch (StateException ex) {
            log.info("Exception: got exception from " + this.toString() + "(completeHandleEvent): " + ex);
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
            Enumeration<State> e = vecSubStates.elements();
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
     *
     * @param toState
     */
    public abstract void onExit(Object context, State toState) throws StateException;

    /**
     * call recursive to onEntry.*
     *
     * @param fromState
     * @param owner
     */
    public final void onGlobalEntry(StateOwner owner, State fromState) throws StateException {
        if (log.isDebugEnabled()) {
            log.debug("onGlobalEntry: " + this.toString() + " context: " + owner.getContext());
        }

        // update state machine with entry/exit
        IStateMachine stateMachine = owner.getStateMachine();
        stateMachine.recordStateEntry(this);

        // do on exit from the current state and go on the vector
        onEntry(owner.getContext(), fromState);
        performSpecificOnGlobalEntry(owner, fromState);
        // go through the vector and call to on Exit
        for (int i = 0; i < vecSubStates.size(); i++) {
            vecSubStates.elementAt(i).onGlobalEntry(owner, fromState);
            vecSubStates.elementAt(i).performSpecificOnGlobalEntry(owner, fromState);
        }

    }

    protected void performSpecificOnGlobalEntry(StateOwner owner, State fromState) {

    }

    /**
     * Call recursive to onExit
     *
     * @param owner StateOwner
     * @param newState State
     */
    public final void onGlobalExit(StateOwner owner, State newState) throws StateException {
        if (log.isDebugEnabled()) {
            log.debug("onGlobalExit: " + this.toString() + " context " + owner.getContext());
        }
        // go recursivlly down the vector
        for (int i = 0; i < vecSubStates.size(); i++) {
            vecSubStates.elementAt(i).onGlobalExit(owner, newState);
            vecSubStates.elementAt(i).performSpecificOnGlobalExit(owner, newState);
        }

        // do "onExit" from the current state
        onExit(owner.getContext(), newState);
        performSpecificOnGlobalExit(owner, newState);
        IStateMachine stateMachine = owner.getStateMachine();
        stateMachine.recordStateExit(this);
    }

    protected void performSpecificOnGlobalExit(StateOwner owner, State fromState) {

    }

    public void onRestore(StateOwner owner) {
        for (Object oState : vecSubStates) {
            State state = (State) oState;
            state.onRestore(owner);
        }
        IStateMachine stateMachine = owner.getStateMachine();
        stateMachine.recordStateEntry(this);
    }

    protected abstract Object onRequest(Object context, Object theRequest) throws StateException;

    public void setStateType(int newStateType) {
        iStateType = newStateType;
    }

    public void setSubState(State newState) {
        vecSubStates.removeAllElements();
        vecSubStates.addElement(newState);
    }

    public void setVecSubStates(Vector<State> newVecSubStates) {
        vecSubStates = newVecSubStates;
    }

    @Override
    public String toString() {
        // check our state and if false go recursive to sub state and check
        StringBuilder val = new StringBuilder(this.getClass().getName());
        for (int i = 0; i < vecSubStates.size(); i++) {
            val.append("/" + vecSubStates.elementAt(i).toString());
        }
        return val.toString();
    }

    public String toStringShort() {
        // check our state and if false go recursive to sub state and check
        StringBuilder val = new StringBuilder(toShortName(this.getClass().getName()));
        for (int i = 0; i < vecSubStates.size(); i++) {
            val.append("/" + toShortName(vecSubStates.elementAt(i).toString()));
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
     *
     * @param stateType int
     * @return boolean
     */
    public boolean validateState(int stateType) {
        // check our state and if false go recursive to sub state and check
        if (isSelfState(stateType)) {
            return true;
        }
        for (int i = 0; i < vecSubStates.size(); i++) {
            boolean retVal = vecSubStates.elementAt(i).validateState(stateType);
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
        for (int i = 0; i < vecSubStates.size(); i++) {
            lsubstate = vecSubStates.elementAt(i).getExactGlobalSetType();
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
