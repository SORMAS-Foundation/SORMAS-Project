/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.adverseeventsfollowingimmunization;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class AefiCriteria extends BaseCriteria implements Serializable {

	public static final String I18N_PREFIX = "AefiCriteria";

	public static final String DISEASE = "disease";
	public static final String PERSON_LIKE = "personLike";
	public static final String AEFI_TYPE = "aefiType";
	public static final String VACCINE_NAME = "vaccineName";
	public static final String VACCINE_MANUFACTURER = "vaccineManufacturer";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String OUTCOME = "outcome";
	public static final String FACILITY_TYPE_GROUP = "facilityTypeGroup";
	public static final String FACILITY_TYPE = "facilityType";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String DATE_FILTER_OPTION = "dateFilterOption";
	public static final String AEFI_DATE_TYPE = "aefiDateType";
	public static final String FROM_DATE = "fromDate";
	public static final String TO_DATE = "toDate";
	public static final String RELEVANCE_STATUS = "relevanceStatus";

	private Disease disease;
	private String personLike;
	private AefiType aefiType;
	private Vaccine vaccineName;
	private VaccineManufacturer vaccineManufacturer;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private AefiOutcome outcome;
	private FacilityTypeGroup facilityTypeGroup;
	private FacilityType facilityType;
	private FacilityReferenceDto healthFacility;
	private DateFilterOption dateFilterOption = DateFilterOption.DATE;
	private AefiDateType aefiDateType;
	private Date fromDate;
	private Date toDate;
	private EntityRelevanceStatus relevanceStatus;

	private Boolean showSeriousAefiForMap = false;
	private Boolean showNonSeriousAefiForMap = false;

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getPersonLike() {
		return personLike;
	}

	public void setPersonLike(String personLike) {
		this.personLike = personLike;
	}

	public AefiType getAefiType() {
		return aefiType;
	}

	public void setAefiType(AefiType aefiType) {
		this.aefiType = aefiType;
	}

	public Vaccine getVaccineName() {
		return vaccineName;
	}

	public void setVaccineName(Vaccine vaccineName) {
		this.vaccineName = vaccineName;
	}

	public VaccineManufacturer getVaccineManufacturer() {
		return vaccineManufacturer;
	}

	public void setVaccineManufacturer(VaccineManufacturer vaccineManufacturer) {
		this.vaccineManufacturer = vaccineManufacturer;
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

	public AefiOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(AefiOutcome outcome) {
		this.outcome = outcome;
	}

	public FacilityTypeGroup getFacilityTypeGroup() {
		return facilityTypeGroup;
	}

	public void setFacilityTypeGroup(FacilityTypeGroup facilityTypeGroup) {
		this.facilityTypeGroup = facilityTypeGroup;
	}

	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
	}

	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}

	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}

	public void setDateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
	}

	public AefiDateType getAefiDateType() {
		return aefiDateType;
	}

	public void setAefiDateType(AefiDateType aefiDateType) {
		this.aefiDateType = aefiDateType;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public void setRelevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
	}

	public AefiCriteria disease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public AefiCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public AefiCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public AefiCriteria aefiType(AefiType aefiType) {
		this.aefiType = aefiType;
		return this;
	}

	public AefiCriteria showSeriousAefiForMap(Boolean showSeriousAefi) {
		this.showSeriousAefiForMap = showSeriousAefi;
		return this;
	}

	public AefiCriteria showNonSeriousAefiForMap(Boolean showNonSeriousAefi) {
		this.showNonSeriousAefiForMap = showNonSeriousAefi;
		return this;
	}

	public Boolean isShowSeriousAefiForMap() {
		return showSeriousAefiForMap;
	}

	public Boolean isShowNonSeriousAefiForMap() {
		return showNonSeriousAefiForMap;
	}
}
