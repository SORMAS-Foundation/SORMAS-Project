package de.symeda.sormas.api.person;

import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

public class PersonContactDetailDto extends PseudonymizableDto {

	public static final String I18N_PREFIX = "PersonContactDetail";

	public static final String PERSON = "person";
	public static final String PRIMARY = "primaryContact";
	public static final String PERSON_CONTACT_DETAILS_TYPE = "personContactDetailType";
	public static final String PHONE_NUMBER_TYPE = "phoneNumberType";
	public static final String DETAILS = "details";
	public static final String CONTACT_INFORMATION = "contactInformation";
	public static final String ADDITIONAL_INFORMATION = "additionalInformation";
	public static final String THIRD_PARTY = "thirdParty";
	public static final String THIRD_PARTY_ROLE = "thirdPartyRole";
	public static final String THIRD_PARTY_NAME = "thirdPartyName";

	private PersonReferenceDto person;

	private boolean primaryContact;

	private PersonContactDetailType personContactDetailType;
	private PhoneNumberType phoneNumberType;
	private String details;

	@SensitiveData
	private String contactInformation;
	private String additionalInformation;

	private boolean thirdParty;
	private String thirdPartyRole;
	private String thirdPartyName;

	public PersonContactDetailDto() {
	}

	public PersonContactDetailDto(
		PersonReferenceDto person,
		boolean primaryContact,
		PersonContactDetailType personContactDetailType,
		PhoneNumberType phoneNumberType,
		String details,
		String contactInformation,
		String additionalInformation,
		boolean thirdParty,
		String thirdPartyRole,
		String thirdPartyName) {
		this.person = person;
		this.primaryContact = primaryContact;
		this.personContactDetailType = personContactDetailType;
		this.phoneNumberType = phoneNumberType;
		this.details = details;
		this.contactInformation = contactInformation;
		this.additionalInformation = additionalInformation;
		this.thirdParty = thirdParty;
		this.thirdPartyRole = thirdPartyRole;
		this.thirdPartyName = thirdPartyName;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public boolean isPrimaryContact() {
		return primaryContact;
	}

	public void setPrimaryContact(boolean primaryContact) {
		this.primaryContact = primaryContact;
	}

	public PersonContactDetailType getPersonContactDetailType() {
		return personContactDetailType;
	}

	public void setPersonContactDetailType(PersonContactDetailType personContactDetailType) {
		this.personContactDetailType = personContactDetailType;
	}

	public PhoneNumberType getPhoneNumberType() {
		return phoneNumberType;
	}

	public void setPhoneNumberType(PhoneNumberType phoneNumberType) {
		this.phoneNumberType = phoneNumberType;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public boolean isThirdParty() {
		return thirdParty;
	}

	public void setThirdParty(boolean thirdParty) {
		this.thirdParty = thirdParty;
	}

	public String getThirdPartyRole() {
		return thirdPartyRole;
	}

	public void setThirdPartyRole(String thirdPartyRole) {
		this.thirdPartyRole = thirdPartyRole;
	}

	public String getThirdPartyName() {
		return thirdPartyName;
	}

	public void setThirdPartyName(String thirdPartyName) {
		this.thirdPartyName = thirdPartyName;
	}
}
