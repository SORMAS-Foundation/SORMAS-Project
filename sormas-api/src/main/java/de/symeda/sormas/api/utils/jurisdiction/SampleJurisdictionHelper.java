package de.symeda.sormas.api.utils.jurisdiction;

import de.symeda.sormas.api.sample.SampleJurisdictionDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;

public class SampleJurisdictionHelper {

	public static boolean isInJurisdictionOrOwned(
		JurisdictionLevel jurisdictionLevel,
		UserJurisdiction userJurisdiction,
		SampleJurisdictionDto sampleJurisdiction) {

		if (sampleJurisdiction.getReportingUserUuid() != null
			&& DataHelper.equal(userJurisdiction.getUuid(), sampleJurisdiction.getReportingUserUuid())) {
			return true;
		}

		switch (jurisdictionLevel) {
		case NONE:
			return false;
		case NATION:
			return true;
		case LABORATORY:
		case EXTERNAL_LABORATORY:
			return sampleJurisdiction.getLabUuid() != null && DataHelper.equal(sampleJurisdiction.getLabUuid(), userJurisdiction.getLabUuid());
		}

		if (sampleJurisdiction.getCaseJurisdiction() != null) {
			return CaseJurisdictionHelper.isInJurisdictionOrOwned(jurisdictionLevel, userJurisdiction, sampleJurisdiction.getCaseJurisdiction());
		}

		if (sampleJurisdiction.getContactJurisdiction() != null) {
			return ContactJurisdictionHelper
				.isInJurisdictionOrOwned(jurisdictionLevel, userJurisdiction, sampleJurisdiction.getContactJurisdiction());
		}

		if (sampleJurisdiction.getEventParticipantJurisdiction() != null) {
			return EventParticipantJurisdictionHelper
				.isInJurisdictionOrOwned(jurisdictionLevel, userJurisdiction, sampleJurisdiction.getEventParticipantJurisdiction());
		}

		return false;
	}
}
