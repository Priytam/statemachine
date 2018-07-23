package com.p2.statemachine.timetable;

import java.util.Collection;
import java.util.Set;

public interface IStateGroupNaming {
	Set<Integer> getStatesByGroupName(String groupName);

	Collection<String> getAllStateGroupNames();

	boolean isExist(String groupName);
}
