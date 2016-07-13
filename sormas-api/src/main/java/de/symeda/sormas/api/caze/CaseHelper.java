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
		
		throw new UnsupportedOperationException("User role not implemented: " + userRole);
	}
}
