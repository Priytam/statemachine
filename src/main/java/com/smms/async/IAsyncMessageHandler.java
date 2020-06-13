package com.smms.async;

import java.io.Serializable;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public interface IAsyncMessageHandler extends Serializable {
	void handleAsyncMessage(Object message) throws Exception;

	Object getOwnerForMessage(Object message);

	void validate();

	void notifyClearPending(String sOperationID);
}
