/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.externalmessage;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_SMALL;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_TEXT;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.RadiographyCompatibility;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.clinicalcourse.ComplianceWithTreatment;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.disease.DiseaseVariantConverter;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReport;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;

@Entity(name = ExternalMessage.TABLE_NAME)

@TypeDefs({
	@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) })
public class ExternalMessage extends AbstractDomainObject {

	public static final String TABLE_NAME = "externalmessage";

	public static final String SAMPLE_REPORTS = "sampleReports";

	public static final String TYPE = "type";
	public static final String DISEASE = "disease";
	public static final String DISEASE_VARIANT_VALUE = "diseaseVariantValue";
	public static final String DISEASE_VARIANT_DETAILS = "diseaseVariantDetails";
	public static final String MESSAGE_DATE_TIME = "messageDateTime";
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String CASE_REPORT_DATE = "caseReportDate";
	public static final String CASE_SYMPTOMS = "caseSymptoms";
	public static final String REPORTER_NAME = "reporterName";
	public static final String REPORTER_EXTERNAL_IDS = "reporterExternalIds";
	public static final String REPORTER_POSTAL_CODE = "reporterPostalCode";
	public static final String REPORTER_CITY = "reporterCity";
	public static final String PERSON_EXTERNAL_ID = "personExternalId";
	public static final String PERSON_NATIONAL_HEALTH_ID = "personNationalHealthId";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String PERSON_SEX = "personSex";
	public static final String PERSON_PRESENT_CONDITION = "personPresentCondition";
	public static final String PERSON_BIRTH_DATE_DD = "personBirthDateDD";
	public static final String PERSON_BIRTH_DATE_MM = "personBirthDateMM";
	public static final String PERSON_BIRTH_DATE_YYYY = "personBirthDateYYYY";
	public static final String PERSON_POSTAL_CODE = "personPostalCode";
	public static final String PERSON_CITY = "personCity";
	public static final String PERSON_STREET = "personStreet";
	public static final String PERSON_HOUSE_NUMBER = "personHouseNumber";
	public static final String PERSON_COUNTRY = "personCountry";
	public static final String PERSON_FACILITY = "personFacility";
	public static final String PERSON_PHONE = "personPhone";
	public static final String PERSON_PHONE_NUMBER_TYPE = "personPhoneNumberType";
	public static final String PERSON_EMAIL = "personEmail";
	public static final String PERSON_GUARDIAN_FIRST_NAME = "personGurdianFirstName";
	public static final String PERSON_GUARDIAN_LAST_NAME = "personGuardianLastName";
	public static final String PERSON_GUARDIAN_RELATIONSHIP = "personGuardianRelationship";
	public static final String PERSON_GUARDIAN_PHONE = "personGuardianPhone";
	public static final String PERSON_GUARDIAN_EMAIL = "personGuardianEmail";
	public static final String EXTERNAL_MESSAGE_DETAILS = "externalMessageDetails";
	public static final String STATUS = "status";
	public static final String REPORT_ID = "reportId";
	public static final String REPORT_MESSAGE_ID = "reportMessageId";
	public static final String ASSIGNEE = "assignee";
	public static final String SURVEILLANCE_REPORT = "surveillanceReport";
	public static final String TSV = "tsv";

	public static final String NOTIFIER_FIRST_NAME = "notifierFirstName";
	public static final String NOTIFIER_LAST_NAME = "notifierLastName";
	public static final String NOTIFIER_REGISTRATION_NUMBER = "notifierRegistrationNumber";
	public static final String NOTIFIER_ADDRESS = "notifierAddress";
	public static final String NOTIFIER_EMAIL = "notifierEmail";
	public static final String NOTIFIER_PHONE = "notifierPhone";

	public static final String TREATMENT_STARTED = "treatmentStarted";
	public static final String TREATMENT_NOT_APPLICABLE = "treatmentNotApplicable";
	public static final String TREATMENT_STARTED_DATE = "treatmentStartedDate";
	public static final String DIAGNOSTIC_DATE = "diagnosticDate";

	public static final String ACTIVITIES_AS_CASE = "activitiesAsCase";
	public static final String EXPOSURES = "exposures";

	public static final String RADIOGRAPHY_COMPATIBILITY = "radiographyCompatibility";
	public static final String OTHER_DIAGNOSTIC_CRITERIA = "otherDiagnosticCriteria";

	private ExternalMessageType type;
	private Disease disease;
	private String diseaseVariantValue;
	private DiseaseVariant diseaseVariant;
	private String diseaseVariantDetails;
	private Date messageDateTime;
	private CaseClassification caseClassification;
	private Date caseReportDate;
	private Symptoms caseSymptoms;
	private String reporterName;
	private List<String> reporterExternalIds;
	private String reporterPostalCode;
	private String reporterCity;

	private String personExternalId;
	private String personNationalHealthId;
	private String personFirstName;
	private String personLastName;
	private Sex personSex;
	private PresentCondition personPresentCondition;
	private Integer personBirthDateDD;
	private Integer personBirthDateMM;
	private Integer personBirthDateYYYY;
	private String personPostalCode;
	private String personCity;
	private String personStreet;
	private Country personCountry;
	private Facility personFacility;
	private String personHouseNumber;
	private String personPhone;
	private PhoneNumberType personPhoneNumberType;
	private String personEmail;
	private String personGuardianFirstName;
	private String personGuardianLastName;
	private String personGuardianRelationship;
	private String personGuardianPhone;
	private String personGuardianEmail;
	private YesNoUnknown treatmentStarted;
	private Boolean treatmentNotApplicable;
	private Date treatmentStartedDate;
	private Date diagnosticDate;
	private Date deceasedDate;

	private String externalMessageDetails;
	private String caseComments;
	//External messages related to each other should have the same reportId
	private String reportId;
	private String reportMessageId;

	private ExternalMessageStatus status = ExternalMessageStatus.UNPROCESSED;
	private User assignee;

	private List<SampleReport> sampleReports;
	private SurveillanceReport surveillanceReport;
	private String tsv;
	private String personAdditionalDetails;

	private VaccinationStatus vaccinationStatus;

	private YesNoUnknown admittedToHealthFacility;
	private String hospitalizationFacilityName;
	private String hospitalizationFacilityExternalId;
	private String hospitalizationFacilityDepartment;
	private Date hospitalizationAdmissionDate;
	private Date hospitalizationDischargeDate;

	private String notifierFirstName;
	private String notifierLastName;

	private String notifierRegistrationNumber;
	private String notifierAddress;
	private String notifierEmail;
	private String notifierPhone;

	private String activitiesAsCase;
	private String exposures;

	private RadiographyCompatibility radiographyCompatibility;
	private String otherDiagnosticCriteria;

	private YesNoUnknown tuberculosis;
	private YesNoUnknown hiv;
	private YesNoUnknown hivArt;

	private Integer tuberculosisInfectionYear;
	private YesNoUnknown previousTuberculosisTreatment;
	private ComplianceWithTreatment complianceWithTreatment;
	private Boolean tuberculosisDirectlyObservedTreatment;
	private Boolean tuberculosisMdrXdrTuberculosis;
	private Boolean tuberculosisBeijingLineage;

	@Enumerated(EnumType.STRING)
	public ExternalMessageType getType() {
		return type;
	}

	public void setType(ExternalMessageType type) {
		this.type = type;
	}

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column(name = "diseasevariant")
	public String getDiseaseVariantValue() {
		return diseaseVariantValue;
	}

	public void setDiseaseVariantValue(String diseaseVariantValue) {
		this.diseaseVariantValue = diseaseVariantValue;
		this.diseaseVariant = new DiseaseVariantConverter().convertToEntityAttribute(disease, diseaseVariantValue);
	}

	@Transient
	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
		this.diseaseVariantValue = new DiseaseVariantConverter().convertToDatabaseColumn(diseaseVariant);
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	@Enumerated(EnumType.STRING)
	@Column
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseSymptoms(Symptoms caseSymptoms) {
		this.caseSymptoms = caseSymptoms;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "casesymptoms_id")
	public Symptoms getCaseSymptoms() {
		return caseSymptoms;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getDiseaseVariantDetails() {
		return diseaseVariantDetails;
	}

	public void setDiseaseVariantDetails(String diseaseVariantDetails) {
		this.diseaseVariantDetails = diseaseVariantDetails;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(Date messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCaseReportDate() {
		return caseReportDate;
	}

	public void setCaseReportDate(Date caseReportDate) {
		this.caseReportDate = caseReportDate;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReporterName() {
		return reporterName;
	}

	public void setReporterName(String labName) {
		this.reporterName = labName;
	}

	@Type(type = "list-array")
	@Column(name = "reporterexternalids", columnDefinition = "VARCHAR(255) ARRAY")
	public List<String> getReporterExternalIds() {
		return reporterExternalIds;
	}

	public void setReporterExternalIds(List<String> reporterExternalIds) {
		this.reporterExternalIds = reporterExternalIds;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReporterPostalCode() {
		return reporterPostalCode;
	}

	public void setReporterPostalCode(String testLabPostalCode) {
		this.reporterPostalCode = testLabPostalCode;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReporterCity() {
		return reporterCity;
	}

	public void setReporterCity(String labCity) {
		this.reporterCity = labCity;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonExternalId() {
		return personExternalId;
	}

	public void setPersonExternalId(String personExternalId) {
		this.personExternalId = personExternalId;
	}

	public String getPersonNationalHealthId() {
		return personNationalHealthId;
	}

	public void setPersonNationalHealthId(String personNationalHealthId) {
		this.personNationalHealthId = personNationalHealthId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonFirstName() {
		return personFirstName;
	}

	public void setPersonFirstName(String personFirstName) {
		this.personFirstName = personFirstName;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonLastName() {
		return personLastName;
	}

	public void setPersonLastName(String personLastName) {
		this.personLastName = personLastName;
	}

	@Enumerated(EnumType.STRING)
	public Sex getPersonSex() {
		return personSex;
	}

	public void setPersonSex(Sex personSex) {
		this.personSex = personSex;
	}

	public PresentCondition getPersonPresentCondition() {
		return personPresentCondition;
	}

	public void setPersonPresentCondition(PresentCondition personPresentCondition) {
		this.personPresentCondition = personPresentCondition;
	}

	@Column(name = "personbirthdatedd")
	public Integer getPersonBirthDateDD() {
		return personBirthDateDD;
	}

	public void setPersonBirthDateDD(Integer personBirthDateDD) {
		this.personBirthDateDD = personBirthDateDD;
	}

	@Column(name = "personbirthdatemm")
	public Integer getPersonBirthDateMM() {
		return personBirthDateMM;
	}

	public void setPersonBirthDateMM(Integer personBirthDateMM) {
		this.personBirthDateMM = personBirthDateMM;
	}

	@Column(name = "personbirthdateyyyy")
	public Integer getPersonBirthDateYYYY() {
		return personBirthDateYYYY;
	}

	public void setPersonBirthDateYYYY(Integer personBirthDateYYYY) {
		this.personBirthDateYYYY = personBirthDateYYYY;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonPostalCode() {
		return personPostalCode;
	}

	public void setPersonPostalCode(String personPostalCode) {
		this.personPostalCode = personPostalCode;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonCity() {
		return personCity;
	}

	public void setPersonCity(String personCity) {
		this.personCity = personCity;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonStreet() {
		return personStreet;
	}

	public void setPersonStreet(String personStreet) {
		this.personStreet = personStreet;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonHouseNumber() {
		return personHouseNumber;
	}

	public void setPersonHouseNumber(String personHouseNumber) {
		this.personHouseNumber = personHouseNumber;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Country getPersonCountry() {
		return personCountry;
	}

	public void setPersonCountry(Country personCountry) {
		this.personCountry = personCountry;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Facility getPersonFacility() {
		return personFacility;
	}

	public void setPersonFacility(Facility personFacility) {
		this.personFacility = personFacility;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonPhone() {
		return personPhone;
	}

	public void setPersonPhone(String personPhone) {
		this.personPhone = personPhone;
	}

	@Enumerated(EnumType.STRING)
	public PhoneNumberType getPersonPhoneNumberType() {
		return personPhoneNumberType;
	}

	public void setPersonPhoneNumberType(PhoneNumberType personPhoneNumberType) {
		this.personPhoneNumberType = personPhoneNumberType;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonEmail() {
		return personEmail;
	}

	public void setPersonEmail(String personEmail) {
		this.personEmail = personEmail;
	}

	@Column(length = CHARACTER_LIMIT_SMALL)
	public String getPersonGuardianFirstName() {
		return personGuardianFirstName;
	}

	public void setPersonGuardianFirstName(String personGuardianFirstName) {
		this.personGuardianFirstName = personGuardianFirstName;
	}

	@Column(length = CHARACTER_LIMIT_SMALL)
	public String getPersonGuardianLastName() {
		return personGuardianLastName;
	}

	public void setPersonGuardianLastName(String personGuardianLastName) {
		this.personGuardianLastName = personGuardianLastName;
	}

	@Column(length = CHARACTER_LIMIT_SMALL)
	public String getPersonGuardianRelationship() {
		return personGuardianRelationship;
	}

	public void setPersonGuardianRelationship(String personGuardianRelationship) {
		this.personGuardianRelationship = personGuardianRelationship;
	}

	@Column(length = CHARACTER_LIMIT_SMALL)
	public String getPersonGuardianPhone() {
		return personGuardianPhone;
	}

	public void setPersonGuardianPhone(String personGuardianPhone) {
		this.personGuardianPhone = personGuardianPhone;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonGuardianEmail() {
		return personGuardianEmail;
	}

	public void setPersonGuardianEmail(String personGuardianEmail) {
		this.personGuardianEmail = personGuardianEmail;
	}

	@Column
	public String getExternalMessageDetails() {
		return externalMessageDetails;
	}

	public void setExternalMessageDetails(String labMessageDetails) {
		this.externalMessageDetails = labMessageDetails;
	}

	@Column(length = CHARACTER_LIMIT_TEXT)
	public String getCaseComments() {
		return caseComments;
	}

	public void setCaseComments(String caseComments) {
		this.caseComments = caseComments;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ExternalMessageStatus getStatus() {
		return status;
	}

	public void setStatus(ExternalMessageStatus status) {
		this.status = status;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReportMessageId() {
		return reportMessageId;
	}

	public void setReportMessageId(String reportMessageId) {
		this.reportMessageId = reportMessageId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	@OneToOne(fetch = FetchType.LAZY)
	public SurveillanceReport getSurveillanceReport() {
		return surveillanceReport;
	}

	public void setSurveillanceReport(SurveillanceReport surveillanceReport) {
		this.surveillanceReport = surveillanceReport;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = SampleReport.LAB_MESSAGE, fetch = FetchType.LAZY)
	public List<SampleReport> getSampleReports() {
		return sampleReports;
	}

	public void setSampleReports(List<SampleReport> sampleReports) {
		this.sampleReports = sampleReports;
	}

	@Column(insertable = false, updatable = false)
	public String getTsv() {
		return tsv;
	}

	public void setTsv(String tsv) {
		this.tsv = tsv;
	}

	@Column(length = CHARACTER_LIMIT_TEXT)
	public String getPersonAdditionalDetails() {
		return personAdditionalDetails;
	}

	public void setPersonAdditionalDetails(String personAdditionalDetails) {
		this.personAdditionalDetails = personAdditionalDetails;
	}

	@Enumerated(EnumType.STRING)
	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
		this.vaccinationStatus = vaccinationStatus;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAdmittedToHealthFacility() {
		return admittedToHealthFacility;
	}

	public void setAdmittedToHealthFacility(YesNoUnknown admittedToHealthFacility) {
		this.admittedToHealthFacility = admittedToHealthFacility;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getHospitalizationFacilityName() {
		return hospitalizationFacilityName;
	}

	public void setHospitalizationFacilityName(String hospitalizationFacilityName) {
		this.hospitalizationFacilityName = hospitalizationFacilityName;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getHospitalizationFacilityExternalId() {
		return hospitalizationFacilityExternalId;
	}

	public void setHospitalizationFacilityExternalId(String hospitalizationFacilityExternalId) {
		this.hospitalizationFacilityExternalId = hospitalizationFacilityExternalId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getHospitalizationFacilityDepartment() {
		return hospitalizationFacilityDepartment;
	}

	public void setHospitalizationFacilityDepartment(String hospitalizationFacilityDepartment) {
		this.hospitalizationFacilityDepartment = hospitalizationFacilityDepartment;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getHospitalizationAdmissionDate() {
		return hospitalizationAdmissionDate;
	}

	public void setHospitalizationAdmissionDate(Date hospitalizationAdmissionDate) {
		this.hospitalizationAdmissionDate = hospitalizationAdmissionDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getHospitalizationDischargeDate() {
		return hospitalizationDischargeDate;
	}

	public void setHospitalizationDischargeDate(Date hospitalizationDischargeDate) {
		this.hospitalizationDischargeDate = hospitalizationDischargeDate;
	}

	@Column(length = CHARACTER_LIMIT_SMALL)
	public String getNotifierFirstName() {
		return notifierFirstName;
	}

	public void setNotifierFirstName(String notifierFirstName) {
		this.notifierFirstName = notifierFirstName;
	}

	@Column(length = CHARACTER_LIMIT_SMALL)
	public String getNotifierLastName() {
		return notifierLastName;
	}

	public void setNotifierLastName(String notifierLastName) {
		this.notifierLastName = notifierLastName;
	}

	@Column(length = CHARACTER_LIMIT_SMALL)
	public String getNotifierRegistrationNumber() {
		return notifierRegistrationNumber;
	}

	public void setNotifierRegistrationNumber(String notifierRegistrationNumber) {
		this.notifierRegistrationNumber = notifierRegistrationNumber;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getNotifierAddress() {
		return notifierAddress;
	}

	public void setNotifierAddress(String notifierAddress) {
		this.notifierAddress = notifierAddress;
	}

	@Column(length = CHARACTER_LIMIT_SMALL)
	public String getNotifierEmail() {
		return notifierEmail;
	}

	public void setNotifierEmail(String notifierEmail) {
		this.notifierEmail = notifierEmail;
	}

	@Column(length = CHARACTER_LIMIT_SMALL)
	public String getNotifierPhone() {
		return notifierPhone;
	}

	public void setNotifierPhone(String notifierPhone) {
		this.notifierPhone = notifierPhone;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getTreatmentStarted() {
		return treatmentStarted;
	}

	public void setTreatmentStarted(YesNoUnknown treatmentStarted) {
		this.treatmentStarted = treatmentStarted;
	}

	@Column
	public Boolean getTreatmentNotApplicable() {
		return treatmentNotApplicable;
	}

	public void setTreatmentNotApplicable(Boolean treatmentNotApplicable) {
		this.treatmentNotApplicable = treatmentNotApplicable;
	}

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date getTreatmentStartedDate() {
		return treatmentStartedDate;
	}

	public void setTreatmentStartedDate(Date treatmentStartedDate) {
		this.treatmentStartedDate = treatmentStartedDate;
	}

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDiagnosticDate() {
		return diagnosticDate;
	}

	public void setDiagnosticDate(Date diagnosticDate) {
		this.diagnosticDate = diagnosticDate;
	}

	@Column(columnDefinition = "jsonb")
	@Type(type = "jsonb")
	public String getActivitiesAsCase() {
		return activitiesAsCase;
	}

	public void setActivitiesAsCase(String activitiesAsCase) {
		this.activitiesAsCase = activitiesAsCase;
	}

	@Column(columnDefinition = "jsonb")
	@Type(type = "jsonb")
	public String getExposures() {
		return exposures;
	}

	public void setExposures(String exposures) {
		this.exposures = exposures;
	}

	public Date getDeceasedDate() {
		return deceasedDate;
	}

	public void setDeceasedDate(Date deceasedDate) {
		this.deceasedDate = deceasedDate;
	}

	@Enumerated(EnumType.STRING)
	public RadiographyCompatibility getRadiographyCompatibility() {
		return radiographyCompatibility;
	}

	public void setRadiographyCompatibility(RadiographyCompatibility radiographyCompatibility) {
		this.radiographyCompatibility = radiographyCompatibility;
	}

	public String getOtherDiagnosticCriteria() {
		return otherDiagnosticCriteria;
	}

	public void setOtherDiagnosticCriteria(String otherDiagnosticCriteria) {
		this.otherDiagnosticCriteria = otherDiagnosticCriteria;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getTuberculosis() {
		return tuberculosis;
	}

	public void setTuberculosis(YesNoUnknown tuberculosis) {
		this.tuberculosis = tuberculosis;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHiv() {
		return hiv;
	}

	public void setHiv(YesNoUnknown hiv) {
		this.hiv = hiv;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHivArt() {
		return hivArt;
	}

	public void setHivArt(YesNoUnknown hivArt) {
		this.hivArt = hivArt;
	}

	public Integer getTuberculosisInfectionYear() {
		return tuberculosisInfectionYear;
	}

	public void setTuberculosisInfectionYear(Integer tuberculosisInfectionYear) {
		this.tuberculosisInfectionYear = tuberculosisInfectionYear;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPreviousTuberculosisTreatment() {
		return previousTuberculosisTreatment;
	}

	public void setPreviousTuberculosisTreatment(YesNoUnknown previousTuberculosisTreatment) {
		this.previousTuberculosisTreatment = previousTuberculosisTreatment;
	}

	@Enumerated(EnumType.STRING)
	public ComplianceWithTreatment getComplianceWithTreatment() {
		return complianceWithTreatment;
	}

	public void setComplianceWithTreatment(ComplianceWithTreatment complianceWithTreatment) {
		this.complianceWithTreatment = complianceWithTreatment;
	}

	public Boolean getTuberculosisDirectlyObservedTreatment() {
		return tuberculosisDirectlyObservedTreatment;
	}

	public void setTuberculosisDirectlyObservedTreatment(Boolean tuberculosisDirectlyObservedTreatment) {
		this.tuberculosisDirectlyObservedTreatment = tuberculosisDirectlyObservedTreatment;
	}

	public Boolean getTuberculosisMdrXdrTuberculosis() {
		return tuberculosisMdrXdrTuberculosis;
	}

	public void setTuberculosisMdrXdrTuberculosis(Boolean tuberculosisMdrXdrTuberculosis) {
		this.tuberculosisMdrXdrTuberculosis = tuberculosisMdrXdrTuberculosis;
	}

	public Boolean getTuberculosisBeijingLineage() {
		return tuberculosisBeijingLineage;
	}

	public void setTuberculosisBeijingLineage(Boolean tuberculosisBeijingLineage) {
		this.tuberculosisBeijingLineage = tuberculosisBeijingLineage;
	}
}
