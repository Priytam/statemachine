package com.p2.async;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import org.apache.log4j.Logger;

import java.util.concurrent.ThreadPoolExecutor;

public class AsyncMessageQueue implements Runnable, IAsyncMessageQueue {
	transient private static Logger log = Logger.getLogger(AsyncMessageQueue.class);
	private int m_maxPendingMessages;
	private int m_numPending = 0;
	private transient Object m_pendingLock = new Object();
	private transient LinkedQueue m_queue = new LinkedQueue();
	private transient ThreadPoolExecutor m_threadPool;
	private transient IAsyncMessageHandler m_asyncMessageQueueHandler;

	public AsyncMessageQueue(int iMaxPendingMessages, ThreadPoolExecutor threadPool, IAsyncMessageHandler asyncMessageQueueHandler) {
		m_maxPendingMessages = iMaxPendingMessages;
		m_threadPool = threadPool;
		m_asyncMessageQueueHandler = asyncMessageQueueHandler;
	}

	public AsyncMessageQueue() {

	}

	@Override
	public boolean postMessage(Object message) {
		if (log.isDebugEnabled())
			log.debug("postMessage() - try for " + m_asyncMessageQueueHandler.getOwnerForMessage(message) + " event " + message);
		if (m_numPending > m_maxPendingMessages) {
			log.error("postMessage() - event " + message + " wil be lost due to big number of pending messages");
			return false;
		}
		try {
			if (m_queue.offer(message, 1000)) {
				synchronized (m_pendingLock) {
					if (log.isDebugEnabled())
						log.debug("postMessage() - posted for " + m_asyncMessageQueueHandler.getOwnerForMessage(message) + " message " + message);
					// schedule to handle first event
					if (m_numPending == 0) {
						getThreadPool().execute(this);
					}
					m_numPending++;
				}
				return true;
			}
		} catch (InterruptedException e) {
			log.warn("postMessage() - ", e);
		}
		return false;
	}

	private ThreadPoolExecutor getThreadPool() {
		return m_threadPool;
	}


	/**
	 * Remove all pending events from the queue
	 *
	 * @param sOperationID
	 */
	@Override
	public void clearPendingMessages(String sOperationID) {
		synchronized (m_pendingLock) {
			m_numPending = 0;
			m_queue = new LinkedQueue();
			m_asyncMessageQueueHandler.notifyClearPending(sOperationID);
		}
	}


	@Override
	public void run() {
		m_asyncMessageQueueHandler.validate();
		Object take = null;
		try {
			take = m_queue.take();
		} catch (InterruptedException e) {
			log.warn("Could not take message from queue, Exception : " + e.getMessage());
		}
		try {
			m_asyncMessageQueueHandler.handleAsyncMessage(take);
		} catch (Exception e) {
			log.error("run() - ", e);
		}
		// reduce the pending event counter
		synchronized (m_pendingLock) {
			if (m_numPending < 1) {
				return;
			}
			m_numPending--;
			if (m_numPending > 0) {
				// Schedule to handle the next waiting event, and release control
				// This should ensure that at any given moment there is only one thread running events
				getThreadPool().execute(this);
			}
		}
	}


	public void setThreadPool(ThreadPoolExecutor threadPool) {
		m_threadPool = threadPool;
	}

}
