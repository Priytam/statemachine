package com.algo.statemachine.statemachine.timetable;

import com.algo.statemachine.stopwatch.IStopWatch;
import com.algo.statemachine.stopwatch.StopWatch;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
/**
 * Description:time manager in state machine 
 * Role: keeps track of time in state machine
 * Responsibility:
 */
public class StateMachineTimeTable implements Serializable {

	private static final long serialVersionUID = 368486527639734932L;

	private static transient Logger log = Logger.getLogger(StateMachineTimeTable.class);

	private Map<Integer, IStopWatch> stateStopWatches = Collections.synchronizedMap(new HashMap<Integer, IStopWatch>());

	private long lCurrentStateStartTime = 0;

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

		lCurrentStateStartTime = System.currentTimeMillis();

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
		synchronized (stateStopWatches) {
			stopWatches.addAll(stateStopWatches.values());
		}
		return stopWatches;
	}

	public void fromMap(Map<Integer, IStopWatch> externalizedForm) {
		synchronized (stateStopWatches) {
			stateStopWatches.clear();
			stateStopWatches.putAll(externalizedForm);
		}
	}

	public Map<Integer, IStopWatch> toMap() {
		synchronized (stateStopWatches) {
			return ImmutableMap.copyOf(stateStopWatches);
		}
	}

	private void addStopWatch(int stateType, IStopWatch stopWatch) {
		stateStopWatches.put(stateType, stopWatch);
	}

	private IStopWatch getStopWatch(int key) {
		return stateStopWatches.get(key);
	}

	public long getStartTimeInCurrentStateMillis() {
		return lCurrentStateStartTime;
	}

	public void setStartTimeInCurrentStateMillis(long startTime) {
		lCurrentStateStartTime = startTime;
	}
}