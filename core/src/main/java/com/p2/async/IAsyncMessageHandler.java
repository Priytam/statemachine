package com.p2.async;

import java.io.Serializable;

public interface IAsyncMessageHandler extends Serializable {
	void handleAsyncMessage(Object message) throws Exception;

	Object getOwnerForMessage(Object message);

	void validate();

	void notifyClearPending(String sOperationID);
}
