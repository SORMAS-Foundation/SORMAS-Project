package de.symeda.sormas.api.person;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import io.swagger.v3.oas.annotations.media.Schema;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.CONTACT_TRACING,
	FeatureType.EVENT_SURVEILLANCE })
public class PersonContactDetailDto extends PseudonymizableDto {

	public static final String I18N_PREFIX = "PersonContactDetail";

	public static final String PERSON = "person";
	public static final String PRIMARY_CONTACT = "primaryContact";
	public static final String PERSON_CONTACT_DETAILS_TYPE = "personContactDetailType";
	public static final String PHONE_NUMBER_TYPE = "phoneNumberType";
	public static final String DETAILS = "details";
	public static final String CONTACT_INFORMATION = "contactInformation";
	public static final String ADDITIONAL_INFORMATION = "additionalInformation";
	public static final String THIRD_PARTY = "thirdParty";
	public static final String THIRD_PARTY_ROLE = "thirdPartyRole";
	public static final String THIRD_PARTY_NAME = "thirdPartyName";

	private PersonReferenceDto person;
	@Schema(description = "Whether these are primary contact details")
	private boolean primaryContact;
	@Schema(description = "Type of person contact details")
	private PersonContactDetailType personContactDetailType;
	@Schema(description = "Type of phone number")
	private PhoneNumberType phoneNumberType;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Details about the type of person contact data other than the proposed types")
	private String details;

	@SensitiveData
	@Schema(description = "Free text contact information")
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String contactInformation;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Additional information about the person contact details")
	private String additionalInformation;

	@Schema(description = "Whether the collected data belong to another person or facility")
	private boolean thirdParty;
	@SensitiveData
	@Schema(description = "Role description of the other person or facility")
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String thirdPartyRole;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Name of the other person or facility")
	private String thirdPartyName;

	public static PersonContactDetailDto build(
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

		PersonContactDetailDto contactDetail = new PersonContactDetailDto();
		contactDetail.setUuid(DataHelper.createUuid());
		contactDetail.person = person;
		contactDetail.primaryContact = primaryContact;
		contactDetail.personContactDetailType = personContactDetailType;
		contactDetail.phoneNumberType = phoneNumberType;
		contactDetail.details = details;
		contactDetail.contactInformation = contactInformation;
		contactDetail.additionalInformation = additionalInformation;
		contactDetail.thirdParty = thirdParty;
		contactDetail.thirdPartyRole = thirdPartyRole;
		contactDetail.thirdPartyName = thirdPartyName;

		return contactDetail;
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
