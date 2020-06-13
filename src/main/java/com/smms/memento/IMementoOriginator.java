package com.smms.memento;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public interface IMementoOriginator<T> {
	IMemento<T> saveToMemento();
	void restoreFromMemento(IMemento<T> memento);
}
