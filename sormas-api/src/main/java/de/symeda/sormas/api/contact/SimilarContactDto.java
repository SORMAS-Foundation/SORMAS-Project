package de.symeda.sormas.api.contact;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class SimilarContactDto extends PseudonymizableIndexDto implements Serializable {

	private static final long serialVersionUID = -7290520732250426907L;

	public static final String I18N_PREFIX = "Contact";

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
	private CaseReferenceDto caze;
	private String caseIdExternalSystem;
	private Date lastContactDate;
	private ContactProximity contactProximity;
	private ContactClassification contactClassification;
	private ContactStatus contactStatus;
	private FollowUpStatus followUpStatus;
	private ContactJurisdictionFlagsDto contactJurisdictionFlagsDto;

	//@formatter:off
	public SimilarContactDto(String firstName, String lastName, String uuid,
							 String cazeUuid, String caseFirstName, String caseLastName, String caseIdExternalSystem,
							 Date lastContactDate, ContactProximity contactProximity, ContactClassification contactClassification,
							 ContactStatus contactStatus, FollowUpStatus followUpStatus, boolean isInJurisdiction, boolean isCaseInJurisdiction) {
		//@formatter:on
		super(uuid);
		this.firstName = firstName;
		this.lastName = lastName;

		if (cazeUuid != null) {
			this.caze = new CaseReferenceDto(cazeUuid, caseFirstName, caseLastName);
		}
		this.caseIdExternalSystem = caseIdExternalSystem;
		this.lastContactDate = lastContactDate;
		this.contactProximity = contactProximity;
		this.contactClassification = contactClassification;
		this.contactStatus = contactStatus;
		this.followUpStatus = followUpStatus;
		this.contactJurisdictionFlagsDto = new ContactJurisdictionFlagsDto(isInJurisdiction, isCaseInJurisdiction);
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

	public Boolean getInJurisdiction() {
		return contactJurisdictionFlagsDto.getInJurisdiction();
	}

	public Boolean getCaseInJurisdiction() {
		return contactJurisdictionFlagsDto.getCaseInJurisdiction();
	}

	public ContactReferenceDto toReference() {
		return new ContactReferenceDto(getUuid(), getFirstName(), getLastName(), null, null);
	}
}
