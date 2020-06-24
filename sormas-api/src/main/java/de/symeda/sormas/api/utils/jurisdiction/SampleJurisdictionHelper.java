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

		switch (jurisdictionLevel) {
		case NONE:
			return false;
		case NATION:
			return true;
		case LABORATORY:
			return sampleJurisdiction.getLabUuid() != null && DataHelper.equal(sampleJurisdiction.getLabUuid(), userJurisdiction.getLabUuid());
		}

		if (sampleJurisdiction.getCaseJurisdiction() != null) {
			return CaseJurisdictionHelper.isInJurisdiction(jurisdictionLevel, userJurisdiction, sampleJurisdiction.getCaseJurisdiction());
		}

		if (sampleJurisdiction.getContactJurisdiction() != null) {
			return ContactJurisdictionHelper.isInJurisdiction(jurisdictionLevel, userJurisdiction, sampleJurisdiction.getContactJurisdiction());
		}

		return false;
	}
}
