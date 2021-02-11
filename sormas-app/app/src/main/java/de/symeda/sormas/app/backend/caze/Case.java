/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.caze;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseIdentificationSource;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.ContactTracingContactType;
import de.symeda.sormas.api.caze.CovidTestReason;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.EndOfIsolationReason;
import de.symeda.sormas.api.caze.HospitalWardType;
import de.symeda.sormas.api.caze.InfectionSetting;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.QuarantineReason;
import de.symeda.sormas.api.caze.RabiesType;
import de.symeda.sormas.api.caze.ReportingType;
import de.symeda.sormas.api.caze.Trimester;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.caze.maternalhistory.MaternalHistory;
import de.symeda.sormas.app.backend.caze.porthealthinfo.PortHealthInfo;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.disease.DiseaseVariant;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.sormastosormas.SormasToSormasOriginInfo;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.therapy.Therapy;
import de.symeda.sormas.app.backend.user.User;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

@Entity(name = Case.TABLE_NAME)
@DatabaseTable(tableName = Case.TABLE_NAME)
public class Case extends PseudonymizableAdo {

	private static final long serialVersionUID = -2697795184163562129L;

	public static final String TABLE_NAME = "cases";
	public static final String I18N_PREFIX = "CaseData";

	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String PERSON = "person_id";
	public static final String REPORT_DATE = "reportDate";
	public static final String SYMPTOMS = "symptoms";
	public static final String EPI_DATA = "epiData";
	public static final String CLINICAL_COURSE = "clinicalCourse";
	public static final String REPORTING_USER = "reportingUser_id";
	public static final String HEALTH_FACILITY = "healthFacility_id";
	public static final String OUTCOME = "outcome";
	public static final String EPID_NUMBER = "epidNumber";
	public static final String CASE_ORIGIN = "caseOrigin";
	public static final String REGION = "region";
	public static final String COMPLETENESS = "completeness";

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
	private Person person;

	@Column(length = COLUMN_LENGTH_BIG)
	private String description;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private DiseaseVariant diseaseVariant;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String diseaseDetails;

	@Enumerated(EnumType.STRING)
	private PlagueType plagueType;

	@Enumerated(EnumType.STRING)
	private DengueFeverType dengueFeverType;

	@Enumerated(EnumType.STRING)
	private RabiesType rabiesType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CaseClassification caseClassification;

	@Enumerated(EnumType.STRING)
	private CaseIdentificationSource caseIdentificationSource;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User classificationUser;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date classificationDate;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String classificationComment;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown clinicalConfirmation;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown epidemiologicalConfirmation;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown laboratoryDiagnosticConfirmation;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InvestigationStatus investigationStatus;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region region;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District district;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Community community;

	@Enumerated(EnumType.STRING)
	private FacilityType facilityType;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility healthFacility;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String healthFacilityDetails;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private PointOfEntry pointOfEntry;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String pointOfEntryDetails;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Symptoms symptoms;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date reportDate;
	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date investigatedDate;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date districtLevelDate;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1)
	private User surveillanceOfficer;
	@Column(length = COLUMN_LENGTH_DEFAULT, name = "clinicianDetails")
	private String clinicianName;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String clinicianPhone;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String clinicianEmail;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1)
	private User caseOfficer;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown pregnant;

	@Enumerated(EnumType.STRING)
	private Vaccination vaccination;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String vaccine;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String vaccinationDoses;

	@Enumerated(EnumType.STRING)
	private VaccinationInfoSource vaccinationInfoSource;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown smallpoxVaccinationScar;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown smallpoxVaccinationReceived;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date firstVaccinationDate;

	@DatabaseField(columnName = "vaccinationDate", dataType = DataType.DATE_LONG)
	private Date lastVaccinationDate;

	@Enumerated(EnumType.STRING)
	private Vaccine vaccineName;

	@Column(columnDefinition = "text")
	private String otherVaccineName;

	@Enumerated(EnumType.STRING)
	private VaccineManufacturer vaccineManufacturer;

	@Column(columnDefinition = "text")
	private String otherVaccineManufacturer;

	@Column(columnDefinition = "text")
	private String vaccineInn;

	@Column(columnDefinition = "text")
	private String vaccineBatchNumber;

	@Column(columnDefinition = "text")
	private String vaccineUniiCode;

	@Column(columnDefinition = "text")
	private String vaccineAtcCode;

	@Deprecated
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date smallpoxVaccinationDate;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String epidNumber;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Hospitalization hospitalization;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private EpiData epiData;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Therapy therapy;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private ClinicalCourse clinicalCourse;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private MaternalHistory maternalHistory;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private PortHealthInfo portHealthInfo;

	@Deprecated
	@Column
	private Long contactOfficer_id;

	@DatabaseField
	private Double reportLat;
	@DatabaseField
	private Double reportLon;
	@DatabaseField
	private Float reportLatLonAccuracy;

	@Enumerated(EnumType.STRING)
	private CaseOutcome outcome;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date outcomeDate;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown sequelae;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String sequelaeDetails;
	@Enumerated(EnumType.STRING)
	private HospitalWardType notifyingClinic;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String notifyingClinicDetails;

	@Enumerated(EnumType.STRING)
	private CaseOrigin caseOrigin;

	@Column(length = 32)
	@DatabaseField(columnName = "versionCreated")
	private String creationVersion;

	@DatabaseField
	private Float completeness;

	@Column(length = COLUMN_LENGTH_BIG)
	private String additionalDetails;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String externalID;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String externalToken;

	@Enumerated(EnumType.STRING)
	private QuarantineType quarantine;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String quarantineTypeDetails;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date quarantineFrom;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date quarantineTo;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String quarantineHelpNeeded;
	@DatabaseField
	private boolean quarantineOrderedVerbally;
	@DatabaseField
	private boolean quarantineOrderedOfficialDocument;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date quarantineOrderedVerballyDate;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date quarantineOrderedOfficialDocumentDate;
	@DatabaseField
	private boolean quarantineExtended;
	@DatabaseField
	private boolean quarantineReduced;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown quarantineHomePossible;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String quarantineHomePossibleComment;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown quarantineHomeSupplyEnsured;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String quarantineHomeSupplyEnsuredComment;
	@DatabaseField
	private boolean quarantineOfficialOrderSent;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date quarantineOfficialOrderSentDate;
	@Enumerated(EnumType.STRING)
	private ReportingType reportingType;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown postpartum;
	@Enumerated(EnumType.STRING)
	private Trimester trimester;
	@DatabaseField
	private Integer caseIdIsm;
	@Enumerated(EnumType.STRING)
	private CovidTestReason covidTestReason;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String covidTestReasonDetails;
	@Enumerated(EnumType.STRING)
	private ContactTracingContactType contactTracingFirstContactType;
	@DatabaseField
	private Date contactTracingFirstContactDate;
	@Enumerated(EnumType.STRING)
	private YesNoUnknown wasInQuarantineBeforeIsolation;
	@Enumerated(EnumType.STRING)
	private QuarantineReason quarantineReasonBeforeIsolation;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String quarantineReasonBeforeIsolationDetails;
	@Enumerated(EnumType.STRING)
	private EndOfIsolationReason endOfIsolationReason;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String endOfIsolationReasonDetails;
	@DatabaseField
	private boolean nosocomialOutbreak;
	@Enumerated(EnumType.STRING)
	private InfectionSetting infectionSetting;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown prohibitionToWork;
	@DatabaseField
	private Date prohibitionToWorkFrom;
	@DatabaseField
	private Date prohibitionToWorkUntil;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown reInfection;
	@DatabaseField
	private Date previousInfectionDate;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District reportingDistrict;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown bloodOrganOrTissueDonated;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private SormasToSormasOriginInfo sormasToSormasOriginInfo;
	@DatabaseField
	private boolean ownershipHandedOver;

	@DatabaseField
	private boolean notACaseReasonNegativeTest;
	@DatabaseField
	private boolean notACaseReasonPhysicianInformation;
	@DatabaseField
	private boolean notACaseReasonDifferentPathogen;
	@DatabaseField
	private boolean notACaseReasonOther;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String notACaseReasonDetails;

	public boolean isUnreferredPortHealthCase() {
		return caseOrigin == CaseOrigin.POINT_OF_ENTRY && healthFacility == null;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public PlagueType getPlagueType() {
		return plagueType;
	}

	public void setPlagueType(PlagueType plagueType) {
		this.plagueType = plagueType;
	}

	public RabiesType getRabiesType() {
		return rabiesType;
	}

	public void setRabiesType(RabiesType rabiesType) {
		this.rabiesType = rabiesType;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public CaseIdentificationSource getCaseIdentificationSource() {
		return caseIdentificationSource;
	}

	public void setCaseIdentificationSource(CaseIdentificationSource caseIdentificationSource) {
		this.caseIdentificationSource = caseIdentificationSource;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Date getInvestigatedDate() {
		return investigatedDate;
	}

	public void setInvestigatedDate(Date investigatedDate) {
		this.investigatedDate = investigatedDate;
	}

	public Date getDistrictLevelDate() {
		return districtLevelDate;
	}

	public void setDistrictLevelDate(Date districtLevelDate) {
		this.districtLevelDate = districtLevelDate;
	}

	public Facility getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	public String getHealthFacilityDetails() {
		return healthFacilityDetails;
	}

	public void setHealthFacilityDetails(String healthFacilityDetails) {
		this.healthFacilityDetails = healthFacilityDetails;
	}

	public Symptoms getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}

	public User getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public void setSurveillanceOfficer(User surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public String getClinicianName() {
		return clinicianName;
	}

	public void setClinicianName(String clinicianName) {
		this.clinicianName = clinicianName;
	}

	public String getClinicianPhone() {
		return clinicianPhone;
	}

	public void setClinicianPhone(String clinicianPhone) {
		this.clinicianPhone = clinicianPhone;
	}

	public String getClinicianEmail() {
		return clinicianEmail;
	}

	public void setClinicianEmail(String clinicianEmail) {
		this.clinicianEmail = clinicianEmail;
	}

	public User getCaseOfficer() {
		return caseOfficer;
	}

	public void setCaseOfficer(User caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public YesNoUnknown getPregnant() {
		return pregnant;
	}

	public void setPregnant(YesNoUnknown pregnant) {
		this.pregnant = pregnant;
	}

	public Vaccination getVaccination() {
		return vaccination;
	}

	public void setVaccination(Vaccination vaccination) {
		this.vaccination = vaccination;
	}

	public String getVaccine() {
		return vaccine;
	}

	public void setVaccine(String vaccine) {
		this.vaccine = vaccine;
	}

	public String getVaccinationDoses() {
		return vaccinationDoses;
	}

	public void setVaccinationDoses(String vaccinationDoses) {
		this.vaccinationDoses = vaccinationDoses;
	}

	public VaccinationInfoSource getVaccinationInfoSource() {
		return vaccinationInfoSource;
	}

	public void setVaccinationInfoSource(VaccinationInfoSource vaccinationInfoSource) {
		this.vaccinationInfoSource = vaccinationInfoSource;
	}

	public YesNoUnknown getSmallpoxVaccinationScar() {
		return smallpoxVaccinationScar;
	}

	public void setSmallpoxVaccinationScar(YesNoUnknown smallpoxVaccinationScar) {
		this.smallpoxVaccinationScar = smallpoxVaccinationScar;
	}

	public YesNoUnknown getSmallpoxVaccinationReceived() {
		return smallpoxVaccinationReceived;
	}

	public void setSmallpoxVaccinationReceived(YesNoUnknown smallpoxVaccinationReceived) {
		this.smallpoxVaccinationReceived = smallpoxVaccinationReceived;
	}

	public Date getFirstVaccinationDate() {
		return firstVaccinationDate;
	}

	public void setFirstVaccinationDate(Date firstVaccinationDate) {
		this.firstVaccinationDate = firstVaccinationDate;
	}

	public Date getLastVaccinationDate() {
		return lastVaccinationDate;
	}

	public void setLastVaccinationDate(Date lastVaccinationDate) {
		this.lastVaccinationDate = lastVaccinationDate;
	}

	public Vaccine getVaccineName() {
		return vaccineName;
	}

	public void setVaccineName(Vaccine vaccineName) {
		this.vaccineName = vaccineName;
	}

	public String getOtherVaccineName() {
		return otherVaccineName;
	}

	public void setOtherVaccineName(String otherVaccineName) {
		this.otherVaccineName = otherVaccineName;
	}

	public VaccineManufacturer getVaccineManufacturer() {
		return vaccineManufacturer;
	}

	public void setVaccineManufacturer(VaccineManufacturer vaccineManufacturer) {
		this.vaccineManufacturer = vaccineManufacturer;
	}

	public String getOtherVaccineManufacturer() {
		return otherVaccineManufacturer;
	}

	public void setOtherVaccineManufacturer(String otherVaccineManufacturer) {
		this.otherVaccineManufacturer = otherVaccineManufacturer;
	}

	public String getVaccineInn() {
		return vaccineInn;
	}

	public void setVaccineInn(String vaccineInn) {
		this.vaccineInn = vaccineInn;
	}

	public String getVaccineBatchNumber() {
		return vaccineBatchNumber;
	}

	public void setVaccineBatchNumber(String vaccineBatchNumber) {
		this.vaccineBatchNumber = vaccineBatchNumber;
	}

	public String getVaccineUniiCode() {
		return vaccineUniiCode;
	}

	public void setVaccineUniiCode(String vaccineUniiCode) {
		this.vaccineUniiCode = vaccineUniiCode;
	}

	public String getVaccineAtcCode() {
		return vaccineAtcCode;
	}

	public void setVaccineAtcCode(String vaccineAtcCode) {
		this.vaccineAtcCode = vaccineAtcCode;
	}

	public String getEpidNumber() {
		return epidNumber;
	}

	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}

	public Hospitalization getHospitalization() {
		return hospitalization;
	}

	public void setHospitalization(Hospitalization hospitalization) {
		this.hospitalization = hospitalization;
	}

	public EpiData getEpiData() {
		return epiData;
	}

	public void setEpiData(EpiData epiData) {
		this.epiData = epiData;
	}

	public Therapy getTherapy() {
		return therapy;
	}

	public void setTherapy(Therapy therapy) {
		this.therapy = therapy;
	}

	public ClinicalCourse getClinicalCourse() {
		return clinicalCourse;
	}

	public void setClinicalCourse(ClinicalCourse clinicalCourse) {
		this.clinicalCourse = clinicalCourse;
	}

	public MaternalHistory getMaternalHistory() {
		return maternalHistory;
	}

	public void setMaternalHistory(MaternalHistory maternalHistory) {
		this.maternalHistory = maternalHistory;
	}

	public PortHealthInfo getPortHealthInfo() {
		return portHealthInfo;
	}

	public void setPortHealthInfo(PortHealthInfo portHealthInfo) {
		this.portHealthInfo = portHealthInfo;
	}

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	public CaseOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	public Date getOutcomeDate() {
		return outcomeDate;
	}

	public void setOutcomeDate(Date outcomeDate) {
		this.outcomeDate = outcomeDate;
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}

	public DengueFeverType getDengueFeverType() {
		return dengueFeverType;
	}

	public void setDengueFeverType(DengueFeverType dengueFeverType) {
		this.dengueFeverType = dengueFeverType;
	}

	public User getClassificationUser() {
		return classificationUser;
	}

	public void setClassificationUser(User classificationUser) {
		this.classificationUser = classificationUser;
	}

	public Date getClassificationDate() {
		return classificationDate;
	}

	public void setClassificationDate(Date classificationDate) {
		this.classificationDate = classificationDate;
	}

	public String getClassificationComment() {
		return classificationComment;
	}

	public void setClassificationComment(String classificationComment) {
		this.classificationComment = classificationComment;
	}

	public YesNoUnknown getClinicalConfirmation() {
		return clinicalConfirmation;
	}

	public void setClinicalConfirmation(YesNoUnknown clinicalConfirmation) {
		this.clinicalConfirmation = clinicalConfirmation;
	}

	public YesNoUnknown getEpidemiologicalConfirmation() {
		return epidemiologicalConfirmation;
	}

	public void setEpidemiologicalConfirmation(YesNoUnknown epidemiologicalConfirmation) {
		this.epidemiologicalConfirmation = epidemiologicalConfirmation;
	}

	public YesNoUnknown getLaboratoryDiagnosticConfirmation() {
		return laboratoryDiagnosticConfirmation;
	}

	public void setLaboratoryDiagnosticConfirmation(YesNoUnknown laboratoryDiagnosticConfirmation) {
		this.laboratoryDiagnosticConfirmation = laboratoryDiagnosticConfirmation;
	}

	@Override
	public boolean isModifiedOrChildModified() {
		if (person.isModifiedOrChildModified())
			return true;
		return super.isModifiedOrChildModified();
	}

	@Override
	public boolean isUnreadOrChildUnread() {
		if (person.isUnreadOrChildUnread())
			return true;
		return super.isUnreadOrChildUnread();
	}

	@Override
	public String toString() {
		return super.toString() + " " + (getPerson() != null ? getPerson().toString() : "") + " (" + DataHelper.getShortUuid(getUuid()) + ")";
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public YesNoUnknown getSequelae() {
		return sequelae;
	}

	public void setSequelae(YesNoUnknown sequelae) {
		this.sequelae = sequelae;
	}

	public String getSequelaeDetails() {
		return sequelaeDetails;
	}

	public void setSequelaeDetails(String sequelaeDetails) {
		this.sequelaeDetails = sequelaeDetails;
	}

	public HospitalWardType getNotifyingClinic() {
		return notifyingClinic;
	}

	public void setNotifyingClinic(HospitalWardType notifyingClinic) {
		this.notifyingClinic = notifyingClinic;
	}

	public String getNotifyingClinicDetails() {
		return notifyingClinicDetails;
	}

	public void setNotifyingClinicDetails(String notifyingClinicDetails) {
		this.notifyingClinicDetails = notifyingClinicDetails;
	}

	public PointOfEntry getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntry pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public String getPointOfEntryDetails() {
		return pointOfEntryDetails;
	}

	public void setPointOfEntryDetails(String pointOfEntryDetails) {
		this.pointOfEntryDetails = pointOfEntryDetails;
	}

	public CaseOrigin getCaseOrigin() {
		return caseOrigin;
	}

	public void setCaseOrigin(CaseOrigin caseOrigin) {
		this.caseOrigin = caseOrigin;
	}

	public String getCreationVersion() {
		return creationVersion;
	}

	public void setCreationVersion(String creationVersion) {
		this.creationVersion = creationVersion;
	}

	public Float getCompleteness() {
		return completeness;
	}

	public void setCompleteness(Float completeness) {
		this.completeness = completeness;
	}

	public String getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	public String getExternalToken() {
		return externalToken;
	}

	public void setExternalToken(String externalToken) {
		this.externalToken = externalToken;
	}

	public QuarantineType getQuarantine() {
		return quarantine;
	}

	public void setQuarantine(QuarantineType quarantine) {
		this.quarantine = quarantine;
	}

	public String getQuarantineTypeDetails() {
		return quarantineTypeDetails;
	}

	public void setQuarantineTypeDetails(String quarantineTypeDetails) {
		this.quarantineTypeDetails = quarantineTypeDetails;
	}

	public Date getQuarantineFrom() {
		return quarantineFrom;
	}

	public void setQuarantineFrom(Date quarantineFrom) {
		this.quarantineFrom = quarantineFrom;
	}

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	public String getQuarantineHelpNeeded() {
		return quarantineHelpNeeded;
	}

	public void setQuarantineHelpNeeded(String quarantineHelpNeeded) {
		this.quarantineHelpNeeded = quarantineHelpNeeded;
	}

	public boolean isQuarantineOrderedVerbally() {
		return quarantineOrderedVerbally;
	}

	public void setQuarantineOrderedVerbally(boolean quarantineOrderedVerbally) {
		this.quarantineOrderedVerbally = quarantineOrderedVerbally;
	}

	public boolean isQuarantineOrderedOfficialDocument() {
		return quarantineOrderedOfficialDocument;
	}

	public void setQuarantineOrderedOfficialDocument(boolean quarantineOrderedOfficialDocument) {
		this.quarantineOrderedOfficialDocument = quarantineOrderedOfficialDocument;
	}

	public Date getQuarantineOrderedVerballyDate() {
		return quarantineOrderedVerballyDate;
	}

	public void setQuarantineOrderedVerballyDate(Date quarantineOrderedVerballyDate) {
		this.quarantineOrderedVerballyDate = quarantineOrderedVerballyDate;
	}

	public Date getQuarantineOrderedOfficialDocumentDate() {
		return quarantineOrderedOfficialDocumentDate;
	}

	public void setQuarantineOrderedOfficialDocumentDate(Date quarantineOrderedOfficialDocumentDate) {
		this.quarantineOrderedOfficialDocumentDate = quarantineOrderedOfficialDocumentDate;
	}

	public boolean isQuarantineExtended() {
		return quarantineExtended;
	}

	public void setQuarantineExtended(boolean quarantineExtended) {
		this.quarantineExtended = quarantineExtended;
	}

	public boolean isQuarantineReduced() {
		return quarantineReduced;
	}

	public void setQuarantineReduced(boolean quarantineReduced) {
		this.quarantineReduced = quarantineReduced;
	}

	public YesNoUnknown getQuarantineHomePossible() {
		return quarantineHomePossible;
	}

	public void setQuarantineHomePossible(YesNoUnknown quarantineHomePossible) {
		this.quarantineHomePossible = quarantineHomePossible;
	}

	public String getQuarantineHomePossibleComment() {
		return quarantineHomePossibleComment;
	}

	public void setQuarantineHomePossibleComment(String quarantineHomePossibleComment) {
		this.quarantineHomePossibleComment = quarantineHomePossibleComment;
	}

	public YesNoUnknown getQuarantineHomeSupplyEnsured() {
		return quarantineHomeSupplyEnsured;
	}

	public void setQuarantineHomeSupplyEnsured(YesNoUnknown quarantineHomeSupplyEnsured) {
		this.quarantineHomeSupplyEnsured = quarantineHomeSupplyEnsured;
	}

	public String getQuarantineHomeSupplyEnsuredComment() {
		return quarantineHomeSupplyEnsuredComment;
	}

	public void setQuarantineHomeSupplyEnsuredComment(String quarantineHomeSupplyEnsuredComment) {
		this.quarantineHomeSupplyEnsuredComment = quarantineHomeSupplyEnsuredComment;
	}

	public boolean isQuarantineOfficialOrderSent() {
		return quarantineOfficialOrderSent;
	}

	public void setQuarantineOfficialOrderSent(boolean quarantineOfficialOrderSent) {
		this.quarantineOfficialOrderSent = quarantineOfficialOrderSent;
	}

	public Date getQuarantineOfficialOrderSentDate() {
		return quarantineOfficialOrderSentDate;
	}

	public void setQuarantineOfficialOrderSentDate(Date quarantineOfficialOrderSentDate) {
		this.quarantineOfficialOrderSentDate = quarantineOfficialOrderSentDate;
	}

	public ReportingType getReportingType() {
		return reportingType;
	}

	public void setReportingType(ReportingType reportingType) {
		this.reportingType = reportingType;
	}

	public YesNoUnknown getPostpartum() {
		return postpartum;
	}

	public void setPostpartum(YesNoUnknown postpartum) {
		this.postpartum = postpartum;
	}

	public Trimester getTrimester() {
		return trimester;
	}

	public void setTrimester(Trimester trimester) {
		this.trimester = trimester;
	}

	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
	}

	public Integer getCaseIdIsm() {
		return caseIdIsm;
	}

	public void setCaseIdIsm(Integer caseIdIsm) {
		this.caseIdIsm = caseIdIsm;
	}

	public CovidTestReason getCovidTestReason() {
		return covidTestReason;
	}

	public void setCovidTestReason(CovidTestReason covidTestReason) {
		this.covidTestReason = covidTestReason;
	}

	public String getCovidTestReasonDetails() {
		return covidTestReasonDetails;
	}

	public void setCovidTestReasonDetails(String covidTestReasonDetails) {
		this.covidTestReasonDetails = covidTestReasonDetails;
	}

	public ContactTracingContactType getContactTracingFirstContactType() {
		return contactTracingFirstContactType;
	}

	public void setContactTracingFirstContactType(ContactTracingContactType contactTracingFirstContactType) {
		this.contactTracingFirstContactType = contactTracingFirstContactType;
	}

	public Date getContactTracingFirstContactDate() {
		return contactTracingFirstContactDate;
	}

	public void setContactTracingFirstContactDate(Date contactTracingFirstContactDate) {
		this.contactTracingFirstContactDate = contactTracingFirstContactDate;
	}

	public YesNoUnknown getWasInQuarantineBeforeIsolation() {
		return wasInQuarantineBeforeIsolation;
	}

	public void setWasInQuarantineBeforeIsolation(YesNoUnknown wasInQuarantineBeforeIsolation) {
		this.wasInQuarantineBeforeIsolation = wasInQuarantineBeforeIsolation;
	}

	public QuarantineReason getQuarantineReasonBeforeIsolation() {
		return quarantineReasonBeforeIsolation;
	}

	public void setQuarantineReasonBeforeIsolation(QuarantineReason quarantineReasonBeforeIsolation) {
		this.quarantineReasonBeforeIsolation = quarantineReasonBeforeIsolation;
	}

	public String getQuarantineReasonBeforeIsolationDetails() {
		return quarantineReasonBeforeIsolationDetails;
	}

	public void setQuarantineReasonBeforeIsolationDetails(String quarantineReasonBeforeIsolationDetails) {
		this.quarantineReasonBeforeIsolationDetails = quarantineReasonBeforeIsolationDetails;
	}

	public EndOfIsolationReason getEndOfIsolationReason() {
		return endOfIsolationReason;
	}

	public void setEndOfIsolationReason(EndOfIsolationReason endOfIsolationReason) {
		this.endOfIsolationReason = endOfIsolationReason;
	}

	public String getEndOfIsolationReasonDetails() {
		return endOfIsolationReasonDetails;
	}

	public void setEndOfIsolationReasonDetails(String endOfIsolationReasonDetails) {
		this.endOfIsolationReasonDetails = endOfIsolationReasonDetails;
	}

	public boolean isNosocomialOutbreak() {
		return nosocomialOutbreak;
	}

	public void setNosocomialOutbreak(boolean nosocomialOutbreak) {
		this.nosocomialOutbreak = nosocomialOutbreak;
	}

	public InfectionSetting getInfectionSetting() {
		return infectionSetting;
	}

	public void setInfectionSetting(InfectionSetting infectionSetting) {
		this.infectionSetting = infectionSetting;
	}

	public YesNoUnknown getProhibitionToWork() {
		return prohibitionToWork;
	}

	public void setProhibitionToWork(YesNoUnknown prohibitionToWork) {
		this.prohibitionToWork = prohibitionToWork;
	}

	public Date getProhibitionToWorkFrom() {
		return prohibitionToWorkFrom;
	}

	public void setProhibitionToWorkFrom(Date prohibitionToWorkFrom) {
		this.prohibitionToWorkFrom = prohibitionToWorkFrom;
	}

	public Date getProhibitionToWorkUntil() {
		return prohibitionToWorkUntil;
	}

	public void setProhibitionToWorkUntil(Date prohibitionToWorkUntil) {
		this.prohibitionToWorkUntil = prohibitionToWorkUntil;
	}

	public YesNoUnknown getReInfection() {
		return reInfection;
	}

	public void setReInfection(YesNoUnknown reInfection) {
		this.reInfection = reInfection;
	}

	public Date getPreviousInfectionDate() {
		return previousInfectionDate;
	}

	public void setPreviousInfectionDate(Date previousInfectionDate) {
		this.previousInfectionDate = previousInfectionDate;
	}

	public District getReportingDistrict() {
		return reportingDistrict;
	}

	public void setReportingDistrict(District reportingDistrict) {
		this.reportingDistrict = reportingDistrict;
	}

	public YesNoUnknown getBloodOrganOrTissueDonated() {
		return bloodOrganOrTissueDonated;
	}

	public void setBloodOrganOrTissueDonated(YesNoUnknown bloodOrganOrTissueDonated) {
		this.bloodOrganOrTissueDonated = bloodOrganOrTissueDonated;
	}

	public SormasToSormasOriginInfo getSormasToSormasOriginInfo() {
		return sormasToSormasOriginInfo;
	}

	public void setSormasToSormasOriginInfo(SormasToSormasOriginInfo sormasToSormasOriginInfo) {
		this.sormasToSormasOriginInfo = sormasToSormasOriginInfo;
	}

	public boolean isOwnershipHandedOver() {
		return ownershipHandedOver;
	}

	public void setOwnershipHandedOver(boolean ownershipHandedOver) {
		this.ownershipHandedOver = ownershipHandedOver;
	}

	public boolean isNotACaseReasonNegativeTest() {
		return notACaseReasonNegativeTest;
	}

	public void setNotACaseReasonNegativeTest(boolean notACaseReasonNegativeTest) {
		this.notACaseReasonNegativeTest = notACaseReasonNegativeTest;
	}

	public boolean isNotACaseReasonPhysicianInformation() {
		return notACaseReasonPhysicianInformation;
	}

	public void setNotACaseReasonPhysicianInformation(boolean notACaseReasonPhysicianInformation) {
		this.notACaseReasonPhysicianInformation = notACaseReasonPhysicianInformation;
	}

	public boolean isNotACaseReasonDifferentPathogen() {
		return notACaseReasonDifferentPathogen;
	}

	public void setNotACaseReasonDifferentPathogen(boolean notACaseReasonDifferentPathogen) {
		this.notACaseReasonDifferentPathogen = notACaseReasonDifferentPathogen;
	}

	public boolean isNotACaseReasonOther() {
		return notACaseReasonOther;
	}

	public void setNotACaseReasonOther(boolean notACaseReasonOther) {
		this.notACaseReasonOther = notACaseReasonOther;
	}

	public String getNotACaseReasonDetails() {
		return notACaseReasonDetails;
	}

	public void setNotACaseReasonDetails(String notACaseReasonDetails) {
		this.notACaseReasonDetails = notACaseReasonDetails;
	}
}
