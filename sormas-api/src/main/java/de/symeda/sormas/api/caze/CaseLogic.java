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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.followup.FollowUpPeriodDto;
import de.symeda.sormas.api.followup.FollowUpStartDateType;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitDto;

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

	public static Date getStartDate(CaseDataDto caseDto) {
		return getStartDate(caseDto.getSymptoms().getOnsetDate(), caseDto.getReportDate());
	}

	public static Date getStartDate(Date onsetDate, Date reportDate) {
		return onsetDate != null ? onsetDate : reportDate;
	}

	public static FollowUpPeriodDto getFollowUpStartDate(CaseDataDto caseDto, List<SampleDto> samples) {
		return getFollowUpStartDate(caseDto.getSymptoms().getOnsetDate(), caseDto.getReportDate(), samples);
	}

	public static FollowUpPeriodDto getFollowUpStartDate(Date onsetDate, Date reportDate, List<SampleDto> samples) {

		if (onsetDate != null) {
			return new FollowUpPeriodDto(onsetDate, FollowUpStartDateType.SYMPTOM_ONSET_DATE);
		}
		return FollowUpLogic.getFollowUpStartDate(reportDate, samples);
	}

	public static FollowUpPeriodDto getFollowUpStartDate(Date onsetDate, Date reportDate, Date earliestSampleDate) {

		if (onsetDate != null) {
			return new FollowUpPeriodDto(onsetDate, FollowUpStartDateType.SYMPTOM_ONSET_DATE);
		}
		return FollowUpLogic.getFollowUpStartDate(reportDate, earliestSampleDate);
	}

	public static Date getEndDate(Date onsetDate, Date reportDate, Date followUpUntil) {
		return followUpUntil != null ? followUpUntil : onsetDate != null ? onsetDate : reportDate;
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
	 * Handles the hospitalization change of a case.
	 *
	 * @param caze
	 *            The new CaseDataDto for which the facility change should be handled.
	 * @param oldCase
	 *            The Dto of the existing case being changed.
	 * @param isTransfer
	 *            Indicates if the old case is transferred (both from or to a hospital).
	 */
	public static void handleHospitalization(CaseDataDto caze, CaseDataDto oldCase, boolean isTransfer) {
		// todo (@JonasCir) I feel this whole class or at least this method should be absorbed by the case EJB
		// case is already in a hospital and is transferred from it (discharge or other hospital)...
		if (isTransfer && FacilityType.HOSPITAL.equals(oldCase.getFacilityType())) {
			// therefore add the old hospitalization to the list of previous ones
			PreviousHospitalizationDto prevHosp = PreviousHospitalizationDto.build(oldCase);
			caze.getHospitalization().getPreviousHospitalizations().add(prevHosp);
			caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
		}

		// clear everything if a case is transferred or discharged from a hospital
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

		// case gets transferred to a hospital
		if (isTransfer && FacilityType.HOSPITAL.equals(caze.getFacilityType())) {
			caze.getHospitalization().setAdmissionDate(new Date());
		}
	}

	/**
	 * Calculates the follow-up until date of the case based on its start date (onset contact or report date), the follow-up duration of
	 * the disease, the current follow-up until date and the date of the last cooperative visit.
	 *
	 * @param ignoreOverwrite
	 *            Ignores current follow-up until date and whether or not follow-up until has been overwritten.
	 */
	public static FollowUpPeriodDto calculateFollowUpUntilDate(
		CaseDataDto caze,
		FollowUpPeriodDto followUpPeriod,
		List<VisitDto> visits,
		int followUpDuration,
		boolean ignoreOverwrite,
		boolean allowFreeOverwrite) {

		Date overwriteUntilDate = !ignoreOverwrite && caze.isOverwriteFollowUpUntil() ? caze.getFollowUpUntil() : null;
		return FollowUpLogic.calculateFollowUpUntilDate(followUpPeriod, overwriteUntilDate, visits, followUpDuration, allowFreeOverwrite);
	}

	public static RegionReferenceDto getRegionWithFallback(CaseDataDto caze) {
		if (caze.getRegion() == null) {
			return caze.getResponsibleRegion();
		}

		return caze.getRegion();
	}

	public static DistrictReferenceDto getDistrictWithFallback(CaseDataDto caze) {
		if (caze.getDistrict() == null) {
			return caze.getResponsibleDistrict();
		}

		return caze.getDistrict();
	}

	public static CommunityReferenceDto getCommunityWithFallback(CaseDataDto caze) {
		if (caze.getRegion() == null) {
			return caze.getResponsibleCommunity();
		}

		return caze.getCommunity();
	}

	public static ReinfectionStatus calculateReinfectionStatus(Map<ReinfectionDetail, Boolean> reinfectionDetails) {

		if (reinfectionDetails == null) {
			return null;
		}

		if (reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN, false)
			&& reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_SEQUENCE_CURRENT_INFECTION_KNOWN, false)
			&& reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_SEQUENCES_NOT_MATCHING, false)) {
			return ReinfectionStatus.CONFIRMED;
		}

		if (!(reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_SEQUENCE_PREVIOUS_INFECTION_KNOWN, false)
			&& reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_SEQUENCE_CURRENT_INFECTION_KNOWN, false))
			&& (reinfectionDetails.getOrDefault(ReinfectionDetail.ACUTE_RESPIRATORY_ILLNESS_OVERCOME, false)
				|| reinfectionDetails.getOrDefault(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION, false))
			&& (reinfectionDetails.getOrDefault(ReinfectionDetail.TESTED_NEGATIVE_AFTER_PREVIOUS_INFECTION, false)
				|| reinfectionDetails.getOrDefault(ReinfectionDetail.LAST_PCR_DETECTION_NOT_RECENT, false))
			&& reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_COPY_NUMBER_ABOVE_THRESHOLD, false)) {
			return ReinfectionStatus.PROBABLE;
		}

		if ((reinfectionDetails.getOrDefault(ReinfectionDetail.ACUTE_RESPIRATORY_ILLNESS_OVERCOME, false)
			|| reinfectionDetails.getOrDefault(ReinfectionDetail.PREVIOUS_ASYMPTOMATIC_INFECTION, false))
			&& (reinfectionDetails.getOrDefault(ReinfectionDetail.TESTED_NEGATIVE_AFTER_PREVIOUS_INFECTION, false)
				|| reinfectionDetails.getOrDefault(ReinfectionDetail.LAST_PCR_DETECTION_NOT_RECENT, false))
			&& reinfectionDetails.getOrDefault(ReinfectionDetail.GENOME_COPY_NUMBER_BELOW_THRESHOLD, false)) {
			return ReinfectionStatus.POSSIBLE;
		}

		return null;
	}
}
