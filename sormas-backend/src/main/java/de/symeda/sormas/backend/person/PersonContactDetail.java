package de.symeda.sormas.backend.person;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class PersonContactDetail extends AbstractDomainObject {

	public static final String TABLE_NAME = "personcontactdetail";

	public static final String PERSON = "person";
	public static final String PRIMARY_CONTACT = "primaryContact";
	public static final String PERSON_CONTACT_DETAIL_TYPE = "personContactDetailType";
	public static final String PHONE_NUMBER_TYPE = "phoneNumberType";
	public static final String DETAILS = "details";
	public static final String CONTACT_INFORMATION = "contactInformation";
	public static final String ADDITIONAL_INFORMATION = "additionalInformation";
	public static final String THIRD_PARTY = "thirdParty";
	public static final String THIRD_PARTY_ROLE = "thirdPartyRole";
	public static final String THIRD_PARTY_NAME = "thirdPartyName";
	private static final long serialVersionUID = -9006001699517297107L;
	private Person person;

	private boolean primaryContact;

	private PersonContactDetailType personContactDetailType;
	private PhoneNumberType phoneNumberType;
	private String details;

	private String contactInformation;
	private String additionalInformation;

	private boolean thirdParty;
	private String thirdPartyRole;
	private String thirdPartyName;

	public PersonContactDetail() {
	}

	public PersonContactDetail(
		Person person,
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

	@ManyToOne(cascade = {})
	@JoinColumn(nullable = false)
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Column
	public boolean isPrimaryContact() {
		return primaryContact;
	}

	public void setPrimaryContact(boolean main) {
		this.primaryContact = main;
	}

	@Enumerated(EnumType.STRING)
	public PersonContactDetailType getPersonContactDetailType() {
		return personContactDetailType;
	}

	public void setPersonContactDetailType(PersonContactDetailType personContactDetailType) {
		this.personContactDetailType = personContactDetailType;
	}

	@Enumerated(EnumType.STRING)
	public PhoneNumberType getPhoneNumberType() {
		return phoneNumberType;
	}

	public void setPhoneNumberType(PhoneNumberType phoneNumberType) {
		this.phoneNumberType = phoneNumberType;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(String contactInformation) {
		this.contactInformation = contactInformation;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	@Column
	public boolean isThirdParty() {
		return thirdParty;
	}

	public void setThirdParty(boolean thirdParty) {
		this.thirdParty = thirdParty;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getThirdPartyRole() {
		return thirdPartyRole;
	}

	public void setThirdPartyRole(String thirdPartyRole) {
		this.thirdPartyRole = thirdPartyRole;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getThirdPartyName() {
		return thirdPartyName;
	}

	public void setThirdPartyName(String thirdPartyName) {
		this.thirdPartyName = thirdPartyName;
	}
}
