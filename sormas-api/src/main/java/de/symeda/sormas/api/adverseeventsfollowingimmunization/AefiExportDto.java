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

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class AefiExportDto extends PseudonymizableIndexDto implements Serializable {

	private static final long serialVersionUID = 4568112808032880384L;

	public static final String I18N_PREFIX = "AefiExport";

	public static final String RECEIVED_AT_NATIONAL_LEVEL_DATE = "receivedAtNationalLevelDate";
	public static final String VACCINATION_FACILITY_NAME = "vaccinationFacilityName";
	public static final String VACCINATION_FACILITY_REGION = "vaccinationFacilityRegion";
	public static final String VACCINATION_FACILITY_DISTRICT = "vaccinationFacilityDistrict";
	public static final String VACCINATION_FACILITY_COMMUNITY = "vaccinationFacilityCommunity";
	public static final String REPORTING_OFFICER_ADDRESS_COUNTRY_NAME = "reportingOfficerAddressCountryName";
	public static final String PATIENT_ADDRESS_REGION = "patientAddressRegion";
	public static final String PATIENT_ADDRESS_DISTRICT = "patientAddressDistrict";
	public static final String PATIENT_ADDRESS_COMMUNITY = "patientAddressCommunity";
	public static final String PATIENT_ADDRESS_DETAILS = "patientAddressDetails";
	public static final String REPORTING_ID_NUMBER = "reportingIdNumber";
	public static final String WORLDWIDE_ID = "worldWideId";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String BIRTH_DATE = "birthDate";
	public static final String ONSET_AGE_YEARS = "onsetAgeYears";
	public static final String ONSET_AGE_MONTHS = "onsetAgeMonths";
	public static final String ONSET_AGE_DAYS = "onsetAgeDays";
	public static final String ONSET_AGE_GROUP = "onsetAgeGroup";
	public static final String SEX = "sex";
	public static final String AEFI_DESCRIPTION = "aefiDescription";
	public static final String PRIMARY_SUSPECT_VACCINE_NAME = "primarySuspectVaccineName";
	public static final String PRIMARY_SUSPECT_VACCINE_OTHER_NAME = "primarySuspectVaccineOtherName";
	public static final String PRIMARY_SUSPECT_VACCINE_BRAND = "primarySuspectVaccineBrand";
	public static final String PRIMARY_SUSPECT_VACCINE_MANUFACTURER = "primarySuspectVaccineManufacturer";
	public static final String PRIMARY_SUSPECT_VACCINE_BATCH_NUMBER = "primarySuspectVaccineBatchNumber";
	public static final String PRIMARY_SUSPECT_VACCINE_DOSE = "primarySuspectVaccineDose";
	public static final String PRIMARY_SUSPECT_VACCINE_DILUENT_BATCH_NUMBER = "primarySuspectVaccineDiluentBatchNumber";
	public static final String PRIMARY_SUSPECT_VACCINE_VACCINATION_DATE = "primarySuspectVaccineVaccinationDate";
	public static final String START_DATE_TIME = "startDateTime";
	public static final String SEVERE_LOCAL_REACTION = "severeLocalReaction";
	public static final String SEVERE_LOCAL_REACTION_MORE_THAN_THREE_DAYS = "severeLocalReactionMoreThanThreeDays";
	public static final String SEVERE_LOCAL_REACTION_BEYOND_NEAREST_JOINT = "severeLocalReactionBeyondNearestJoint";
	public static final String SEIZURES = "seizures";
	public static final String SEIZURE_TYPE = "seizureType";
	public static final String ABSCESS = "abscess";
	public static final String SEPSIS = "sepsis";
	public static final String ENCEPHALOPATHY = "encephalopathy";
	public static final String TOXIC_SHOCK_SYNDROME = "toxicShockSyndrome";
	public static final String THROMBOCYTOPENIA = "thrombocytopenia";
	public static final String ANAPHYLAXIS = "anaphylaxis";
	public static final String FEVERISH_FEELING = "feverishFeeling";
	public static final String OTHER_ADVERSE_EVENT_DETAILS = "otherAdverseEventDetails";
	public static final String OUTCOME = "outcome";
	public static final String SERIOUS = "serious";
	public static final String REPORTING_OFFICER_NAME = "reportingOfficerName";
	public static final String REPORTING_OFFICER_FACILITY_NAME = "reportingOfficerFacilityName";
	public static final String REPORTING_OFFICER_FACILITY_REGION = "reportingOfficerFacilityRegion";
	public static final String REPORTING_OFFICER_FACILITY_DISTRICT = "reportingOfficerFacilityDistrict";
	public static final String REPORTING_OFFICER_FACILITY_COMMUNITY = "reportingOfficerFacilityCommunity";
	public static final String REPORTING_OFFICER_DESIGNATION = "reportingOfficerDesignation";
	public static final String REPORTING_OFFICER_DEPARTMENT = "reportingOfficerDepartment";
	public static final String REPORTING_OFFICER_EMAIL = "reportingOfficerEmail";
	public static final String REPORTING_OFFICER_PHONE_NUMBER = "reportingOfficerPhoneNumber";
	public static final String REPORT_DATE = "reportDate";
	public static final String NATIONAL_LEVEL_COMMENT = "nationalLevelComment";

	private Date receivedAtNationalLevelDate;
	private String vaccinationFacilityName;
	private String vaccinationFacilityRegion;
	private String vaccinationFacilityDistrict;
	private String vaccinationFacilityCommunity;
	private String reportingOfficerAddressCountryName;
	private String patientAddressRegion;
	private String patientAddressDistrict;
	private String patientAddressCommunity;
	private String patientAddressDetails;
	private String reportingIdNumber;
	private String worldWideId;
	private String firstName;
	private String lastName;
	private BirthDateDto birthDate;
	private Integer onsetAgeYears;
	private Integer onsetAgeMonths;
	private Integer onsetAgeDays;
	private AefiAgeGroup onsetAgeGroup;
	private Sex sex;
	private String aefiDescription;
	private Vaccine primarySuspectVaccineName;
	private String primarySuspectVaccineOtherName;
	private String primarySuspectVaccineBrandName;
	private VaccineManufacturer primarySuspectVaccineManufacturer;
	private String primarySuspectVaccineBatchNumber;
	private String primarySuspectVaccineDose;
	private String primarySuspectVaccineDiluentBatchNumber;
	private Date primarySuspectVaccineVaccinationDate;
	private Date startDateTime;
	private AdverseEventState severeLocalReaction;
	private boolean severeLocalReactionMoreThanThreeDays;
	private boolean severeLocalReactionBeyondNearestJoint;
	private AdverseEventState seizures;
	private SeizureType seizureType;
	private AdverseEventState abscess;
	private AdverseEventState sepsis;
	private AdverseEventState encephalopathy;
	private AdverseEventState toxicShockSyndrome;
	private AdverseEventState thrombocytopenia;
	private AdverseEventState anaphylaxis;
	private AdverseEventState feverishFeeling;
	private String otherAdverseEventDetails;
	private AefiOutcome outcome;
	private YesNoUnknown serious;
	private String reportingOfficerName;
	private String reportingOfficerFacilityName;
	private String reportingOfficerFacilityRegion;
	private String reportingOfficerFacilityDistrict;
	private String reportingOfficerFacilityCommunity;
	private String reportingOfficerDesignation;
	private String reportingOfficerDepartment;
	private String reportingOfficerEmail;
	private String reportingOfficerPhoneNumber;
	private Date reportDate;
	private String nationalLevelComment;
	private Boolean isInJurisdiction;

	public AefiExportDto(
		String uuid,
		Date receivedAtNationalLevelDate,
		String vaccinationFacilityName,
		String vaccinationFacilityRegion,
		String vaccinationFacilityDistrict,
		String vaccinationFacilityCommunity,
		String reportingOfficerAddressCountryName,
		String patientAddressRegion,
		String patientAddressDistrict,
		String patientAddressCommunity,
		String street,
		String houseNumber,
		String postalCode,
		String city,
		String reportingIdNumber,
		String worldWideId,
		String firstName,
		String lastName,
		Integer birthdateDD,
		Integer birthdateMM,
		Integer birthdateYYYY,
		Integer onsetAgeYears,
		Integer onsetAgeMonths,
		Integer onsetAgeDays,
		AefiAgeGroup onsetAgeGroup,
		Sex sex,
		String aefiDescription,
		Vaccine primarySuspectVaccineName,
		String primarySuspectVaccineOtherName,
		VaccineManufacturer primarySuspectVaccineManufacturer,
		String primarySuspectVaccineBatchNumber,
		String primarySuspectVaccineDose,
		Date primarySuspectVaccineVaccinationDate,
		Date startDateTime,
		AdverseEventState severeLocalReaction,
		boolean severeLocalReactionMoreThanThreeDays,
		boolean severeLocalReactionBeyondNearestJoint,
		AdverseEventState seizures,
		SeizureType seizureType,
		AdverseEventState abscess,
		AdverseEventState sepsis,
		AdverseEventState encephalopathy,
		AdverseEventState toxicShockSyndrome,
		AdverseEventState thrombocytopenia,
		AdverseEventState anaphylaxis,
		AdverseEventState feverishFeeling,
		String otherAdverseEventDetails,
		AefiOutcome outcome,
		YesNoUnknown serious,
		String reportingOfficerFirstName,
		String reportingOfficerLastName,
		String reportingOfficerFacilityName,
		String reportingOfficerFacilityRegion,
		String reportingOfficerFacilityDistrict,
		String reportingOfficerFacilityCommunity,
		String reportingOfficerEmail,
		String reportingOfficerPhoneNumber,
		Date reportDate,
		String nationalLevelComment,
		boolean isInJurisdiction) {
		super(uuid);
		this.receivedAtNationalLevelDate = receivedAtNationalLevelDate;
		this.vaccinationFacilityName = vaccinationFacilityName;
		this.vaccinationFacilityRegion = vaccinationFacilityRegion;
		this.vaccinationFacilityDistrict = vaccinationFacilityDistrict;
		this.vaccinationFacilityCommunity = vaccinationFacilityCommunity;
		this.reportingOfficerAddressCountryName = reportingOfficerAddressCountryName;
		this.patientAddressRegion = patientAddressRegion;
		this.patientAddressDistrict = patientAddressDistrict;
		this.patientAddressCommunity = patientAddressCommunity;

		StringBuilder patientAddressBuilder = new StringBuilder();
		if (!StringUtils.isBlank(houseNumber)) {
			patientAddressBuilder.append(houseNumber);
		}
		if (!StringUtils.isBlank(street)) {
			patientAddressBuilder.append(", ").append(street);
		}
		if (!StringUtils.isBlank(postalCode)) {
			patientAddressBuilder.append(", ").append(postalCode);
		}
		if (!StringUtils.isBlank(city)) {
			patientAddressBuilder.append(", ").append(city);
		}

		this.patientAddressDetails = patientAddressBuilder.toString();

		this.reportingIdNumber = reportingIdNumber;
		this.worldWideId = worldWideId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = new BirthDateDto(birthdateDD, birthdateMM, birthdateYYYY);
		this.onsetAgeYears = onsetAgeYears;
		this.onsetAgeMonths = onsetAgeMonths;
		this.onsetAgeDays = onsetAgeDays;
		this.onsetAgeGroup = onsetAgeGroup;
		this.sex = sex;
		this.aefiDescription = aefiDescription;
		this.primarySuspectVaccineName = primarySuspectVaccineName;
		this.primarySuspectVaccineOtherName = primarySuspectVaccineOtherName;
		this.primarySuspectVaccineManufacturer = primarySuspectVaccineManufacturer;
		this.primarySuspectVaccineBatchNumber = primarySuspectVaccineBatchNumber;
		this.primarySuspectVaccineDose = primarySuspectVaccineDose;
		this.primarySuspectVaccineVaccinationDate = primarySuspectVaccineVaccinationDate;
		this.startDateTime = startDateTime;
		this.severeLocalReaction = severeLocalReaction;
		this.severeLocalReactionMoreThanThreeDays = severeLocalReactionMoreThanThreeDays;
		this.severeLocalReactionBeyondNearestJoint = severeLocalReactionBeyondNearestJoint;
		this.seizures = seizures;
		this.seizureType = seizureType;
		this.abscess = abscess;
		this.sepsis = sepsis;
		this.encephalopathy = encephalopathy;
		this.toxicShockSyndrome = toxicShockSyndrome;
		this.thrombocytopenia = thrombocytopenia;
		this.anaphylaxis = anaphylaxis;
		this.feverishFeeling = feverishFeeling;
		this.otherAdverseEventDetails = otherAdverseEventDetails;
		this.outcome = outcome;
		this.serious = serious;
		this.reportingOfficerName = reportingOfficerFirstName + " " + reportingOfficerLastName;
		this.reportingOfficerFacilityName = reportingOfficerFacilityName;
		this.reportingOfficerFacilityRegion = reportingOfficerFacilityRegion;
		this.reportingOfficerFacilityDistrict = reportingOfficerFacilityDistrict;
		this.reportingOfficerFacilityCommunity = reportingOfficerFacilityCommunity;
		this.reportingOfficerEmail = reportingOfficerEmail;
		this.reportingOfficerPhoneNumber = reportingOfficerPhoneNumber;
		this.reportDate = reportDate;
		this.nationalLevelComment = nationalLevelComment;
		this.isInJurisdiction = isInJurisdiction;
	}

	@Order(0)
	public Date getReceivedAtNationalLevelDate() {
		return receivedAtNationalLevelDate;
	}

	@Order(1)
	public String getVaccinationFacilityName() {
		return vaccinationFacilityName;
	}

	@Order(2)
	public String getVaccinationFacilityRegion() {
		return vaccinationFacilityRegion;
	}

	@Order(3)
	public String getVaccinationFacilityDistrict() {
		return vaccinationFacilityDistrict;
	}

	@Order(4)
	public String getVaccinationFacilityCommunity() {
		return vaccinationFacilityCommunity;
	}

	@Order(5)
	public String getReportingOfficerAddressCountryName() {
		return reportingOfficerAddressCountryName;
	}

	@Order(6)
	public String getPatientAddressRegion() {
		return patientAddressRegion;
	}

	@Order(7)
	public String getPatientAddressDistrict() {
		return patientAddressDistrict;
	}

	@Order(8)
	public String getPatientAddressCommunity() {
		return patientAddressCommunity;
	}

	@Order(9)
	public String getPatientAddressDetails() {
		return patientAddressDetails;
	}

	@Order(10)
	public String getReportingIdNumber() {
		return reportingIdNumber;
	}

	@Order(11)
	public String getWorldWideId() {
		return worldWideId;
	}

	@Order(12)
	public String getFirstName() {
		return firstName;
	}

	@Order(13)
	public String getLastName() {
		return lastName;
	}

	@Order(14)
	public BirthDateDto getBirthDate() {
		return birthDate;
	}

	@Order(15)
	public Integer getOnsetAgeYears() {
		return onsetAgeYears;
	}

	@Order(16)
	public Integer getOnsetAgeMonths() {
		return onsetAgeMonths;
	}

	@Order(17)
	public Integer getOnsetAgeDays() {
		return onsetAgeDays;
	}

	@Order(18)
	public AefiAgeGroup getOnsetAgeGroup() {
		return onsetAgeGroup;
	}

	@Order(19)
	public Sex getSex() {
		return sex;
	}

	@Order(20)
	public String getAefiDescription() {
		return aefiDescription;
	}

	@Order(21)
	public Vaccine getPrimarySuspectVaccineName() {
		return primarySuspectVaccineName;
	}

	@Order(22)
	public String getPrimarySuspectVaccineOtherName() {
		return primarySuspectVaccineOtherName;
	}

	@Order(23)
	public String getPrimarySuspectVaccineBrandName() {
		return primarySuspectVaccineBrandName;
	}

	@Order(24)
	public VaccineManufacturer getPrimarySuspectVaccineManufacturer() {
		return primarySuspectVaccineManufacturer;
	}

	@Order(25)
	public String getPrimarySuspectVaccineBatchNumber() {
		return primarySuspectVaccineBatchNumber;
	}

	@Order(26)
	public String getPrimarySuspectVaccineDose() {
		return primarySuspectVaccineDose;
	}

	@Order(27)
	public String getPrimarySuspectVaccineDiluentBatchNumber() {
		return primarySuspectVaccineDiluentBatchNumber;
	}

	@Order(28)
	public Date getPrimarySuspectVaccineVaccinationDate() {
		return primarySuspectVaccineVaccinationDate;
	}

	@Order(29)
	public Date getStartDateTime() {
		return startDateTime;
	}

	@Order(30)
	public AdverseEventState getSevereLocalReaction() {
		return severeLocalReaction;
	}

	@Order(31)
	public boolean isSevereLocalReactionMoreThanThreeDays() {
		return severeLocalReactionMoreThanThreeDays;
	}

	@Order(32)
	public boolean isSevereLocalReactionBeyondNearestJoint() {
		return severeLocalReactionBeyondNearestJoint;
	}

	@Order(33)
	public AdverseEventState getSeizures() {
		return seizures;
	}

	@Order(34)
	public SeizureType getSeizureType() {
		return seizureType;
	}

	@Order(35)
	public AdverseEventState getAbscess() {
		return abscess;
	}

	@Order(36)
	public AdverseEventState getSepsis() {
		return sepsis;
	}

	@Order(37)
	public AdverseEventState getEncephalopathy() {
		return encephalopathy;
	}

	@Order(38)
	public AdverseEventState getToxicShockSyndrome() {
		return toxicShockSyndrome;
	}

	@Order(39)
	public AdverseEventState getThrombocytopenia() {
		return thrombocytopenia;
	}

	@Order(40)
	public AdverseEventState getAnaphylaxis() {
		return anaphylaxis;
	}

	@Order(41)
	public AdverseEventState getFeverishFeeling() {
		return feverishFeeling;
	}

	@Order(42)
	public String getOtherAdverseEventDetails() {
		return otherAdverseEventDetails;
	}

	@Order(43)
	public AefiOutcome getOutcome() {
		return outcome;
	}

	@Order(44)
	public YesNoUnknown getSerious() {
		return serious;
	}

	@Order(45)
	public String getReportingOfficerName() {
		return reportingOfficerName;
	}

	@Order(46)
	public String getReportingOfficerFacilityName() {
		return reportingOfficerFacilityName;
	}

	@Order(47)
	public String getReportingOfficerFacilityRegion() {
		return reportingOfficerFacilityRegion;
	}

	@Order(48)
	public String getReportingOfficerFacilityDistrict() {
		return reportingOfficerFacilityDistrict;
	}

	@Order(49)
	public String getReportingOfficerFacilityCommunity() {
		return reportingOfficerFacilityCommunity;
	}

	@Order(50)
	public String getReportingOfficerDesignation() {
		return reportingOfficerDesignation;
	}

	@Order(51)
	public String getReportingOfficerDepartment() {
		return reportingOfficerDepartment;
	}

	@Order(52)
	public String getReportingOfficerEmail() {
		return reportingOfficerEmail;
	}

	@Order(53)
	public String getReportingOfficerPhoneNumber() {
		return reportingOfficerPhoneNumber;
	}

	@Order(54)
	public Date getReportDate() {
		return reportDate;
	}

	@Order(55)
	public String getNationalLevelComment() {
		return nationalLevelComment;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
	}
}
