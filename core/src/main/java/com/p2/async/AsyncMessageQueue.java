package com.p2.async;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import org.apache.log4j.Logger;

import java.util.concurrent.ThreadPoolExecutor;

public class AsyncMessageQueue implements Runnable, IAsyncMessageQueue {
	transient private static Logger log = Logger.getLogger(AsyncMessageQueue.class);
	private int maxPendingMessages;
	private int numPending = 0;
	private transient Object pendingLock = new Object();
	private transient LinkedQueue m_queue = new LinkedQueue();
	private transient ThreadPoolExecutor threadPool;
	private transient IAsyncMessageHandler asyncMessageQueueHandler;

	public AsyncMessageQueue(int iMaxPendingMessages, ThreadPoolExecutor threadPool, IAsyncMessageHandler asyncMessageQueueHandler) {
		maxPendingMessages = iMaxPendingMessages;
		this.threadPool = threadPool;
		this.asyncMessageQueueHandler = asyncMessageQueueHandler;
	}

	public AsyncMessageQueue() {

	}

	@Override
	public boolean postMessage(Object message) {
		if (log.isDebugEnabled())
			log.debug("postMessage() - try for " + asyncMessageQueueHandler.getOwnerForMessage(message) + " event " + message);
		if (numPending > maxPendingMessages) {
			log.error("postMessage() - event " + message + " wil be lost due to big number of pending messages");
			return false;
		}
		try {
			if (m_queue.offer(message, 1000)) {
				synchronized (pendingLock) {
					if (log.isDebugEnabled())
						log.debug("postMessage() - posted for " + asyncMessageQueueHandler.getOwnerForMessage(message) + " message " + message);
					// schedule to handle first event
					if (numPending == 0) {
						getThreadPool().execute(this);
					}
					numPending++;
				}
				return true;
			}
		} catch (InterruptedException e) {
			log.warn("postMessage() - ", e);
		}
		return false;
	}

	private ThreadPoolExecutor getThreadPool() {
		return threadPool;
	}


	/**
	 * Remove all pending events from the queue
	 *
	 * @param sOperationID
	 */
	@Override
	public void clearPendingMessages(String sOperationID) {
		synchronized (pendingLock) {
			numPending = 0;
			m_queue = new LinkedQueue();
			asyncMessageQueueHandler.notifyClearPending(sOperationID);
		}
	}


	@Override
	public void run() {
		asyncMessageQueueHandler.validate();
		Object take = null;
		try {
			take = m_queue.take();
		} catch (InterruptedException e) {
			log.warn("Could not take message from queue, Exception : " + e.getMessage());
		}
		try {
			asyncMessageQueueHandler.handleAsyncMessage(take);
		} catch (Exception e) {
			log.error("run() - ", e);
		}
		// reduce the pending event counter
		synchronized (pendingLock) {
			if (numPending < 1) {
				return;
			}
			numPending--;
			if (numPending > 0) {
				// Schedule to handle the next waiting event, and release control
				// This should ensure that at any given moment there is only one thread running events
				getThreadPool().execute(this);
			}
		}
	}


	public void setThreadPool(ThreadPoolExecutor threadPool) {
		this.threadPool = threadPool;
	}

}
