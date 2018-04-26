package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;

public class CaseExportDto implements Serializable {

	private static final long serialVersionUID = 8581579464816945555L;

	public static final String I18N_PREFIX = "CaseData";
	
	public static final String UUID = "uuid";
	public static final String EPID_NUMBER = "epidNumber";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String DISEASE = "disease";
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String REPORT_DATE = "reportDate";
	public static final String REGION_UUID = "regionUuid";
	public static final String DISTRICT_UUID = "districtUuid";
	public static final String DISTRICT_NAME = "districtName";
	public static final String HEALTH_FACILITY_UUID = "healthFacilityUuid";
	public static final String SURVEILLANCE_OFFICER_UUID = "surveillanceOfficerUuid";
	public static final String OUTCOME = "outcome";

	private String uuid;
	private String epidNumber;
	private String disease;
	private String name;
	private Sex sex;
	private String age;
	private Date onsetDate;
	private Date admissionDate;
	private CaseClassification caseClassification;
	private InvestigationStatus investigationStatus;
	private PresentCondition presentCondition;
	private CaseOutcome outcome;
	private Date reportDate;
	private String regionName;
	private String districtName;
	private String communityName;

	private String symptoms;
	
	private String healthFacility;

	// TODO lab sample data and other fields


	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(getUuid(), name);
	}
	
	@Override
	public String toString() {
		return CaseReferenceDto.buildCaption(getUuid(), name);
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
		
	public String getEpidNumber() {
		return epidNumber;
	}
	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Date getOnsetDate() {
		return onsetDate;
	}

	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public CaseOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(String symptoms) {
		this.symptoms = symptoms;
	}

	public String getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(String healthFacility) {
		this.healthFacility = healthFacility;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}
}
