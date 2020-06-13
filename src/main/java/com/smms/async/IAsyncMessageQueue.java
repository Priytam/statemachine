package com.smms.async;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public interface IAsyncMessageQueue {
    boolean postMessage(Object message);

    void clearPendingMessages(String sOperationID);
}