package de.symeda.sormas.api.utils.jurisdiction;

import java.util.Collections;

import de.symeda.sormas.api.event.EventJurisdictionDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;

public class EventJurisdictionHelper {

	public static boolean isInJurisdiction(RoleCheck roleCheck, UserJurisdiction userJurisdiction, EventJurisdictionDto eventJurisdiction) {

		if (eventJurisdiction.getReportingUserUuid() != null
			&& DataHelper.equal(userJurisdiction.getUuid(), eventJurisdiction.getReportingUserUuid())) {
			return true;
		}

		if (eventJurisdiction.getRegionUuid() != null && roleCheck.hasAnyRole(UserRole.getSupervisorRoles())) {
			return DataHelper.equal(eventJurisdiction.getRegionUuid(), userJurisdiction.getRegionUuid());
		}

		if (eventJurisdiction.getDistrictUuid() != null && roleCheck.hasAnyRole(UserRole.getOfficerRoles())) {
			return DataHelper.equal(eventJurisdiction.getDistrictUuid(), userJurisdiction.getDistrictUuid());
		}

		if (eventJurisdiction.getSurveillanceOfficerUuid() != null
			&& DataHelper.equal(userJurisdiction.getUuid(), eventJurisdiction.getSurveillanceOfficerUuid())) {
			return true;
		}

		return roleCheck.hasAnyRole(Collections.singleton(UserRole.NATIONAL_USER));
	}
}
