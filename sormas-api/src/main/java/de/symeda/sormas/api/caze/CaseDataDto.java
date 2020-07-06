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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.PseudonymizableDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.Required;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitDto;

public class CaseDataDto extends PseudonymizableDto {

	private static final long serialVersionUID = 5007131477733638086L;

	public static final String I18N_PREFIX = "CaseData";

	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String CLASSIFICATION_USER = "classificationUser";
	public static final String CLASSIFICATION_DATE = "classificationDate";
	public static final String CLASSIFICATION_COMMENT = "classificationComment";
	public static final String CLASSIFIED_BY = "classifiedBy";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String PLAGUE_TYPE = "plagueType";
	public static final String DENGUE_FEVER_TYPE = "dengueFeverType";
	public static final String RABIES_TYPE = "rabiesType";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String HEALTH_FACILITY_DETAILS = "healthFacilityDetails";
	public static final String NONE_HEALTH_FACILITY_DETAILS = "noneHealthFacilityDetails";
	public static final String REPORTING_USER = "reportingUser";
	public static final String REPORT_DATE = "reportDate";
	public static final String INVESTIGATED_DATE = "investigatedDate";
	public static final String DISTRICT_LEVEL_DATE = "districtLevelDate";
	public static final String REGION_LEVEL_DATE = "regionLevelDate";
	public static final String NATIONAL_LEVEL_DATE = "nationalLevelDate";
	public static final String SURVEILLANCE_OFFICER = "surveillanceOfficer";
	public static final String SYMPTOMS = "symptoms";
	public static final String HOSPITALIZATION = "hospitalization";
	public static final String EPI_DATA = "epiData";
	public static final String THERAPY = "therapy";
	public static final String CLINICAL_COURSE = "clinicalCourse";
	public static final String MATERNAL_HISTORY = "maternalHistory";
	public static final String PORT_HEALTH_INFO = "portHealthInfo";
	public static final String PREGNANT = "pregnant";
	public static final String VACCINATION = "vaccination";
	public static final String VACCINATION_DOSES = "vaccinationDoses";
	public static final String VACCINATION_INFO_SOURCE = "vaccinationInfoSource";
	public static final String VACCINATION_DATE = "vaccinationDate";
	public static final String VACCINE = "vaccine";
	public static final String SMALLPOX_VACCINATION_SCAR = "smallpoxVaccinationScar";
	public static final String SMALLPOX_VACCINATION_RECEIVED = "smallpoxVaccinationReceived";
	public static final String EPID_NUMBER = "epidNumber";
	public static final String REPORT_LAT = "reportLat";
	public static final String REPORT_LON = "reportLon";
	public static final String REPORT_LAT_LON_ACCURACY = "reportLatLonAccuracy";
	public static final String OUTCOME = "outcome";
	public static final String OUTCOME_DATE = "outcomeDate";
	public static final String SEQUELAE = "sequelae";
	public static final String SEQUELAE_DETAILS = "sequelaeDetails";
	public static final String CLINICIAN_NAME = "clinicianName";
	public static final String CLINICIAN_PHONE = "clinicianPhone";
	public static final String CLINICIAN_EMAIL = "clinicianEmail";
	public static final String NOTIFYING_CLINIC = "notifyingClinic";
	public static final String NOTIFYING_CLINIC_DETAILS = "notifyingClinicDetails";
	public static final String CASE_ORIGIN = "caseOrigin";
	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String POINT_OF_ENTRY_DETAILS = "pointOfEntryDetails";
	public static final String ADDITIONAL_DETAILS = "additionalDetails";
	public static final String EXTERNAL_ID = "externalID";
	public static final String SHARED_TO_COUNTRY = "sharedToCountry";
	public static final String QUARANTINE = "quarantine";
	public static final String QUARANTINE_TYPE_DETAILS = "quarantineTypeDetails";
	public static final String QUARANTINE_FROM = "quarantineFrom";
	public static final String QUARANTINE_TO = "quarantineTo";
	public static final String QUARANTINE_HELP_NEEDED = "quarantineHelpNeeded";
	public static final String QUARANTINE_ORDERED_VERBALLY = "quarantineOrderedVerbally";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT = "quarantineOrderedOfficialDocument";
	public static final String QUARANTINE_ORDERED_VERBALLY_DATE = "quarantineOrderedVerballyDate";
	public static final String QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE = "quarantineOrderedOfficialDocumentDate";
	public static final String QUARANTINE_HOME_POSSIBLE = "quarantineHomePossible";
	public static final String QUARANTINE_HOME_POSSIBLE_COMMENT = "quarantineHomePossibleComment";
	public static final String QUARANTINE_HOME_SUPPLY_ENSURED = "quarantineHomeSupplyEnsured";
	public static final String QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT = "quarantineHomeSupplyEnsuredComment";
	public static final String REPORTING_TYPE = "reportingType";
	public static final String POSTPARTUM = "postpartum";
	public static final String TRIMESTER = "trimester";

	// Fields are declared in the order they should appear in the import template

	@Outbreaks
	@Required
	private Disease disease;
	@Outbreaks
	private String diseaseDetails;
	@Diseases({
		Disease.PLAGUE })
	@Outbreaks
	private PlagueType plagueType;
	@Diseases({
		Disease.DENGUE })
	@Outbreaks
	private DengueFeverType dengueFeverType;
	@Diseases({
		Disease.RABIES })
	@Outbreaks
	private RabiesType rabiesType;
	@Required
	private PersonReferenceDto person;
	@Outbreaks
	private String epidNumber;
	@Outbreaks
	@Required
	private Date reportDate;
	@Outbreaks
	@Required
	private UserReferenceDto reportingUser;
	private Date regionLevelDate;
	private Date nationalLevelDate;
	@Outbreaks
	private Date districtLevelDate;
	@Outbreaks
	@Required
	private CaseClassification caseClassification;
	@Outbreaks
	private UserReferenceDto classificationUser;
	@Outbreaks
	private Date classificationDate;
	@Outbreaks
	private String classificationComment;
	@Outbreaks
	@Required
	private InvestigationStatus investigationStatus;
	@Outbreaks
	private Date investigatedDate;
	@Outbreaks
	private CaseOutcome outcome;
	@Outbreaks
	private Date outcomeDate;
	private YesNoUnknown sequelae;
	private String sequelaeDetails;
	@Outbreaks
	@Required
	private RegionReferenceDto region;
	@Outbreaks
	@Required
	private DistrictReferenceDto district;
	@Outbreaks
	@PersonalData
	private CommunityReferenceDto community;
	@Outbreaks
	@Required
	@PersonalData
	private FacilityReferenceDto healthFacility;
	@Outbreaks
	@PersonalData
	private String healthFacilityDetails;
	private YesNoUnknown pregnant;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.OTHER })
	@Outbreaks
	private Vaccination vaccination;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.CSM,
		Disease.YELLOW_FEVER,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.OTHER })
	@Outbreaks
	private String vaccinationDoses;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.MONKEYPOX,
		Disease.UNSPECIFIED_VHF,
		Disease.RABIES,
		Disease.ANTHRAX,
		Disease.OTHER })
	@Outbreaks
	private Date vaccinationDate;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.MEASLES,
		Disease.POLIO,
		Disease.YELLOW_FEVER,
		Disease.CSM,
		Disease.RABIES,
		Disease.UNSPECIFIED_VHF,
		Disease.ANTHRAX,
		Disease.OTHER })
	private VaccinationInfoSource vaccinationInfoSource;
	@Diseases({
		Disease.AFP,
		Disease.GUINEA_WORM,
		Disease.POLIO,
		Disease.RABIES,
		Disease.OTHER })
	@Outbreaks
	private String vaccine;
	@Diseases({
		Disease.MONKEYPOX })
	private YesNoUnknown smallpoxVaccinationScar;
	@Diseases({
		Disease.MONKEYPOX })
	private YesNoUnknown smallpoxVaccinationReceived;
	@Outbreaks
	private UserReferenceDto surveillanceOfficer;
	private String clinicianName;
	private String clinicianPhone;
	private String clinicianEmail;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private HospitalWardType notifyingClinic;
	@Diseases({
		Disease.CONGENITAL_RUBELLA })
	private String notifyingClinicDetails;
	@Deprecated
	private UserReferenceDto caseOfficer;
	private Double reportLat;
	private Double reportLon;
	private Float reportLatLonAccuracy;
	private HospitalizationDto hospitalization;
	private SymptomsDto symptoms;
	private EpiDataDto epiData;
	private TherapyDto therapy;
	private ClinicalCourseDto clinicalCourse;
	private MaternalHistoryDto maternalHistory;
	private String creationVersion;
	private PortHealthInfoDto portHealthInfo;
	private CaseOrigin caseOrigin;
	@PersonalData
	private PointOfEntryReferenceDto pointOfEntry;
	@PersonalData
	private String pointOfEntryDetails;
	private String additionalDetails;
	private String externalID;
	private boolean sharedToCountry;
	private QuarantineType quarantine;
	private String quarantineTypeDetails;
	private Date quarantineFrom;
	private Date quarantineTo;
	private String quarantineHelpNeeded;
	private boolean quarantineOrderedVerbally;
	private boolean quarantineOrderedOfficialDocument;
	private Date quarantineOrderedVerballyDate;
	private Date quarantineOrderedOfficialDocumentDate;
	@HideForCountriesExcept
	private YesNoUnknown quarantineHomePossible;
	@HideForCountriesExcept
	private String quarantineHomePossibleComment;
	@HideForCountriesExcept
	private YesNoUnknown quarantineHomeSupplyEnsured;
	@HideForCountriesExcept
	private String quarantineHomeSupplyEnsuredComment;
	private ReportingType reportingType;
	private YesNoUnknown postpartum;
	private Trimester trimester;

	public static CaseDataDto build(PersonReferenceDto person, Disease disease) {
		CaseDataDto caze = new CaseDataDto();
		caze.setUuid(DataHelper.createUuid());
		caze.setPerson(person);
		caze.setHospitalization(HospitalizationDto.build());
		caze.setEpiData(EpiDataDto.build());
		caze.setSymptoms(SymptomsDto.build());
		caze.setTherapy(TherapyDto.build());
		caze.setClinicalCourse(ClinicalCourseDto.build());
		caze.setMaternalHistory(MaternalHistoryDto.build());
		caze.setPortHealthInfo(PortHealthInfoDto.build());
		caze.setDisease(disease);
		caze.setInvestigationStatus(InvestigationStatus.PENDING);
		caze.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
		caze.setOutcome(CaseOutcome.NO_OUTCOME);
		caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
		return caze;
	}

	public static CaseDataDto buildFromContact(ContactDto contact, VisitDto lastVisit) {

		CaseDataDto cazeData = CaseDataDto.build(contact.getPerson(), contact.getDisease());
		cazeData.setEpiData(contact.getEpiData());
		SymptomsDto newSymptoms = cazeData.getSymptoms();
		if (lastVisit != null) {
			SymptomsDto oldSymptoms = lastVisit.getSymptoms();

			try {
				// reflection to call the setters of the new symptoms object with the getters
				// from the one in the visit
				for (PropertyDescriptor pd : Introspector.getBeanInfo(SymptomsDto.class, EntityDto.class).getPropertyDescriptors()) {
					if (pd.getWriteMethod() != null) {
						try {
							pd.getWriteMethod().invoke(newSymptoms, pd.getReadMethod().invoke(oldSymptoms));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					}
				}
			} catch (IntrospectionException e) {
				throw new RuntimeException(e);
			}
		}
		cazeData.setSymptoms(newSymptoms);
		return cazeData;
	}

	public static CaseDataDto buildFromEventParticipant(EventParticipantDto eventParticipant, Disease eventDisease) {

		CaseDataDto cazeData = CaseDataDto.build(eventParticipant.getPerson().toReference(), eventDisease);
		return cazeData;
	}

	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(getUuid(), getPerson().getFirstName(), getPerson().getLastName());
	}

	/**
	 * Returns true if the case is an original point of entry case and has not yet
	 * been assigned a health facility.
	 */
	public boolean isUnreferredPortHealthCase() {
		return caseOrigin == CaseOrigin.POINT_OF_ENTRY && healthFacility == null;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto personDto) {
		this.person = personDto;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public UserReferenceDto getClassificationUser() {
		return classificationUser;
	}

	public void setClassificationUser(UserReferenceDto classificationUser) {
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

	public PlagueType getPlagueType() {
		return plagueType;
	}

	public void setPlagueType(PlagueType plagueType) {
		this.plagueType = plagueType;
	}

	public DengueFeverType getDengueFeverType() {
		return dengueFeverType;
	}

	public void setDengueFeverType(DengueFeverType dengueFeverType) {
		this.dengueFeverType = dengueFeverType;
	}

	public RabiesType getRabiesType() {
		return rabiesType;
	}

	public void setRabiesType(RabiesType rabiesType) {
		this.rabiesType = rabiesType;
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

	public Date getInvestigatedDate() {
		return investigatedDate;
	}

	public void setInvestigatedDate(Date investigatedDate) {
		this.investigatedDate = investigatedDate;
	}

	public Date getRegionLevelDate() {
		return regionLevelDate;
	}

	public void setRegionLevelDate(Date regionLevelDate) {
		this.regionLevelDate = regionLevelDate;
	}

	public Date getNationalLevelDate() {
		return nationalLevelDate;
	}

	public void setNationalLevelDate(Date nationalLevelDate) {
		this.nationalLevelDate = nationalLevelDate;
	}

	public Date getDistrictLevelDate() {
		return districtLevelDate;
	}

	public void setDistrictLevelDate(Date districtLevelDate) {
		this.districtLevelDate = districtLevelDate;
	}

	public UserReferenceDto getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public void setSurveillanceOfficer(UserReferenceDto surveillanceOfficer) {
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

	@Deprecated
	public UserReferenceDto getCaseOfficer() {
		return caseOfficer;
	}

	@Deprecated
	public void setCaseOfficer(UserReferenceDto caseOfficer) {
		this.caseOfficer = caseOfficer;
	}

	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
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

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public HospitalizationDto getHospitalization() {
		return hospitalization;
	}

	public void setHospitalization(HospitalizationDto hospitalization) {
		this.hospitalization = hospitalization;
	}

	public EpiDataDto getEpiData() {
		return epiData;
	}

	public void setEpiData(EpiDataDto epiData) {
		this.epiData = epiData;
	}

	public TherapyDto getTherapy() {
		return therapy;
	}

	public void setTherapy(TherapyDto therapy) {
		this.therapy = therapy;
	}

	public ClinicalCourseDto getClinicalCourse() {
		return clinicalCourse;
	}

	public void setClinicalCourse(ClinicalCourseDto clinicalCourse) {
		this.clinicalCourse = clinicalCourse;
	}

	public MaternalHistoryDto getMaternalHistory() {
		return maternalHistory;
	}

	public void setMaternalHistory(MaternalHistoryDto maternalHistory) {
		this.maternalHistory = maternalHistory;
	}

	public PortHealthInfoDto getPortHealthInfo() {
		return portHealthInfo;
	}

	public void setPortHealthInfo(PortHealthInfoDto portHealthInfo) {
		this.portHealthInfo = portHealthInfo;
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

	public Date getVaccinationDate() {
		return vaccinationDate;
	}

	public void setVaccinationDate(Date vaccinationDate) {
		this.vaccinationDate = vaccinationDate;
	}

	public String getVaccine() {
		return vaccine;
	}

	public void setVaccine(String vaccine) {
		this.vaccine = vaccine;
	}

	public String getEpidNumber() {
		return epidNumber;
	}

	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
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

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
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

	@ImportIgnore
	public String getCreationVersion() {
		return creationVersion;
	}

	public void setCreationVersion(String creationVersion) {
		this.creationVersion = creationVersion;
	}

	public CaseOrigin getCaseOrigin() {
		return caseOrigin;
	}

	public void setCaseOrigin(CaseOrigin caseOrigin) {
		this.caseOrigin = caseOrigin;
	}

	public PointOfEntryReferenceDto getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntryReferenceDto pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public String getPointOfEntryDetails() {
		return pointOfEntryDetails;
	}

	public void setPointOfEntryDetails(String pointOfEntryDetails) {
		this.pointOfEntryDetails = pointOfEntryDetails;
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

	public boolean isSharedToCountry() {
		return sharedToCountry;
	}

	public void setSharedToCountry(boolean sharedToCountry) {
		this.sharedToCountry = sharedToCountry;
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
}
