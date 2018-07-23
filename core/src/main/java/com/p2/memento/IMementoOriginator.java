package com.p2.memento;

public interface IMementoOriginator<T> {
	IMemento<T> saveToMemento();
	void restoreFromMemento(IMemento<T> memento);
}
