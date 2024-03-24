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

package de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiAgeGroup;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiOutcome;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.SeriousAefiReason;
import de.symeda.sormas.api.caze.Trimester;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.vaccination.Vaccination;

@Entity
@Table(name = "adverseeventsfollowingimmunization")
public class Aefi extends CoreAdo {

	private static final long serialVersionUID = -7845660472641846292L;

	public static final String TABLE_NAME = "adverseeventsfollowingimmunization";
	public static final String AEFI_VACCINATIONS_TABLE_NAME = "adverseeventsfollowingimmunization_vaccinations";

	public static final String IMMUNIZATION = "immunization";
	public static final String IMMUNIZATION_ID = "immunizationId";
	public static final String ADDRESS = "address";
	public static final String VACCINATIONS = "vaccinations";
	public static final String PRIMARY_SUSPECT_VACCINE = "primarySuspectVaccine";
	public static final String ADVERSE_EVENTS = "adverseEvents";
	public static final String PERSON = "person";
	public static final String PERSON_ID = "personId";
	public static final String REPORT_DATE = "reportDate";
	public static final String REPORTING_USER = "reportingUser";
	public static final String EXTERNAL_ID = "externalId";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";
	public static final String RESPONSIBLE_DISTRICT = "responsibleDistrict";
	public static final String RESPONSIBLE_COMMUNITY = "responsibleCommunity";
	public static final String COUNTRY = "country";
	public static final String REPORTINGID_NUMBER = "reportingIdNumber";
	public static final String PREGNANT = "pregnant";
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String TRIMESTER = "trimester";
	public static final String LACTATING = "lactating";
	public static final String ONSET_AGE_YEARS = "onsetAgeYears";
	public static final String ONSET_AGE_MONTHS = "onsetAgeMonths";
	public static final String ONSET_AGE_DAYS = "onsetAgeDays";
	public static final String AGE_GROUP = "ageGroup";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String REPORTING_OFFICER_NAME = "reportingOfficerName";
	public static final String REPORTING_OFFICER_FACILITY = "reportingOfficerFacility";
	public static final String REPORTING_OFFICER_DESIGNATION = "reportingOfficerDesignation";
	public static final String REPORTING_OFFICER_DEPARTMENT = "reportingOfficerDepartment";
	public static final String REPORTING_OFFICER_ADDRESS = "reportingOfficerAddress";
	public static final String REPORTING_OFFICER_PHONE_NUMBER = "reportingOfficerPhoneNumber";
	public static final String REPORTING_OFFICER_EMAIL = "reportingOfficerEmail";
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

	private Immunization immunization;
	private Long immunizationId;
	private Person person;
	private Long personId;
	private Location address;
	private Set<Vaccination> vaccinations = new HashSet<>();
	private Vaccination primarySuspectVaccine;
	private AdverseEvents adverseEvents;
	private Date reportDate;
	private User reportingUser;
	private String externalId;
	private Region responsibleRegion;
	private District responsibleDistrict;
	private Community responsibleCommunity;
	private Country country;
	private String reportingIdNumber;
	private String phoneNumber;
	private YesNoUnknown pregnant;
	private Trimester trimester;
	private YesNoUnknown lactating;
	private Integer onsetAgeYears;
	private Integer onsetAgeMonths;
	private Integer onsetAgeDays;
	private AefiAgeGroup ageGroup;
	private Facility healthFacility;
	private String healthFacilityDetails;
	private String reportingOfficerName;
	private Facility reportingOfficerFacility;
	private String reportingOfficerDesignation;
	private String reportingOfficerDepartment;
	private Location reportingOfficerAddress;
	private String reportingOfficerPhoneNumber;
	private String reportingOfficerEmail;
	private Date todaysDate;
	private Date startDateTime;
	private String aefiDescription;
	private YesNoUnknown serious;
	private SeriousAefiReason seriousReason;
	private String seriousReasonDetails;
	private AefiOutcome outcome;
	private Date deathDate;
	private YesNoUnknown autopsyDone;
	private String pastMedicalHistory;
	private YesNoUnknown investigationNeeded;
	private Date investigationPlannedDate;
	private Date receivedAtNationalLevelDate;
	private String worldwideId;
	private String nationalLevelComment;

	public static Aefi build() {
		Aefi aefi = new Aefi();
		return aefi;
	}

	@ManyToOne
	@JoinColumn(nullable = false)
	public Immunization getImmunization() {
		return immunization;
	}

	public void setImmunization(Immunization immunization) {
		this.immunization = immunization;
	}

	@Column(name = "immunization_id", updatable = false, insertable = false)
	public Long getImmunizationId() {
		return immunizationId;
	}

	public void setImmunizationId(Long immunizationId) {
		this.immunizationId = immunizationId;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = AEFI_VACCINATIONS_TABLE_NAME,
		joinColumns = @JoinColumn(name = "adverseeventsfollowingimmunization_id"),
		inverseJoinColumns = @JoinColumn(name = "vaccination_id"))
	public Set<Vaccination> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(Set<Vaccination> vaccinations) {
		this.vaccinations = vaccinations;
	}

	@OneToOne
	public Vaccination getPrimarySuspectVaccine() {
		return primarySuspectVaccine;
	}

	public void setPrimarySuspectVaccine(Vaccination primarySuspectVaccine) {
		this.primarySuspectVaccine = primarySuspectVaccine;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	//@ManyToOne
	public AdverseEvents getAdverseEvents() {
		return adverseEvents;
	}

	public void setAdverseEvents(AdverseEvents adverseEvents) {
		this.adverseEvents = adverseEvents;
	}

	@ManyToOne
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Column(name = "person_id", updatable = false, insertable = false)
	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Location getAddress() {
		if (address == null) {
			address = new Location();
		}
		return address;
	}

	public void setAddress(Location address) {
		this.address = address;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getExternalId() {
		return externalId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Region getResponsibleRegion() {
		return responsibleRegion;
	}

	public void setResponsibleRegion(Region responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public District getResponsibleDistrict() {
		return responsibleDistrict;
	}

	public void setResponsibleDistrict(District responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Community getResponsibleCommunity() {
		return responsibleCommunity;
	}

	public void setResponsibleCommunity(Community responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReportingIdNumber() {
		return reportingIdNumber;
	}

	public void setReportingIdNumber(String reportingIdNumber) {
		this.reportingIdNumber = reportingIdNumber;
	}

	@Column
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPregnant() {
		return pregnant;
	}

	public void setPregnant(YesNoUnknown pregnant) {
		this.pregnant = pregnant;
	}

	@Enumerated(EnumType.STRING)
	public Trimester getTrimester() {
		return trimester;
	}

	public void setTrimester(Trimester trimester) {
		this.trimester = trimester;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getLactating() {
		return lactating;
	}

	public void setLactating(YesNoUnknown lactating) {
		this.lactating = lactating;
	}

	@Column
	public Integer getOnsetAgeYears() {
		return onsetAgeYears;
	}

	public void setOnsetAgeYears(Integer onsetAgeYears) {
		this.onsetAgeYears = onsetAgeYears;
	}

	@Column
	public Integer getOnsetAgeMonths() {
		return onsetAgeMonths;
	}

	public void setOnsetAgeMonths(Integer onsetAgeMonths) {
		this.onsetAgeMonths = onsetAgeMonths;
	}

	@Column
	public Integer getOnsetAgeDays() {
		return onsetAgeDays;
	}

	public void setOnsetAgeDays(Integer onsetAgeDays) {
		this.onsetAgeDays = onsetAgeDays;
	}

	@Enumerated(EnumType.STRING)
	public AefiAgeGroup getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(AefiAgeGroup ageGroup) {
		this.ageGroup = ageGroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Facility getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getHealthFacilityDetails() {
		return healthFacilityDetails;
	}

	public void setHealthFacilityDetails(String healthFacilityDetails) {
		this.healthFacilityDetails = healthFacilityDetails;
	}

	@Column
	public String getReportingOfficerName() {
		return reportingOfficerName;
	}

	public void setReportingOfficerName(String reporterName) {
		this.reportingOfficerName = reporterName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Facility getReportingOfficerFacility() {
		return reportingOfficerFacility;
	}

	public void setReportingOfficerFacility(Facility reporterInstitution) {
		this.reportingOfficerFacility = reporterInstitution;
	}

	@Column
	public String getReportingOfficerDesignation() {
		return reportingOfficerDesignation;
	}

	public void setReportingOfficerDesignation(String reporterDesignation) {
		this.reportingOfficerDesignation = reporterDesignation;
	}

	@Column
	public String getReportingOfficerDepartment() {
		return reportingOfficerDepartment;
	}

	public void setReportingOfficerDepartment(String reporterDepartment) {
		this.reportingOfficerDepartment = reporterDepartment;
	}

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "reportingofficeraddress_id")
	public Location getReportingOfficerAddress() {
		return reportingOfficerAddress;
	}

	public void setReportingOfficerAddress(Location reporterAddress) {
		this.reportingOfficerAddress = reporterAddress;
	}

	@Column
	public String getReportingOfficerPhoneNumber() {
		return reportingOfficerPhoneNumber;
	}

	public void setReportingOfficerPhoneNumber(String reporterPhone) {
		this.reportingOfficerPhoneNumber = reporterPhone;
	}

	@Column
	public String getReportingOfficerEmail() {
		return reportingOfficerEmail;
	}

	public void setReportingOfficerEmail(String reporterEmail) {
		this.reportingOfficerEmail = reporterEmail;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTodaysDate() {
		return todaysDate;
	}

	public void setTodaysDate(Date todaysDate) {
		this.todaysDate = todaysDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	@Column(columnDefinition = "text")
	public String getAefiDescription() {
		return aefiDescription;
	}

	public void setAefiDescription(String aefiDescription) {
		this.aefiDescription = aefiDescription;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public YesNoUnknown getSerious() {
		return serious;
	}

	public void setSerious(YesNoUnknown serious) {
		this.serious = serious;
	}

	@Enumerated(EnumType.STRING)
	public SeriousAefiReason getSeriousReason() {
		return seriousReason;
	}

	public void setSeriousReason(SeriousAefiReason seriousReason) {
		this.seriousReason = seriousReason;
	}

	@Column(columnDefinition = "text")
	public String getSeriousReasonDetails() {
		return seriousReasonDetails;
	}

	public void setSeriousReasonDetails(String seriousReasonDetails) {
		this.seriousReasonDetails = seriousReasonDetails;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public AefiOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(AefiOutcome outcome) {
		this.outcome = outcome;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAutopsyDone() {
		return autopsyDone;
	}

	public void setAutopsyDone(YesNoUnknown autopsyDone) {
		this.autopsyDone = autopsyDone;
	}

	@Column(columnDefinition = "text")
	public String getPastMedicalHistory() {
		return pastMedicalHistory;
	}

	public void setPastMedicalHistory(String pastMedicalHistory) {
		this.pastMedicalHistory = pastMedicalHistory;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getInvestigationNeeded() {
		return investigationNeeded;
	}

	public void setInvestigationNeeded(YesNoUnknown investigationNeeded) {
		this.investigationNeeded = investigationNeeded;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getInvestigationPlannedDate() {
		return investigationPlannedDate;
	}

	public void setInvestigationPlannedDate(Date investigationPlannedDate) {
		this.investigationPlannedDate = investigationPlannedDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getReceivedAtNationalLevelDate() {
		return receivedAtNationalLevelDate;
	}

	public void setReceivedAtNationalLevelDate(Date receivedAtNationalLevelDate) {
		this.receivedAtNationalLevelDate = receivedAtNationalLevelDate;
	}

	@Column
	public String getWorldwideId() {
		return worldwideId;
	}

	public void setWorldwideId(String worldwideId) {
		this.worldwideId = worldwideId;
	}

	@Column(columnDefinition = "text")
	public String getNationalLevelComment() {
		return nationalLevelComment;
	}

	public void setNationalLevelComment(String nationalLevelComment) {
		this.nationalLevelComment = nationalLevelComment;
	}
}
