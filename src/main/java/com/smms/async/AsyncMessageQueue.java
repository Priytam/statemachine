package com.smms.async;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public class AsyncMessageQueue implements Runnable, IAsyncMessageQueue {
    final transient private static Logger log = Logger.getLogger(AsyncMessageQueue.class);
    private final int maxPendingMessages;
    private int numPending = 0;
    private final transient Object pendingLock = new Object();
    private transient LinkedQueue queue = new LinkedQueue();
    private transient ExecutorService threadPool;
    private final transient IAsyncMessageHandler asyncMessageQueueHandler;

    public AsyncMessageQueue(int maxPendingMessages, ExecutorService threadPool, IAsyncMessageHandler asyncMessageQueueHandler) {
        this.maxPendingMessages = maxPendingMessages;
        this.threadPool = threadPool;
        this.asyncMessageQueueHandler = asyncMessageQueueHandler;
    }

    @Override
    public boolean postMessage(Object message) {
        if (log.isDebugEnabled()){
            log.debug("postMessage() - try for " + asyncMessageQueueHandler.getOwnerForMessage(message) + " event " + message);
        }
        if (numPending > maxPendingMessages) {
            log.error("postMessage() - event " + message + " wil be lost due to big number of pending messages");
            return false;
        }
        try {
            if (queue.offer(message, 1000)) {
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

    private ExecutorService getThreadPool() {
        return threadPool;
    }


    /**
     * Remove all pending events from the queue
     * @param operationID
     */
    @Override
    public void clearPendingMessages(String operationID) {
        synchronized (pendingLock) {
            numPending = 0;
            queue = new LinkedQueue();
            asyncMessageQueueHandler.notifyClearPending(operationID);
        }
    }


    @Override
    public void run() {
        asyncMessageQueueHandler.validate();
        Object take = null;
        try {
            take = queue.take();
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
