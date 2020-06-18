package de.symeda.sormas.api.utils.jurisdiction;

import de.symeda.sormas.api.sample.SampleJurisdictionDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;

public class SampleJurisdictionHelper {

	public static boolean isInJurisdiction(
		JurisdictionLevel jurisdictionLevel,
		UserJurisdiction userJurisdiction,
		SampleJurisdictionDto sampleJurisdiction) {

		if (sampleJurisdiction.getReportingUserUuid() != null
			&& DataHelper.equal(userJurisdiction.getUuid(), sampleJurisdiction.getReportingUserUuid())) {
			return true;
		}

		if (sampleJurisdiction.getCaseJurisdiction() != null) {
			return CaseJurisdictionHelper.isInJurisdiction(jurisdictionLevel, userJurisdiction, sampleJurisdiction.getCaseJurisdiction());
		}

		if (sampleJurisdiction.getContactJurisdiction() != null) {
			return ContactJurisdictionHelper.isInJurisdiction(jurisdictionLevel, userJurisdiction, sampleJurisdiction.getContactJurisdiction());
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
			return sampleJurisdiction.getLabUuid() != null
				&& DataHelper.equal(sampleJurisdiction.getLabUuid(), userJurisdiction.getHealthFacilityUuid());
		case EXTERNAL_LABORATORY:
			return sampleJurisdiction.getOtherLabUuid() != null
				&& DataHelper.equal(sampleJurisdiction.getOtherLabUuid(), userJurisdiction.getHealthFacilityUuid());
		case POINT_OF_ENTRY:
			return false;
		default:
			return false;
		}

	}

}
