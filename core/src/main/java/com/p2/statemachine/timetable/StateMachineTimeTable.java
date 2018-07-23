package com.p2.statemachine.timetable;

import com.google.common.collect.ImmutableMap;
import com.p2.stopwatch.IStopWatch;
import com.p2.stopwatch.StopWatch;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;


/**
 * Description:time manager in state machine 
 * Role: keeps track of time in state machine
 * Responsibility:
 * 
 */
public class StateMachineTimeTable implements Serializable {

	private static final long serialVersionUID = 368486527639734932L;

	private static transient Logger log = Logger.getLogger(StateMachineTimeTable.class);

	private Map<Integer, IStopWatch> m_stateStopWatches = Collections.synchronizedMap(new HashMap<Integer, IStopWatch>());

	private long m_lCurrentStateStartTime = 0;

	public StateMachineTimeTable() {
		super();
	}

	public void recordEntry(int state) {
		IStopWatch stopWatch = getStopWatch(state);

		if (stopWatch == null) {
			log.debug("recordEntry() - creating new watch for state " + state);
			stopWatch = StopWatch.create();
			addStopWatch(state, stopWatch);
		}

		stopWatch.start();

		m_lCurrentStateStartTime = System.currentTimeMillis();

	}

	public void recordExit(int state) {
		IStopWatch stopWatch = getStopWatch(state);
		if (stopWatch == null) {
			log.warn("recordExit() - stop watch for state " + state + "not found - check for matching recordEntry() call");
			return;
		}
		stopWatch.pause();
	}

	public long getTimeInStateMillis(int stateType) {
		IStopWatch stopWatch = getStopWatch(stateType);
		if (stopWatch == null) {
			return 0;
		}
		return stopWatch.elapsedMillis();
	}

	public void restart(int stateType) {
		IStopWatch stopWatch = getStopWatch(stateType);
		if (stopWatch == null) {
			return;
		}
		stopWatch.restart();
	}

	public void restartAll() {
		Collection<IStopWatch> stopWatches = getAllStopWatches();
		for (IStopWatch stopWatch : stopWatches) {
			stopWatch.restart();
		}
	}

	private Collection<IStopWatch> getAllStopWatches() {
		Collection<IStopWatch> stopWatches = new ArrayList<IStopWatch>();
		synchronized (m_stateStopWatches) {
			stopWatches.addAll(m_stateStopWatches.values());
		}
		return stopWatches;
	}

	public void fromMap(Map<Integer, IStopWatch> externalizedForm) {
		synchronized (m_stateStopWatches) {
			m_stateStopWatches.clear();
			m_stateStopWatches.putAll(externalizedForm);
		}
	}

	public Map<Integer, IStopWatch> toMap() {
		synchronized (m_stateStopWatches) {
			return ImmutableMap.copyOf(m_stateStopWatches);
		}
	}

	private void addStopWatch(int stateType, IStopWatch stopWatch) {
		m_stateStopWatches.put(stateType, stopWatch);
	}

	private IStopWatch getStopWatch(int key) {
		return m_stateStopWatches.get(key);
	}

	public long getTimeInCurrentStateMillis() {
		return m_lCurrentStateStartTime;
	}

	public void setTimeInCurrentStateMillis(long startTime) {
		m_lCurrentStateStartTime = startTime;
	}
}