package com.p2.statemachine;



public class SyncStateMachine extends AbstractStateMachine {
	private static final long serialVersionUID = 1L;

	private final Object m_objSync;
	transient private boolean m_bHandlingEvent = false;

	protected SyncStateMachine() {
		m_objSync = this;
	}

	public SyncStateMachine(Object context) {
		super(context);
		m_objSync = this;
	}

	public SyncStateMachine(Object sOwner, Object objSync) {
		super(sOwner);
		m_objSync = objSync;
	}


	public boolean postEventAndWait(Object event) throws StateException {
		State newState = postEventInternal(event);
		return (null != newState);
	}


	protected State postEventInternal(Object event) throws StateException {
		State newState;
		State oldState;
		synchronized (m_objSync) {
			oldState = getState();
			try {
				if (m_bHandlingEvent) {
					throw new RuntimeException("Nested postEventAndWait()");
				}
				m_bHandlingEvent = true;
				newState = onEvent(event);
			} finally {
				m_bHandlingEvent = false;
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
		return m_objSync;
	}
}
