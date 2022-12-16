/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.immunization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.Required;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import io.swagger.v3.oas.annotations.media.Schema;

@DependingOnFeatureType(featureType = FeatureType.IMMUNIZATION_MANAGEMENT)
@Schema(description = "Data transfer object for immunization information")
public class ImmunizationDto extends SormasToSormasShareableDto {

	private static final long serialVersionUID = -6538566879882613529L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 25455;

	public static final String I18N_PREFIX = "Immunization";

	public static final String ADDITIONAL_DETAILS = "additionalDetails";
	public static final String COUNTRY = "country";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String END_DATE = "endDate";
	public static final String EXTERNAL_ID = "externalId";
	public static final String FACILITY_TYPE = "facilityType";
	public static final String FIRST_VACCINATION_DATE = "firstVaccinationDate";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String IMMUNIZATION_STATUS = "immunizationStatus";
	public static final String LAST_INFECTION_DATE = "lastInfectionDate";
	public static final String LAST_VACCINATION_DATE = "lastVaccinationDate";
	public static final String IMMUNIZATION_MANAGEMENT_STATUS = "immunizationManagementStatus";
	public static final String MEANS_OF_IMMUNIZATION = "meansOfImmunization";
	public static final String MEANS_OF_IMMUNIZATION_DETAILS = "meansOfImmunizationDetails";
	public static final String NUMBER_OF_DOSES = "numberOfDoses";
	public static final String NUMBER_OF_DOSES_DETAILS = "numberOfDosesDetails";
	public static final String PERSON = "person";
	public static final String POSITIVE_TEST_RESULT_DATE = "positiveTestResultDate";
	public static final String PREVIOUS_INFECTION = "previousInfection";
	public static final String RECOVERY_DATE = "recoveryDate";
	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE = "reportDate";
	public static final String RESPONSIBLE_COMMUNITY = "responsibleCommunity";
	public static final String RESPONSIBLE_DISTRICT = "responsibleDistrict";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";
	public static final String START_DATE = "startDate";
	public static final String VALID_FROM = "validFrom";
	public static final String VALID_UNTIL = "validUntil";
	public static final String VACCINATIONS = "vaccinations";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	@Outbreaks
	@Required
	private Disease disease;
	@Outbreaks
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Additional information about the disease")
	private String diseaseDetails;
	@Required
	@EmbeddedPersonalData
	private PersonReferenceDto person;
	@Required
	@Schema(description = "The date on which the vaccination is reported")
	private Date reportDate;
	private UserReferenceDto reportingUser;
	@Schema(description = "Indicates whether this immunization has been archived")
	private boolean archived;
	@Required
	private ImmunizationStatus immunizationStatus;
	@Required
	private MeansOfImmunization meansOfImmunization;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@SensitiveData(mandatoryField = true)
	@Schema(description = "Additional information about means of immunization. This field is required if the means of immunization is 'OTHER'.")
	private String meansOfImmunizationDetails;
	@Required
	private ImmunizationManagementStatus immunizationManagementStatus;
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	@SensitiveData(mandatoryField = true)
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private String externalId;

	@Required
	private RegionReferenceDto responsibleRegion;
	@Required
	private DistrictReferenceDto responsibleDistrict;
	@PersonalData
	@SensitiveData
	private CommunityReferenceDto responsibleCommunity;
	private CountryReferenceDto country;

	@PersonalData
	@SensitiveData
	private FacilityType facilityType;
	@Outbreaks
	@PersonalData
	@SensitiveData
	private FacilityReferenceDto healthFacility;
	@Outbreaks
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "Free text details about the facility, like name or description")
	private String healthFacilityDetails;

	@Schema(description = "Start date of the immunization")
	private Date startDate;
	@Schema(description = "End date of the immunization")
	private Date endDate;
	@Schema(description = "Number of doses of vaccine given for immunization. "
		+ "This field is required only if the means of immunization is vaccination.")
	private Integer numberOfDoses;
	@Schema(description = "Free text of the information on the number of doses of vaccine given for immunization. "
		+ "This field is required only if the means of immunization is vaccination.")
	private String numberOfDosesDetails;
	@Schema(description = "Indicates if there was a previous infection")
	private YesNoUnknown previousInfection;

	@Schema(description = "Date of last infection")
	private Date lastInfectionDate;
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@SensitiveData
	@Schema(description = "General comments about the immunization")
	private String additionalDetails;
	@Schema(description = "Date of first positive test result")
	private Date positiveTestResultDate;
	@Schema(description = "Date of recovery")
	private Date recoveryDate;
	@Schema(description = "Date from when immunization is valid")
	private Date validFrom;
	@Schema(description = "Date until when the immunization is valid")
	private Date validUntil;

	private CaseReferenceDto relatedCase;

	@Schema(description = "Whether this immunization entry has been deleted")
	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Detailed deletion reason other than proposed reasons.")
	private String otherDeletionReason;

	@Valid
	private List<VaccinationDto> vaccinations = new ArrayList<>();

	public static ImmunizationDto build(PersonReferenceDto person) {

		final ImmunizationDto immunizationDto = new ImmunizationDto();
		immunizationDto.setUuid(DataHelper.createUuid());
		immunizationDto.setPerson(person);
		immunizationDto.setReportDate(new Date());
		immunizationDto.setImmunizationManagementStatus(ImmunizationManagementStatus.SCHEDULED);

		return immunizationDto;
	}

	public ImmunizationReferenceDto toReference() {
		return new ImmunizationReferenceDto(getUuid(), getPerson().getCaption(), getExternalId());
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@Override
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	@Override
	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public ImmunizationStatus getImmunizationStatus() {
		return immunizationStatus;
	}

	public void setImmunizationStatus(ImmunizationStatus immunizationStatus) {
		this.immunizationStatus = immunizationStatus;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public RegionReferenceDto getResponsibleRegion() {
		return responsibleRegion;
	}

	public void setResponsibleRegion(RegionReferenceDto responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public DistrictReferenceDto getResponsibleDistrict() {
		return responsibleDistrict;
	}

	public void setResponsibleDistrict(DistrictReferenceDto responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public CommunityReferenceDto getResponsibleCommunity() {
		return responsibleCommunity;
	}

	public void setResponsibleCommunity(CommunityReferenceDto responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
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

	public String getHealthFacilityDetails() {
		return healthFacilityDetails;
	}

	public void setHealthFacilityDetails(String healthFacilityDetails) {
		this.healthFacilityDetails = healthFacilityDetails;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getNumberOfDoses() {
		return numberOfDoses;
	}

	public void setNumberOfDoses(Integer numberOfDoses) {
		this.numberOfDoses = numberOfDoses;
	}

	public String getNumberOfDosesDetails() {
		return numberOfDosesDetails;
	}

	public void setNumberOfDosesDetails(String numberOfDosesDetails) {
		this.numberOfDosesDetails = numberOfDosesDetails;
	}

	public YesNoUnknown getPreviousInfection() {
		return previousInfection;
	}

	public void setPreviousInfection(YesNoUnknown previousInfection) {
		this.previousInfection = previousInfection;
	}

	public Date getLastInfectionDate() {
		return lastInfectionDate;
	}

	public void setLastInfectionDate(Date lastInfectionDate) {
		this.lastInfectionDate = lastInfectionDate;
	}

	public String getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public Date getPositiveTestResultDate() {
		return positiveTestResultDate;
	}

	public void setPositiveTestResultDate(Date positiveTestResultDate) {
		this.positiveTestResultDate = positiveTestResultDate;
	}

	public Date getRecoveryDate() {
		return recoveryDate;
	}

	public void setRecoveryDate(Date recoveryDate) {
		this.recoveryDate = recoveryDate;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}

	public CaseReferenceDto getRelatedCase() {
		return relatedCase;
	}

	public void setRelatedCase(CaseReferenceDto relatedCase) {
		this.relatedCase = relatedCase;
	}

	public MeansOfImmunization getMeansOfImmunization() {
		return meansOfImmunization;
	}

	public void setMeansOfImmunization(MeansOfImmunization meansOfImmunization) {
		this.meansOfImmunization = meansOfImmunization;
	}

	public ImmunizationManagementStatus getImmunizationManagementStatus() {
		return immunizationManagementStatus;
	}

	public void setImmunizationManagementStatus(ImmunizationManagementStatus immunizationManagementStatus) {
		this.immunizationManagementStatus = immunizationManagementStatus;
	}

	public String getMeansOfImmunizationDetails() {
		return meansOfImmunizationDetails;
	}

	public void setMeansOfImmunizationDetails(String meansOfImmunizationDetails) {
		this.meansOfImmunizationDetails = meansOfImmunizationDetails;
	}

	public CountryReferenceDto getCountry() {
		return country;
	}

	public void setCountry(CountryReferenceDto country) {
		this.country = country;
	}

	public List<VaccinationDto> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<VaccinationDto> vaccinations) {
		this.vaccinations = vaccinations;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}
}
