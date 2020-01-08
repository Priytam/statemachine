package com.p2.statemachine;

import org.apache.log4j.Logger;


/**
 * A wrapper for events that is used by the state machine for postAndWait requests
 */
public class StateMachineLatch {
	private static Logger log = Logger.getLogger(StateMachineLatch.class);
	/**
	 * true if timedout before the event was processed
	 */
	private boolean expired = false;
	/**
	 * true if the StateMachine has processed the event
	 */
	private boolean released = false;
	/**
	 * how long to wait for the StateMachine to process the event
	 */
	private long timeoutMilli;
	/**
	 * The event that is being wrapped
	 */
	private Object event;
	/**
	 * Avoid race condition when StateMachine is about to handle this event and the timeout occurs.
	 * When true the latch continues to sleep until released
	 */
	private volatile boolean m_busy = false;
	/**
	 * Throwable generated during handling of the event will be stored in here
	 */
	private Throwable m_thrown;

	public StateMachineLatch(Object event, long timeoutMilli) {
		this.timeoutMilli = timeoutMilli;
		this.event = event;
	}

	/**
	 * Block until either timeout occurs or latch is released
	 *
	 * @return
	 */
	synchronized public boolean block() throws Throwable {
		Thread.interrupted();
		// may have already been handled
		if (released) {
			if (m_thrown != null) {
				throw (m_thrown);
			}
			return true;
		}
		try {
			do {
				wait(timeoutMilli);
			}
			while (m_busy);
		} catch (InterruptedException e) {
			log.debug("caught Exception", e);
		}
		if (!released) {
			expired = true;
			return false;
		}
		if (m_thrown != null) {
			throw (m_thrown);
		}
		return true;
	}

	/**
	 * Release the thread waiting on this latch (also clears busy flag)
	 */
	synchronized public void release(Throwable thrown) {
		m_thrown = thrown;
		released = true;
		m_busy = false;
		notifyAll();
	}

	/**
	 * @return true if timedout before the event was processed
	 */
	synchronized public boolean isExpired() {
		return expired;
	}

	/**
	 * @return The event that is being wrapped
	 */
	synchronized public Object getEvent() {
		return event;
	}

	/**
	 * Mark the latch as busy - postpones the wakeup on timeout
	 *
	 * @param busy
	 */
	public void setBusy(boolean busy) {
		m_busy = busy;
	}
}
