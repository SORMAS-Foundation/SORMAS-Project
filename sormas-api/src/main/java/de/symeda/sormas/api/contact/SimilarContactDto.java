package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.utils.PersonalData;

public class SimilarContactDto implements Serializable {

	private static final long serialVersionUID = -7290520732250426907L;

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String UUID = "uuid";
	public static final String CASE_ID_EXTERNAL_SYSTEM = "caseIdExternalSystem";
	public static final String CAZE = "caze";
	public static final String LAST_CONTACT_DATE = "lastContactDate";
	public static final String CONTACT_PROXIMITY = "contactProximity";
	public static final String CONTACT_CLASSIFICATION = "contactClassification";
	public static final String CONTACT_STATUS = "contactStatus";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";

	@PersonalData
	private String firstName;
	@PersonalData
	private String lastName;
	private String uuid;
	private CaseReferenceDto caze;
	private String caseIdExternalSystem;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	private ContactJurisdictionDto jurisdiction;
	private CaseJurisdictionDto caseJurisdiction;

	public SimilarContactDto(String firstName, String lastName, String uuid,
							 String cazeUuid, String caseFirstName, String caseLastName, String caseIdExternalSystem,
							 Date lastContactDate, ContactProximity contactProximity, ContactClassification contactClassification,
							 ContactStatus contactStatus, FollowUpStatus followUpStatus,

							 String reportingUserUuid, String regionUuid, String districtUuid,
							 String caseReportingUuid, String caseRegionUuid, String caseDistrictUuid, String caseCommunityUuid,
							 String caseHealthFacilityUuid, String casePointOfEntryUuid) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.uuid = uuid;

		CaseJurisdictionDto caseJurisdiction = null;
		if (cazeUuid != null) {
			this.caze = new CaseReferenceDto(cazeUuid, caseFirstName, caseLastName);
			this.caseJurisdiction = new CaseJurisdictionDto(caseReportingUuid, caseRegionUuid, caseDistrictUuid,
					caseCommunityUuid, caseHealthFacilityUuid, casePointOfEntryUuid);
		}
		this.caseIdExternalSystem = caseIdExternalSystem;
		this.lastContactDate = lastContactDate;
		this.contactProximity = contactProximity;
		this.contactClassification = contactClassification;
		this.contactStatus = contactStatus;
		this.followUpStatus = followUpStatus;

		this.jurisdiction = new ContactJurisdictionDto(reportingUserUuid, regionUuid, districtUuid, caseJurisdiction);
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public String getCaseIdExternalSystem() {
		return caseIdExternalSystem;
	}

	public void setCaseIdExternalSystem(String caseIdExternalSystem) {
		this.caseIdExternalSystem = caseIdExternalSystem;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ContactClassification getContactClassification() {
		return contactClassification;
	}

	public void setContactClassification(ContactClassification contactClassification) {
		this.contactClassification = contactClassification;
	}

	public ContactStatus getContactStatus() {
		return contactStatus;
	}

	public void setContactStatus(ContactStatus contactStatus) {
		this.contactStatus = contactStatus;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public ContactJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}

	public CaseJurisdictionDto getCaseJurisdiction() {
		return caseJurisdiction;
	}
}
