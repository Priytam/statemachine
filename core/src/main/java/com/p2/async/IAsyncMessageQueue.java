package com.p2.async;

public interface IAsyncMessageQueue {
    boolean postMessage(Object message);

    void clearPendingMessages(String sOperationID);
}
