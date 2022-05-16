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
package de.symeda.sormas.api.hospitalization;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import java.util.Date;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = FeatureType.CASE_SURVEILANCE)
public class PreviousHospitalizationDto extends PseudonymizableDto {

	private static final long serialVersionUID = -7544440109802739018L;

	public static final String I18N_PREFIX = "CasePreviousHospitalization";

	public static final String ADMITTED_TO_HEALTH_FACILITY = "admittedToHealthFacility";
	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String ISOLATED = "isolated";
	public static final String ISOLATION_DATE = "isolationDate";
	public static final String DESCRIPTION = "description";
	public static final String HOSPITALIZATION_REASON = "hospitalizationReason";
	public static final String OTHER_HOSPITALIZATION_REASON = "otherHospitalizationReason";
	public static final String INTENSIVE_CARE_UNIT = "intensiveCareUnit";
	public static final String INTENSIVE_CARE_UNIT_START = "intensiveCareUnitStart";
	public static final String INTENSIVE_CARE_UNIT_END = "intensiveCareUnitEnd";

	private YesNoUnknown admittedToHealthFacility;
	private Date admissionDate;
	private Date dischargeDate;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	@SensitiveData
	private CommunityReferenceDto community;
	@SensitiveData
	private FacilityReferenceDto healthFacility;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String healthFacilityDetails;
	private YesNoUnknown isolated;
	private Date isolationDate;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String description;

	private HospitalizationReasonType hospitalizationReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherHospitalizationReason;

	private YesNoUnknown intensiveCareUnit;
	private Date intensiveCareUnitStart;
	private Date intensiveCareUnitEnd;

	public static PreviousHospitalizationDto build(CaseDataDto caze) {

		HospitalizationDto hospitalization = caze.getHospitalization();
		PreviousHospitalizationDto previousHospitalization = new PreviousHospitalizationDto();
		previousHospitalization.setUuid(DataHelper.createUuid());

		previousHospitalization.setAdmittedToHealthFacility(hospitalization.getAdmittedToHealthFacility());
		if (hospitalization.getAdmissionDate() != null) {
			previousHospitalization.setAdmissionDate(hospitalization.getAdmissionDate());
		} else {
			previousHospitalization.setAdmissionDate(caze.getReportDate());
		}

		if (hospitalization.getDischargeDate() != null) {
			previousHospitalization.setDischargeDate(hospitalization.getDischargeDate());
		} else {
			previousHospitalization.setDischargeDate(new Date());
		}

		previousHospitalization.setRegion(CaseLogic.getRegionWithFallback(caze));
		previousHospitalization.setDistrict(CaseLogic.getDistrictWithFallback(caze));
		previousHospitalization.setCommunity(CaseLogic.getCommunityWithFallback(caze));
		previousHospitalization.setHealthFacility(caze.getHealthFacility());
		previousHospitalization.setHealthFacilityDetails(caze.getHealthFacilityDetails());
		previousHospitalization.setIsolated(hospitalization.getIsolated());
		previousHospitalization.setIsolationDate(hospitalization.getIsolationDate());
		previousHospitalization.setHospitalizationReason(hospitalization.getHospitalizationReason());
		previousHospitalization.setOtherHospitalizationReason(hospitalization.getOtherHospitalizationReason());
		previousHospitalization.setIntensiveCareUnit(hospitalization.getIntensiveCareUnit());
		previousHospitalization.setIntensiveCareUnitStart(hospitalization.getIntensiveCareUnitStart());
		previousHospitalization.setIntensiveCareUnitEnd(hospitalization.getIntensiveCareUnitEnd());
		previousHospitalization.setDescription(hospitalization.getDescription());

		return previousHospitalization;
	}

	public YesNoUnknown getAdmittedToHealthFacility() {
		return admittedToHealthFacility;
	}

	public void setAdmittedToHealthFacility(YesNoUnknown admittedToHealthFacility) {
		this.admittedToHealthFacility = admittedToHealthFacility;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}

	public YesNoUnknown getIsolated() {
		return isolated;
	}

	public void setIsolated(YesNoUnknown isolated) {
		this.isolated = isolated;
	}

	public Date getIsolationDate() {
		return isolationDate;
	}

	public void setIsolationDate(Date isolationDate) {
		this.isolationDate = isolationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHealthFacilityDetails() {
		return healthFacilityDetails;
	}

	public void setHealthFacilityDetails(String healthFacilityDetails) {
		this.healthFacilityDetails = healthFacilityDetails;
	}

	public HospitalizationReasonType getHospitalizationReason() {
		return hospitalizationReason;
	}

	public void setHospitalizationReason(HospitalizationReasonType hospitalizationReason) {
		this.hospitalizationReason = hospitalizationReason;
	}

	public String getOtherHospitalizationReason() {
		return otherHospitalizationReason;
	}

	public void setOtherHospitalizationReason(String otherHospitalizationReason) {
		this.otherHospitalizationReason = otherHospitalizationReason;
	}

	public YesNoUnknown getIntensiveCareUnit() {
		return intensiveCareUnit;
	}

	public void setIntensiveCareUnit(YesNoUnknown intensiveCareUnit) {
		this.intensiveCareUnit = intensiveCareUnit;
	}

	public Date getIntensiveCareUnitStart() {
		return intensiveCareUnitStart;
	}

	public void setIntensiveCareUnitStart(Date intensiveCareUnitStart) {
		this.intensiveCareUnitStart = intensiveCareUnitStart;
	}

	public Date getIntensiveCareUnitEnd() {
		return intensiveCareUnitEnd;
	}

	public void setIntensiveCareUnitEnd(Date intensiveCareUnitEnd) {
		this.intensiveCareUnitEnd = intensiveCareUnitEnd;
	}
}
