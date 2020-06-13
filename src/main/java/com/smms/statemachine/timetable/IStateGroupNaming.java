package com.smms.statemachine.timetable;

import java.util.Collection;
import java.util.Set;

/**
 * User: Priytam Jee Pandey
 * Date: 05/06/20
 * Time: 6:21 pm
 * email: mrpjpandey@gmail.ocm
 */
public interface IStateGroupNaming {
	Set<Integer> getStatesByGroupName(String groupName);

	Collection<String> getAllStateGroupNames();

	boolean isExist(String groupName);
}
