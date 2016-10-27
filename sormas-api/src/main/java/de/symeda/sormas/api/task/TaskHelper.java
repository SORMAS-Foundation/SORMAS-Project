package de.symeda.sormas.api.task;

import java.util.Arrays;
import java.util.Collections;

import de.symeda.sormas.api.user.UserRole;

public final class TaskHelper {

	public static Iterable<TaskStatus> getPossibleStatusChanges(TaskStatus currentStatus, UserRole userRole) {

		if (UserRole.SURVEILLANCE_SUPERVISOR.equals(userRole)) {
			switch (currentStatus) {
			case PENDING:
				return Arrays.asList(TaskStatus.DONE, TaskStatus.DISCARDED);
			default:
				return Collections.emptyList();
			}
		}
		else if (UserRole.SURVEILLANCE_OFFICER.equals(userRole)) {
			switch (currentStatus) {
			case PENDING:
				return Arrays.asList(TaskStatus.DONE);
			default:
				return Collections.emptyList();
			}
		}		
		else if (UserRole.INFORMANT.equals(userRole)) {
			switch (currentStatus) {
			default:
				return Collections.emptyList();
			}
		}
		
		throw new UnsupportedOperationException("User role not implemented: " + userRole);
	}
	
}
