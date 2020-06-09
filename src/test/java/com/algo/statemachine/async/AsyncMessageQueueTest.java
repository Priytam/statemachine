package com.algo.statemachine.async;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.CheckedInputStream;

/**
 * User: Priytam Jee Pandey
 * Date: 06/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public class AsyncMessageQueueTest {

    private IAsyncMessageHandler handler;
    private String sNotifyId;
    private List<String> messages = Lists.newArrayList();

    @Before
    public void initHandler() {
        handler = new IAsyncMessageHandler() {
            @Override
            public void handleAsyncMessage(Object message) throws Exception {
                try {
                    messages.add((String) message);
                    Thread.sleep(500);
                } catch (Exception e) {

                }
            }

            @Override
            public Object getOwnerForMessage(Object message) {
                return null;
            }

            @Override
            public void validate() {
                throw new RuntimeException("Can't start");
            }

            @Override
            public void notifyClearPending(String sOperationID) {
                sNotifyId = sOperationID;
            }
        };
    }

    @After
    public void clean() {
        messages = Lists.newArrayList();
    }

    @Test
    public void shouldNotMoreThanMaxPending() {
        AsyncMessageQueue asyncMessageQueue = new AsyncMessageQueue(3, Executors.newSingleThreadExecutor(), handler);
        Assert.assertTrue(asyncMessageQueue.postMessage("1"));
        Assert.assertTrue(asyncMessageQueue.postMessage("2"));
        Assert.assertTrue(asyncMessageQueue.postMessage("3"));
        Assert.assertTrue(asyncMessageQueue.postMessage("4"));
        Assert.assertFalse(asyncMessageQueue.postMessage("5"));
        Assert.assertFalse(asyncMessageQueue.postMessage("6"));
        Assert.assertFalse(asyncMessageQueue.postMessage("7"));
    }

    @Test
    public void shouldProcess() {
        AsyncMessageQueue asyncMessageQueue = new AsyncMessageQueue(3, Executors.newSingleThreadExecutor(), handler);
        Assert.assertTrue(asyncMessageQueue.postMessage("1"));
        Assert.assertTrue(asyncMessageQueue.postMessage("2"));
        Assert.assertTrue(asyncMessageQueue.postMessage("3"));
        Assert.assertTrue(asyncMessageQueue.postMessage("4"));
        Assert.assertFalse(asyncMessageQueue.postMessage("5"));

        asyncMessageQueue.clearPendingMessages("123");

        Assert.assertEquals("123", sNotifyId);
        Assert.assertTrue(asyncMessageQueue.postMessage("7"));
        Assert.assertTrue(asyncMessageQueue.postMessage("8"));
        Assert.assertTrue(asyncMessageQueue.postMessage("9"));
        Assert.assertTrue(asyncMessageQueue.postMessage("10"));
        Assert.assertFalse(asyncMessageQueue.postMessage("11"));
    }

    @Test
    public void shouldNotProcessInvalid() throws InterruptedException {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        AsyncMessageQueue asyncMessageQueue = new AsyncMessageQueue(3, threadPool, handler);
        asyncMessageQueue.postMessage("invalid");
        Thread.sleep(2000);
        Assert.assertTrue(messages.isEmpty());
    }

}