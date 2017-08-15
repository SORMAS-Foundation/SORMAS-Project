package de.symeda.sormas.api.task;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import de.symeda.sormas.api.user.UserRole;

public final class TaskHelper {

	public static List<TaskStatus> getPossibleStatusChanges(TaskStatus currentStatus, UserRole userRole) {

		if (UserRole.SURVEILLANCE_SUPERVISOR.equals(userRole)
			|| UserRole.CONTACT_SUPERVISOR.equals(userRole)
			|| UserRole.CASE_SUPERVISOR.equals(userRole)) {
			switch (currentStatus) {
			case PENDING:
				return Arrays.asList(TaskStatus.DONE, TaskStatus.NOT_EXECUTABLE, TaskStatus.REMOVED);
			default:
				return Collections.emptyList();
			}
		}
		else if (UserRole.SURVEILLANCE_OFFICER.equals(userRole)
				|| UserRole.CONTACT_OFFICER.equals(userRole)
				|| UserRole.CASE_OFFICER.equals(userRole)) {
			switch (currentStatus) {
			case PENDING:
				return Arrays.asList(TaskStatus.DONE, TaskStatus.NOT_EXECUTABLE);
			default:
				return Collections.emptyList();
			}
		}		
		else if (UserRole.INFORMANT.equals(userRole)) {
			switch (currentStatus) {
			case PENDING:
				return Arrays.asList(TaskStatus.DONE, TaskStatus.NOT_EXECUTABLE);
			default:
				return Collections.emptyList();
			}
		}
		
		throw new UnsupportedOperationException("User role not implemented: " + userRole);
	}
	
	public static Date getDefaultSuggestedStart() {
		return new DateTime().toDate();
	}
	
	public static Date getDefaultDueDate() {
		return new DateTime().plusDays(1).toDate();
	}

}
