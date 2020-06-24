package de.symeda.sormas.api.utils.jurisdiction;

import de.symeda.sormas.api.event.EventJurisdictionDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;

public class EventJurisdictionHelper {

	public static boolean isInJurisdiction(
		JurisdictionLevel jurisdictionLevel,
		UserJurisdiction userJurisdiction,
		EventJurisdictionDto eventJurisdiction) {

		if (eventJurisdiction.getReportingUserUuid() != null
			&& DataHelper.equal(userJurisdiction.getUuid(), eventJurisdiction.getReportingUserUuid())) {
			return true;
		}

		if (eventJurisdiction.getSurveillanceOfficerUuid() != null
			&& DataHelper.equal(userJurisdiction.getUuid(), eventJurisdiction.getSurveillanceOfficerUuid())) {
			return true;
		}

		switch (jurisdictionLevel) {
		case NONE:
			return false;
		case NATION:
			return true;
		case REGION:
			return eventJurisdiction.getRegionUuid() != null && DataHelper.equal(eventJurisdiction.getRegionUuid(), userJurisdiction.getRegionUuid());
		case DISTRICT:
			return eventJurisdiction.getDistrictUuid() != null
				&& DataHelper.equal(eventJurisdiction.getDistrictUuid(), userJurisdiction.getDistrictUuid());
		case COMMUNITY:
			return eventJurisdiction.getCommunityUuid() != null
				&& DataHelper.equal(eventJurisdiction.getCommunityUuid(), userJurisdiction.getCommunityUuid());
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
