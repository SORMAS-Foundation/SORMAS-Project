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

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.PseudonymizableDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class PreviousHospitalizationDto extends PseudonymizableDto {

	private static final long serialVersionUID = -7544440109802739018L;

	public static final String I18N_PREFIX = "CasePreviousHospitalization";

	public static final String ADMISSION_DATE = "admissionDate";
	public static final String DISCHARGE_DATE = "dischargeDate";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String ISOLATED = "isolated";
	public static final String DESCRIPTION = "description";

	private Date admissionDate;
	private Date dischargeDate;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	@SensitiveData
	private CommunityReferenceDto community;
	@SensitiveData
	private FacilityReferenceDto healthFacility;
	@SensitiveData
	private String healthFacilityDetails;
	private YesNoUnknown isolated;
	@SensitiveData
	private String description;

	public static PreviousHospitalizationDto build(CaseDataDto caze) {

		HospitalizationDto hospitalization = caze.getHospitalization();
		PreviousHospitalizationDto previousHospitalization = new PreviousHospitalizationDto();
		previousHospitalization.setUuid(DataHelper.createUuid());

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

		previousHospitalization.setRegion(caze.getRegion());
		previousHospitalization.setDistrict(caze.getDistrict());
		previousHospitalization.setCommunity(caze.getCommunity());
		previousHospitalization.setHealthFacility(caze.getHealthFacility());
		previousHospitalization.setIsolated(hospitalization.getIsolated());

		return previousHospitalization;
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
}
