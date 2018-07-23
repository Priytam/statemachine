package com.p2.stopwatch;

import EDU.oswego.cs.dl.util.concurrent.SynchronizedLong;

public class StopWatch implements IStopWatch {
	private static final long serialVersionUID = 6035643693870755049L;

	private enum State implements INamedEnum {
		STOPPED("Stopped"),
		RUNNING("Running"),
		PAUSED("Paused");

		private String m_name;

		private State(String name) {
			m_name = name;
		}

		@Override
		public String getName() {
			return m_name;
		}
	}

	private static SynchronizedLong s_currentID = new SynchronizedLong(0);

	private long m_id = 0L;
	private State m_state = State.STOPPED;
	private long m_savedElapsedTime = 0L;
	private long m_startTime = 0L;

	private StopWatch() {
		super();
		setId(s_currentID.increment());
	}

	public static IStopWatch create() {
		return new StopWatch();
	}

	public static IStopWatch createAndStart() {
		IStopWatch stopWatch = create();
		stopWatch.start();
		return stopWatch;
	}

	@Override
	public void start() {
		if (isRunning()) {
			return;
		}

		setStartTime(now());
		setState(State.RUNNING);
	}

	@Override
	public long elapsedMillis() {
		long savedElapsed = getSavedElapsedTime();

		if (!isRunning()) {
			return savedElapsed;
		}

		return (now() - getStartTime() + savedElapsed);
	}

	@Override
	public void pause() {
		if (!isRunning()) {
			return;
		}

		setSavedElapsedTime(elapsedMillis());
		setState(State.PAUSED);
	}

	@Override
	public void restart() {
		boolean wasRunning = isRunning();
		stop();

		if (wasRunning) {
			start();
		}
	}

	@Override
	public long id() {
		return m_id;
	}

	@Override
	public void stop() {
		setSavedElapsedTime(0);
		setStartTime(0);
		setState(State.STOPPED);
	}

	private long now() {
		return System.currentTimeMillis();
	}

	private void setId(long id) {
		m_id = id;
	}

	public State getState() {
		return m_state;
	}

	public void setState(State state) {
		m_state = state;
	}

	public long getSavedElapsedTime() {
		return m_savedElapsedTime;
	}

	public void setSavedElapsedTime(long savedElapsedTime) {
		m_savedElapsedTime = savedElapsedTime;
	}

	public long getStartTime() {
		return m_startTime;
	}

	public void setStartTime(long startTime) {
		m_startTime = startTime;
	}

	private boolean isRunning() {
		return getState() == State.RUNNING;
	}

	@Override
	public String toString() {
		return "StopWatch: " + elapsedMillis() + " [" + getState() + "]";
	}

	public static void main(String[] args) throws InterruptedException {
		IStopWatch sw = StopWatch.create();
		sw.start();
		Thread.sleep(1000);
		System.out.println(sw.elapsedMillis());
		Thread.sleep(2000);
		System.out.println(sw.elapsedMillis());
		sw.pause();
		Thread.sleep(3000);
		System.out.println(sw.elapsedMillis());
		sw.start();
		Thread.sleep(4000);
		System.out.println(sw.elapsedMillis());
		sw.stop();
		Thread.sleep(5000);
		System.out.println(sw.elapsedMillis());
		sw.restart();
		Thread.sleep(6000);
		System.out.println(sw.elapsedMillis());

	}
}
