package de.symeda.sormas.api.caze;

import java.sql.Timestamp;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.person.PresentCondition;

public class CaseIndexDto extends EntityDto {

	private static final long serialVersionUID = -7764607075875188799L;

	public static final String I18N_PREFIX = "CaseData";
	
	public static final String EPID_NUMBER = "epidNumber";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String REPORT_DATE = "reportDate";
	public static final String REGION_UUID = "regionUuid";
	public static final String DISTRICT_UUID = "districtUuid";
	public static final String DISTRICT_NAME = "districtName";
	public static final String HEALTH_FACILITY_UUID = "healthFacilityUuid";
	public static final String SURVEILLANCE_OFFICER_UUID = "surveillanceOfficerUuid";

	private String epidNumber;
	private String personFirstName;
	private String personLastName;
	private Disease disease;
	private String diseaseDetails;
	private CaseClassification caseClassification;
	private InvestigationStatus investigationStatus;
	private PresentCondition presentCondition;
	private Date reportDate;
	private String regionUuid;
	private String districtUuid;
	private String districtName;
	private String healthFacilityUuid;
	private String surveillanceOfficerUuid;
		
	public CaseIndexDto(Timestamp creationDate, Timestamp changeDate, String uuid, String epidNumber, String personFirstName, String personLastName, Disease disease,
			String diseaseDetails, CaseClassification caseClassification, InvestigationStatus investigationStatus,
			PresentCondition presentCondition, Date reportDate, String regionUuid, 
			String districtUuid, String districtName, String healthFacilityUuid, String surveillanceOfficerUuid
			) {
		super(creationDate, changeDate, uuid);
		this.epidNumber = epidNumber;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.caseClassification = caseClassification;
		this.investigationStatus = investigationStatus;
		this.presentCondition = presentCondition;
		this.reportDate = reportDate;
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.districtName = districtName;
		this.healthFacilityUuid = healthFacilityUuid;
		this.surveillanceOfficerUuid = surveillanceOfficerUuid;
	}
	
	public String getEpidNumber() {
		return epidNumber;
	}
	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}
	public String getPersonFirstName() {
		return personFirstName;
	}
	public void setPersonFirstName(String personFirstName) {
		this.personFirstName = personFirstName;
	}
	public String getPersonLastName() {
		return personLastName;
	}
	public void setPersonLastName(String personLastName) {
		this.personLastName = personLastName;
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
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}
	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}
	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}
	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	public String getRegionUuid() {
		return regionUuid;
	}
	public void setRegionUuid(String regionUuid) {
		this.regionUuid = regionUuid;
	}
	public String getDistrictUuid() {
		return districtUuid;
	}
	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}
	public String getSurveillanceOfficerUuid() {
		return surveillanceOfficerUuid;
	}
	public void setSurveillanceOfficerUuid(String surveillanceOfficerUuid) {
		this.surveillanceOfficerUuid = surveillanceOfficerUuid;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public PresentCondition getPresentCondition() {
		return presentCondition;
	}
	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}
	public String getHealthFacilityUuid() {
		return healthFacilityUuid;
	}
	public void setHealthFacilityUuid(String healthFacilityUuid) {
		this.healthFacilityUuid = healthFacilityUuid;
	}
	
	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(getUuid());
	}
	
	@Override
	public String toString() {
		return CaseDataDto.buildCaption(getUuid(), personFirstName, personLastName);
	}
	
}
