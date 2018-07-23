package com.p2.statemachine;
import java.io.Serializable;

public class TimeInState implements Serializable {

	private static final long serialVersionUID = 1L;
	private long m_lStateEntryTime;
	private long m_lStateExitTime;
	private long m_lStateTotalTime;


	public TimeInState(long start, long end, long total) {
		m_lStateEntryTime = start;
		m_lStateExitTime = end;
		m_lStateTotalTime = total;
	}

	@Override
	public String toString() {
		return "TimeInState [m_lStateEntryTime=" + m_lStateEntryTime + ", m_lStateExitTime=" + m_lStateExitTime + ", m_lStateTotalTime=" + m_lStateTotalTime
				+ "]";
	}

	public TimeInState(long start, long end) {
		this(start, end, 0L);
	}

	public TimeInState(long start) {
		this(start, 0L, 0L);
	}

	public TimeInState() {
		this(0L, 0L, 0L);
	}

	public long getStateEntryTime() {
		return m_lStateEntryTime;
	}

	public void setStateEntryTime(long start) {
		m_lStateEntryTime = start;
	}

	public long getStateExitTime() {
		return m_lStateExitTime;
	}

	public void setStateExitTime(long end) {
		m_lStateExitTime = end;
	}

	public long getStateTotalTime() {
		return m_lStateTotalTime;
	}

	public void setStateTotalTime(long total) {
		m_lStateTotalTime = total;
	}
}
