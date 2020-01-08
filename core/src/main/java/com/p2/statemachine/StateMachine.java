package com.p2.statemachine;

import com.p2.async.AsyncMessageQueue;
import com.p2.async.IAsyncMessageHandler;
import com.p2.statemachine.iface.StateOwner;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A default implementation of a state machine with a queue of pending events.
 * 
 * Use as member of the class that requires a state.
 * 
 * Initialization:
 * StateMachine m_stateMachine = new StateMachine(this,maxPendingRequests);
 * m_stateMachine.initState(initialState);
 * 
 * Posting events to the state machine's queue:
 * m_stateMachine.postEvent(event);
 * 
 */
public class StateMachine extends AbstractStateMachine implements Serializable, IAsyncMessageHandler {
	private static final long serialVersionUID = -3624053223595674494L;
	transient private static Logger log = Logger.getLogger(StateMachine.class);
	private transient AsyncMessageQueue asyncMessageQueue;
	private transient ThreadLocal<Integer> handleEventDepth = new ThreadLocal<Integer>();

	public StateMachine(Object context, int maxPendingEvents, ThreadPoolExecutor threadPool) {
		super(context);
		asyncMessageQueue = new AsyncMessageQueue(maxPendingEvents, threadPool, this);
		StateMachine machine = this;
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

	/**
	 * @param owner            The owner of this state machine. This will appear as the context in the state's onEntry onEvent and onExit methods
	 * @param maxPendingEvents maximum number of allowed pending events in the queue. Excess events are discarded.
	 */
	public StateMachine(Object owner, int maxPendingEvents, int threadPoolSize) {
		this(owner, maxPendingEvents, (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize));
	}

	public StateMachine(Object owner, int maxPendingEvents, int threadPoolSize, State state) {
		this(owner, maxPendingEvents, threadPoolSize);
		setState(state);
	}

	/**
	 * @param event the event to be posted to the queue
	 * @return true on success
	 */
	@Override
	public boolean postEvent(Object event) {
		return asyncMessageQueue.postMessage(event);
	}

	public boolean postEventAndWait(Object event, long timeoutMilli) throws Throwable {
		if (inHandleEvent()) {
			throw new IllegalStateException("Trying to call postEventAndWait while handling another event within the same thread would have cause a deadlock");
		} else {
			StateMachineLatch latch = new StateMachineLatch(event, timeoutMilli);
			if (postEvent(latch) == false) {
				return false;
			}
			return latch.block();
		}
	}


	public void setThreadPool(ThreadPoolExecutor threadpool) {
		asyncMessageQueue.setThreadPool(threadpool);
	}

	private boolean inHandleEvent() {
		Integer depth = handleEventDepth.get();
		if (depth == null) {
			return false;
		}
		if (depth.intValue() == 0) {
			return false;
		}
		return true;
	}

	private void incrementHandleEventDepth() {
		Integer depth = handleEventDepth.get();
		if (depth == null) {
			depth = Integer.valueOf(1);
		} else {
			depth = Integer.valueOf(depth.intValue() + 1);
		}
		handleEventDepth.set(depth);
	}

	private void decrementHandleEventDepth() {
		Integer depth = handleEventDepth.get();
		depth = Integer.valueOf(depth.intValue() - 1);
		handleEventDepth.set(depth);
	}


	/**
	 * @param event
	 */
	public void forceEventAndWait(Object event) throws Throwable {
		onEvent(event);
	}


	/**
	 * Remove all pending events from the queue
	 */
	public void clearPendingEvents() {
		asyncMessageQueue.clearPendingMessages(null);
	}


	@Override
	public String toString() {
		if (null != getState()) {
			return getState().toString();
		}
		return "unknown";
	}

	public boolean postEventAndWait(Object event) throws Throwable {
		return postEventAndWait(event, 10000);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		asyncMessageQueue = new AsyncMessageQueue();
	}

	@Override
	public void handleAsyncMessage(Object event) throws Exception {
		// get the next waiting event
		StateMachineLatch latch = null;

		// if it is a latch
		if (event instanceof StateMachineLatch) {
			latch = (StateMachineLatch) event;
			// make sure it doesn't timeout while it is being handled
			latch.setBusy(true);
			// check that it hasn't already timedout
			if (!latch.isExpired()) {
				// extract the event
				event = latch.getEvent();
			} else {
				// timedout - ignore the event
				event = null;
			}
		}

		Throwable e = null;
		try {
			if (event != null) {
				if (log.isDebugEnabled())
					log.debug("run() - start handling for " + getOwner() + " event " + event);
				// handle the event
				incrementHandleEventDepth();
				onEvent(event);
				decrementHandleEventDepth();
				if (log.isDebugEnabled())
					log.debug("run() - done handling for " + getOwner() + " event " + event);
			}
		} catch (Throwable e1) {
			e = e1;
		}

		// release the thread waiting on this latch, and notify of errors
		if (latch != null) {
			latch.release(e);
		}
		// log unhandled errors
		else if (e != null) {
			log.error("run() - unhandled exception ", e);
		}
	}

	@Override
	public Object getOwnerForMessage(Object message) {
		return getOwner();
	}

	@Override
	public void validate() {
		// don't run if state not initialized
		if (getState() == null) {
			throw new RuntimeException("Can't start, no initial state was set for state machine of " + getOwner());
		}
	}

	@Override
	public void notifyClearPending(String sOperationID) {

	}
}
