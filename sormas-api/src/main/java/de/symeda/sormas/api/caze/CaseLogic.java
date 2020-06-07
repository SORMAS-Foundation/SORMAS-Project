/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.caze;

import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.YesNoUnknown;

public final class CaseLogic {

	private CaseLogic() {
		// Hide Utility Class Constructor
	}

	private static final String EPID_PATTERN_COMPLETE = "([A-Z]{3}-){3}[0-9]{2}-[0-9]+";
	private static final String EPID_PATTERN_PREFIX = "([A-Z]{3}-){3}[0-9]{2}-";

	public static void validateInvestigationDoneAllowed(CaseDataDto caze) throws ValidationException {

		if (caze.getCaseClassification() == CaseClassification.NOT_CLASSIFIED) {
			throw new ValidationException("Not allowed to set investigation status to done for an unclassified case.");
		}
	}

	public static Date getStartDate(Date onsetDate, Date reportDate) {

		if (onsetDate != null) {
			return onsetDate;
		} else {
			return reportDate;
		}
	}

	public static boolean isEpidNumberPrefix(String s) {

		if (StringUtils.isEmpty(s)) {
			return false;
		}

		return Pattern.matches(EPID_PATTERN_PREFIX, s);
	}

	public static boolean isCompleteEpidNumber(String s) {

		if (StringUtils.isEmpty(s)) {
			return false;
		}

		return Pattern.matches(EPID_PATTERN_COMPLETE, s);
	}

	public static void createPreviousHospitalizationAndUpdateHospitalization(CaseDataDto caze, CaseDataDto oldCase) {

		PreviousHospitalizationDto prevHosp = PreviousHospitalizationDto.build(oldCase);
		caze.getHospitalization().getPreviousHospitalizations().add(prevHosp);
		caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
		caze.getHospitalization().setAdmissionDate(new Date());
		caze.getHospitalization().setDischargeDate(null);
		caze.getHospitalization().setIsolated(null);
	}
}
