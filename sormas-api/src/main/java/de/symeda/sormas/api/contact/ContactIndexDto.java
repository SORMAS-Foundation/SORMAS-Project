package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;

public class ContactIndexDto implements Serializable {

	private static final long serialVersionUID = 7511900591141885152L;

	public static final String I18N_PREFIX = "Contact";
	
	public static final String UUID = "uuid";
	public static final String PERSON = "person";
	public static final String CAZE = "caze";
	public static final String CASE_DISEASE = "caseDisease";
	public static final String CASE_PERSON = "casePerson";
	public static final String CASE_REGION_UUID = "caseRegionUuid";
	public static final String CASE_DISTRICT_UUID = "caseDistrictUuid";
	public static final String CASE_HEALTH_FACILITY_UUID = "caseHealthFacilityUuid";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String CONTACT_OFFICER_UUID = "contactOfficerUuid";
	public static final String NUMBER_OF_COOPERATIVE_VISITS = "numberOfCooperativeVisits";
	public static final String NUMBER_OF_MISSED_VISITS = "numberOfMissedVisits";

	private String uuid;
	private PersonReferenceDto person;
	private CaseReferenceDto caze;
	private Disease caseDisease;
	private String caseDiseaseDetails;
	private String caseRegionUuid;
	private String caseDistrictUuid;
	private String caseHealthFacilityUuid;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private String contactOfficerUuid;
	
	public ContactIndexDto(String uuid, String personUuid, String personFirstName, String personLastName, String cazeUuid,
			Disease caseDisease, String caseDiseaseDetails, String casePersonUuid, String caseFirstName, String caseLastName, String caseRegionUuid,
			String caseDistrictUuid, String caseHealthFacilityUuid, Date lastContactDate, ContactProximity contactProximity,
			ContactClassification contactClassification, FollowUpStatus followUpStatus, Date followUpUntil, String contactOfficerUuid) {
		this.uuid = uuid;
		this.person = new PersonReferenceDto(personUuid, personFirstName, personLastName);
		this.caze = new CaseReferenceDto(cazeUuid, caseFirstName, caseLastName);
		this.caseDisease = caseDisease;
		this.caseDiseaseDetails = caseDiseaseDetails;
		this.caseRegionUuid = caseRegionUuid;
		this.caseDistrictUuid = caseDistrictUuid;
		this.caseHealthFacilityUuid = caseHealthFacilityUuid;
		this.lastContactDate = lastContactDate;
		this.contactProximity = contactProximity;
		this.contactClassification = contactClassification;
		this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.contactOfficerUuid = contactOfficerUuid;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public PersonReferenceDto getPerson() {
		return person;
	}
	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}
	public CaseReferenceDto getCaze() {
		return caze;
	}
	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}
	public Disease getCaseDisease() {
		return caseDisease;
	}
	public void setCaseDisease(Disease caseDisease) {
		this.caseDisease = caseDisease;
	}
	public String getCaseDiseaseDetails() {
		return caseDiseaseDetails;
	}
	public void setCaseDiseaseDetails(String caseDiseaseDetails) {
		this.caseDiseaseDetails = caseDiseaseDetails;
	}
	public Date getLastContactDate() {
		return lastContactDate;
	}
	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}
	public ContactProximity getContactProximity() {
		return contactProximity;
	}
	public void setContactProximity(ContactProximity contactProximity) {
		this.contactProximity = contactProximity;
	}
	public ContactClassification getContactClassification() {
		return contactClassification;
	}
	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}
	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}
	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}
	public Date getFollowUpUntil() {
		return followUpUntil;
	}
	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}
	public String getCaseRegionUuid() {
		return caseRegionUuid;
	}
	public void setCaseRegionUuid(String caseRegionUuid) {
		this.caseRegionUuid = caseRegionUuid;
	}
	public String getCaseDistrictUuid() {
		return caseDistrictUuid;
	}
	public void setCaseDistrictUuid(String caseDistrictUuid) {
		this.caseDistrictUuid = caseDistrictUuid;
	}
	public String getCaseHealthFacilityUuid() {
		return caseHealthFacilityUuid;
	}
	public void setCaseHealthFacilityUuid(String caseHealthFacilityUuid) {
		this.caseHealthFacilityUuid = caseHealthFacilityUuid;
	}
	public String getContactOfficerUuid() {
		return contactOfficerUuid;
	}
	public void setContactOfficerUuid(String contactOfficerUuid) {
		this.contactOfficerUuid = contactOfficerUuid;
	}

	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(uuid);
	}
	
}
