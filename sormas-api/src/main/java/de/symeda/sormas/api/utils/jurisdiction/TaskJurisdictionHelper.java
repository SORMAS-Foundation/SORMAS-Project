package de.symeda.sormas.api.utils.jurisdiction;

import de.symeda.sormas.api.task.TaskJurisdictionDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;

public class TaskJurisdictionHelper {

	public static boolean isInJurisdiction(
		JurisdictionLevel jurisdictionLevel,
		UserJurisdiction userJurisdiction,
		TaskJurisdictionDto taskJurisdiction) {

		if (taskJurisdiction.getCreatorUserUuid() != null && DataHelper.equal(userJurisdiction.getUuid(), taskJurisdiction.getCreatorUserUuid())) {
			return true;
		}

		if (taskJurisdiction.getAssigneeUserUuid() != null && DataHelper.equal(userJurisdiction.getUuid(), taskJurisdiction.getAssigneeUserUuid())) {
			return true;
		}

		if (taskJurisdiction.getCaseJurisdiction() != null) {
			return CaseJurisdictionHelper.isInJurisdiction(jurisdictionLevel, userJurisdiction, taskJurisdiction.getCaseJurisdiction());
		}

		if (taskJurisdiction.getContactJurisdiction() != null) {
			return ContactJurisdictionHelper.isInJurisdiction(jurisdictionLevel, userJurisdiction, taskJurisdiction.getContactJurisdiction());
		}

		if (taskJurisdiction.getEventJurisdiction() != null) {
			return EventJurisdictionHelper.isInJurisdiction(jurisdictionLevel, userJurisdiction, taskJurisdiction.getEventJurisdiction());
		}

		switch (jurisdictionLevel) {
		case NONE:
			return false;
		case NATION:
			return true;
		case REGION:
			return false;
		case DISTRICT:
			return false;
		case COMMUNITY:
			return false;
		case HEALTH_FACILITY:
			return false;
		case LABORATORY:
			return false;
		case EXTERNAL_LABORATORY:
			return false;
		case POINT_OF_ENTRY:
			return false;
		default:
			return false;
		}
	}
}
