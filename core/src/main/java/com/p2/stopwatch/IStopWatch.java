package com.p2.stopwatch;

import java.io.Serializable;

public interface IStopWatch extends Serializable {
	void start();

	long elapsedMillis();

	void pause();

	void restart();

	void stop();

	long id();
}
