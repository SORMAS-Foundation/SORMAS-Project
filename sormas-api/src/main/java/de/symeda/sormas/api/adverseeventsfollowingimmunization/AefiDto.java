/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.api.adverseeventsfollowingimmunization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.caze.Trimester;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.api.vaccination.VaccinationDto;

@DependingOnFeatureType(featureType = {
	FeatureType.IMMUNIZATION_MANAGEMENT,
	FeatureType.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_MANAGEMENT })
public class AefiDto extends PseudonymizableDto {

	private static final long serialVersionUID = 5023410664970514090L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 25455;

	public static final String I18N_PREFIX = "Aefi";

	public static final String IMMUNIZATION = "immunization";
	public static final String ADDRESS = "address";
	public static final String VACCINATIONS = "vaccinations";
	public static final String PRIMARY_SUSPECT_VACCINE = "primarySuspectVaccine";
	public static final String ADVERSE_EVENTS = "adverseEvents";
	public static final String PERSON = "person";
	public static final String REPORT_DATE = "reportDate";
	public static final String REPORTING_USER = "reportingUser";
	public static final String EXTERNAL_ID = "externalId";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";
	public static final String RESPONSIBLE_DISTRICT = "responsibleDistrict";
	public static final String RESPONSIBLE_COMMUNITY = "responsibleCommunity";
	public static final String COUNTRY = "country";
	public static final String REPORTING_ID_NUMBER = "reportingIdNumber";
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String PREGNANT = "pregnant";
	public static final String TRIMESTER = "trimester";
	public static final String LACTATING = "lactating";
	public static final String ONSET_AGE_YEARS = "onsetAgeYears";
	public static final String ONSET_AGE_MONTHS = "onsetAgeMonths";
	public static final String ONSET_AGE_DAYS = "onsetAgeDays";
	public static final String AGE_GROUP = "ageGroup";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String REPORTER_NAME = "reporterName";
	public static final String REPORTER_INSTITUTION = "reporterInstitution";
	public static final String REPORTER_DESIGNATION = "reporterDesignation";
	public static final String REPORTER_DEPARTMENT = "reporterDepartment";
	public static final String REPORTER_ADDRESS = "reporterAddress";
	public static final String REPORTER_PHONE = "reporterPhone";
	public static final String REPORTER_EMAIL = "reporterEmail";
	public static final String TODAYS_DATE = "todaysDate";
	public static final String START_DATE_TIME = "startDateTime";
	public static final String AEFI_DESCRIPTION = "aefiDescription";
	public static final String SERIOUS = "serious";
	public static final String SERIOUS_REASON = "seriousReason";
	public static final String SERIOUS_REASON_DETAILS = "seriousReasonDetails";
	public static final String OUTCOME = "outcome";
	public static final String DEATH_DATE = "deathDate";
	public static final String AUTOPSY_DONE = "autopsyDone";
	public static final String PAST_MEDICAL_HISTORY = "pastMedicalHistory";
	public static final String INVESTIGATION_NEEDED = "investigationNeeded";
	public static final String INVESTIGATION_PLANNED_DATE = "investigationPlannedDate";
	public static final String RECEIVED_AT_NATIONAL_LEVEL_DATE = "receivedAtNationalLevelDate";
	public static final String WORLD_WIDE_ID = "worldwideId";
	public static final String NATIONAL_LEVEL_COMMENT = "nationalLevelComment";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";

	@NotNull(message = Validations.validImmunization)
	private ImmunizationReferenceDto immunization;
	private PersonReferenceDto person;
	private LocationDto address;
	@NotEmpty(message = Validations.aefiWithoutSuspectVaccine)
	private List<VaccinationDto> vaccinations = new ArrayList<>();
	@NotNull(message = Validations.aefiWithoutPrimarySuspectVaccine)
	private VaccinationDto primarySuspectVaccine;
	@NotNull(message = Validations.aefiWithoutAdverseEvents)
	private AdverseEventsDto adverseEvents;
	@NotNull(message = Validations.validReportDateTime)
	private Date reportDate;
	private UserReferenceDto reportingUser;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String externalId;
	private RegionReferenceDto responsibleRegion;
	private DistrictReferenceDto responsibleDistrict;
	private CommunityReferenceDto responsibleCommunity;
	private CountryReferenceDto country;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String reportingIdNumber;
	private String phoneNumber;
	private YesNoUnknown pregnant;
	private Trimester trimester;
	private YesNoUnknown lactating;
	private Integer onsetAgeYears;
	private Integer onsetAgeMonths;
	private Integer onsetAgeDays;
	private AefiAgeGroup ageGroup;
	private FacilityReferenceDto healthFacility;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String healthFacilityDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String reporterName;
	private FacilityReferenceDto reporterInstitution;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String reporterDesignation;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String reporterDepartment;
	private LocationDto reporterAddress;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String reporterPhone;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String reporterEmail;
	private Date todaysDate;
	private Date startDateTime;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String aefiDescription;
	private YesNoUnknown serious;
	private SeriousAefiReason seriousReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String seriousReasonDetails;
	private AefiOutcome outcome;
	private Date deathDate;
	private YesNoUnknown autopsyDone;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String pastMedicalHistory;
	private YesNoUnknown investigationNeeded;
	private Date investigationPlannedDate;
	private Date receivedAtNationalLevelDate;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String worldwideId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String nationalLevelComment;
	private boolean archived;
	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;

	public static AefiDto build(UserReferenceDto user) {

		final AefiDto aefiDto = new AefiDto();
		aefiDto.setUuid(DataHelper.createUuid());
		aefiDto.setReportingUser(user);
		aefiDto.setReportDate(new Date());
		aefiDto.setAdverseEvents(AdverseEventsDto.build());

		return aefiDto;
	}

	public static AefiDto build(ImmunizationReferenceDto immunization) {

		final AefiDto aefiDto = new AefiDto();
		aefiDto.setUuid(DataHelper.createUuid());
		aefiDto.setImmunization(immunization);
		aefiDto.setReportDate(new Date());
		aefiDto.setAdverseEvents(AdverseEventsDto.build());

		return aefiDto;
	}

	public static AefiDto build(AefiReferenceDto aefiReferenceDto) {

		final AefiDto aefiDto = new AefiDto();
		aefiDto.setUuid(aefiReferenceDto.getUuid());
		aefiDto.setReportDate(new Date());
		aefiDto.setAdverseEvents(AdverseEventsDto.build());

		return aefiDto;
	}

	public AefiReferenceDto toReference() {
		return new AefiReferenceDto(getUuid(), getPerson().getCaption(), getExternalId());
	}

	public ImmunizationReferenceDto getImmunization() {
		return immunization;
	}

	public void setImmunization(ImmunizationReferenceDto immunization) {
		this.immunization = immunization;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public LocationDto getAddress() {
		return address;
	}

	public void setAddress(LocationDto address) {
		this.address = address;
	}

	public List<VaccinationDto> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<VaccinationDto> vaccinations) {
		this.vaccinations = vaccinations;
	}

	public AdverseEventsDto getAdverseEvents() {
		return adverseEvents;
	}

	public VaccinationDto getPrimarySuspectVaccine() {
		return primarySuspectVaccine;
	}

	public void setPrimarySuspectVaccine(VaccinationDto primarySuspectVaccine) {
		this.primarySuspectVaccine = primarySuspectVaccine;
	}

	public void setAdverseEvents(AdverseEventsDto adverseEvents) {
		this.adverseEvents = adverseEvents;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
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

	public CountryReferenceDto getCountry() {
		return country;
	}

	public void setCountry(CountryReferenceDto country) {
		this.country = country;
	}

	public String getReportingIdNumber() {
		return reportingIdNumber;
	}

	public void setReportingIdNumber(String reportingIdNumber) {
		this.reportingIdNumber = reportingIdNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public YesNoUnknown getPregnant() {
		return pregnant;
	}

	public void setPregnant(YesNoUnknown pregnant) {
		this.pregnant = pregnant;
	}

	public Trimester getTrimester() {
		return trimester;
	}

	public void setTrimester(Trimester trimester) {
		this.trimester = trimester;
	}

	public YesNoUnknown getLactating() {
		return lactating;
	}

	public void setLactating(YesNoUnknown lactating) {
		this.lactating = lactating;
	}

	public Integer getOnsetAgeYears() {
		return onsetAgeYears;
	}

	public void setOnsetAgeYears(Integer onsetAgeYears) {
		this.onsetAgeYears = onsetAgeYears;
	}

	public Integer getOnsetAgeMonths() {
		return onsetAgeMonths;
	}

	public void setOnsetAgeMonths(Integer onsetAgeMonths) {
		this.onsetAgeMonths = onsetAgeMonths;
	}

	public Integer getOnsetAgeDays() {
		return onsetAgeDays;
	}

	public void setOnsetAgeDays(Integer onsetAgeDays) {
		this.onsetAgeDays = onsetAgeDays;
	}

	public AefiAgeGroup getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(AefiAgeGroup ageGroup) {
		this.ageGroup = ageGroup;
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

	public String getReporterName() {
		return reporterName;
	}

	public void setReporterName(String reporterName) {
		this.reporterName = reporterName;
	}

	public FacilityReferenceDto getReporterInstitution() {
		return reporterInstitution;
	}

	public void setReporterInstitution(FacilityReferenceDto reporterInstitution) {
		this.reporterInstitution = reporterInstitution;
	}

	public String getReporterDesignation() {
		return reporterDesignation;
	}

	public void setReporterDesignation(String reporterDesignation) {
		this.reporterDesignation = reporterDesignation;
	}

	public String getReporterDepartment() {
		return reporterDepartment;
	}

	public void setReporterDepartment(String reporterDepartment) {
		this.reporterDepartment = reporterDepartment;
	}

	public LocationDto getReporterAddress() {
		return reporterAddress;
	}

	public void setReporterAddress(LocationDto reporterAddress) {
		this.reporterAddress = reporterAddress;
	}

	public String getReporterPhone() {
		return reporterPhone;
	}

	public void setReporterPhone(String reporterPhone) {
		this.reporterPhone = reporterPhone;
	}

	public String getReporterEmail() {
		return reporterEmail;
	}

	public void setReporterEmail(String reporterEmail) {
		this.reporterEmail = reporterEmail;
	}

	public Date getTodaysDate() {
		return todaysDate;
	}

	public void setTodaysDate(Date todaysDate) {
		this.todaysDate = todaysDate;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getAefiDescription() {
		return aefiDescription;
	}

	public void setAefiDescription(String aefiDescription) {
		this.aefiDescription = aefiDescription;
	}

	public YesNoUnknown getSerious() {
		return serious;
	}

	public void setSerious(YesNoUnknown serious) {
		this.serious = serious;
	}

	public SeriousAefiReason getSeriousReason() {
		return seriousReason;
	}

	public void setSeriousReason(SeriousAefiReason seriousReason) {
		this.seriousReason = seriousReason;
	}

	public String getSeriousReasonDetails() {
		return seriousReasonDetails;
	}

	public void setSeriousReasonDetails(String seriousReasonDetails) {
		this.seriousReasonDetails = seriousReasonDetails;
	}

	public AefiOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(AefiOutcome outcome) {
		this.outcome = outcome;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public YesNoUnknown getAutopsyDone() {
		return autopsyDone;
	}

	public void setAutopsyDone(YesNoUnknown autopsyDone) {
		this.autopsyDone = autopsyDone;
	}

	public String getPastMedicalHistory() {
		return pastMedicalHistory;
	}

	public void setPastMedicalHistory(String pastMedicalHistory) {
		this.pastMedicalHistory = pastMedicalHistory;
	}

	public YesNoUnknown getInvestigationNeeded() {
		return investigationNeeded;
	}

	public void setInvestigationNeeded(YesNoUnknown investigationNeeded) {
		this.investigationNeeded = investigationNeeded;
	}

	public Date getInvestigationPlannedDate() {
		return investigationPlannedDate;
	}

	public void setInvestigationPlannedDate(Date investigationPlannedDate) {
		this.investigationPlannedDate = investigationPlannedDate;
	}

	public Date getReceivedAtNationalLevelDate() {
		return receivedAtNationalLevelDate;
	}

	public void setReceivedAtNationalLevelDate(Date receivedAtNationalLevelDate) {
		this.receivedAtNationalLevelDate = receivedAtNationalLevelDate;
	}

	public String getWorldwideId() {
		return worldwideId;
	}

	public void setWorldwideId(String worldwideId) {
		this.worldwideId = worldwideId;
	}

	public String getNationalLevelComment() {
		return nationalLevelComment;
	}

	public void setNationalLevelComment(String nationalLevelComment) {
		this.nationalLevelComment = nationalLevelComment;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
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
