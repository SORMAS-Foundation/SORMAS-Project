package de.symeda.sormas.api.caze;

import java.util.Arrays;
import java.util.Collections;

import de.symeda.sormas.api.user.UserRole;

public final class CaseHelper {

	public static Iterable<CaseStatus> getPossibleStatusChanges(CaseStatus currentStatus, UserRole userRole) {

		if (UserRole.SURVEILLANCE_SUPERVISOR.equals(userRole)) {
			switch (currentStatus) {
			case POSSIBLE:
				return Arrays.asList(CaseStatus.INVESTIGATED);
			case INVESTIGATED:
				return Arrays.asList(CaseStatus.SUSPECT, CaseStatus.NO_CASE, CaseStatus.POSSIBLE);
			case SUSPECT:
				return Arrays.asList(CaseStatus.INVESTIGATED);
			case NO_CASE:
				return Arrays.asList(CaseStatus.INVESTIGATED);
			default:
				return Collections.emptyList();
			}
		}
		else if (UserRole.SURVEILLANCE_OFFICER.equals(userRole)) {
			switch (currentStatus) {
			case POSSIBLE:
				return Arrays.asList(CaseStatus.INVESTIGATED);
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
	
	public static boolean isPrimary(CaseStatus currentStatus, CaseStatus nextStatus) {
		
		switch (currentStatus) {
		case POSSIBLE:
			return nextStatus == CaseStatus.INVESTIGATED;
		case INVESTIGATED:
			return nextStatus == CaseStatus.SUSPECT;
		case SUSPECT:
			return false;
		case NO_CASE:
			return false;
		default:
			return false;
		}
	}
	
	public static boolean isBackward(CaseStatus currentStatus, CaseStatus nextStatus) {
		
		switch (currentStatus) {
		case INVESTIGATED:
			return nextStatus == CaseStatus.POSSIBLE;
		case SUSPECT:
			return nextStatus == CaseStatus.INVESTIGATED;
		case NO_CASE:
			return nextStatus == CaseStatus.INVESTIGATED;
		default:
			return false;
		}
	}
}
