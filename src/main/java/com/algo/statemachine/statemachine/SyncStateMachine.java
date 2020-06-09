package com.algo.statemachine.statemachine;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public class SyncStateMachine extends AbstractStateMachine {
	private static final long serialVersionUID = 1L;

	private final Object objSync;
	transient private boolean bHandlingEvent = false;

	protected SyncStateMachine() {
		objSync = this;
	}

	public SyncStateMachine(Object context) {
		super(context);
		objSync = this;
	}

	public SyncStateMachine(Object sOwner, Object objSync) {
		super(sOwner);
		this.objSync = objSync;
	}

	public boolean postEventAndWait(Object event) throws StateException {
		State newState = postEventInternal(event);
		return (null != newState);
	}

	protected State postEventInternal(Object event) throws StateException {
		State newState;
		State oldState;
		synchronized (objSync) {
			oldState = getState();
			try {
				if (bHandlingEvent) {
					throw new RuntimeException("Nested postEventAndWait()");
				}
				bHandlingEvent = true;
				newState = onEvent(event);
			} finally {
				bHandlingEvent = false;
			}
		}
		if (null != newState) {
			afterStateChanged(oldState, newState);
		}
		return newState;
	}

	/**
	 * called after a state was changed out of the sync block
	 */
	protected void afterStateChanged(State oldState, State newState) {
	}

	@Override
	public boolean postEvent(Object event) {
		try {
			State newState = postEventInternal(event);
			return (null != newState);
		} catch (StateException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected Object getSyncObject() {
		return objSync;
	}
}
