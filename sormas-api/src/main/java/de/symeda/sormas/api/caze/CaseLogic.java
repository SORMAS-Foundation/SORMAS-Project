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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
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

	public static Date getEndDate(Date onsetDate, Date reportDate, Date followUpUntil) {
		return followUpUntil != null ? followUpUntil : onsetDate != null ? onsetDate: reportDate;
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

	/**
	 * Should be called if the facility of a case is changed
	 * 
	 * @param caze
	 * @param oldCase
	 * @param isTransfer
	 */
	public static void handleHospitalization(CaseDataDto caze, CaseDataDto oldCase, boolean isTransfer) {

		if (isTransfer && FacilityType.HOSPITAL.equals(oldCase.getFacilityType())) {
			PreviousHospitalizationDto prevHosp = PreviousHospitalizationDto.build(oldCase);
			caze.getHospitalization().getPreviousHospitalizations().add(prevHosp);
			caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
		}

		if (isTransfer || !FacilityType.HOSPITAL.equals(caze.getFacilityType())) {
			// set everything but previous hospitalization to null
			try {
				PropertyDescriptor[] pds = Introspector.getBeanInfo(HospitalizationDto.class, EntityDto.class).getPropertyDescriptors();

				for (PropertyDescriptor pd : pds) {
					// Skip properties without a read or write method
					if (pd.getWriteMethod() == null
						|| HospitalizationDto.HOSPITALIZED_PREVIOUSLY.equals(pd.getName())
						|| HospitalizationDto.PREVIOUS_HOSPITALIZATIONS.equals(pd.getName())) {
						continue;
					}

					pd.getWriteMethod().invoke(caze.getHospitalization(), (Object) null);
				}
			} catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException("Exception when trying to fill dto: " + e.getMessage(), e.getCause());
			}
		}

		if (isTransfer && FacilityType.HOSPITAL.equals(caze.getFacilityType()))

		{
			caze.getHospitalization().setAdmissionDate(new Date());
		}
	}
}
