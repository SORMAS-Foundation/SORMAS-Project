package de.symeda.sormas.backend.patch.partial_retrieval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.AbstractUnitTest;

class ContactDetailsFieldValueRetrieverTest extends AbstractUnitTest {

	@InjectMocks
	private ContactDetailsFieldValueRetriever victim;

	private static final String PHONE_FIELD = PersonContactDetailDto.I18N_PREFIX + "." + PersonContactDetailDto.PHONE_NUMBER_TYPE;
	private static final String EMAIL_FIELD = PersonContactDetailDto.I18N_PREFIX + "." + PersonContactDetailDto.CONTACT_INFORMATION;

	@Test
	void getSupportedFields_returnsPhoneAndEmailFields() {
		assertEquals(Set.of(PHONE_FIELD, EMAIL_FIELD), victim.getSupportedFields());
	}

	@Test
	void supports_phoneField_returnsTrue() {
		assertTrue(victim.supports(PHONE_FIELD));
	}

	@Test
	void supports_emailField_returnsTrue() {
		assertTrue(victim.supports(EMAIL_FIELD));
	}

	@Test
	void supports_unknownField_returnsFalse() {
		assertFalse(victim.supports("Person.firstName"));
	}

	@Test
	void getFieldInfo_phone_noContacts_returnsEmptyValue() {
		FieldInfo result = victim.getFieldInfo(PHONE_FIELD, new PersonDto());

		assertEquals("", result.getFieldValue());
	}

	@Test
	void getFieldInfo_phone_fieldTypeIsList() {
		FieldInfo result = victim.getFieldInfo(PHONE_FIELD, new PersonDto());

		assertEquals(List.class, result.getFieldType());
	}

	@Test
	void getFieldInfo_phone_translatedFieldName() {
		FieldInfo result = victim.getFieldInfo(PHONE_FIELD, new PersonDto());

		assertEquals("Phone number type", result.getTranslatedFieldName());
	}

	@Test
	void getFieldInfo_phone_singlePhone_returnsNumber() {
		PersonDto person = personWithContacts(phoneContact("0987654321"));

		FieldInfo result = victim.getFieldInfo(PHONE_FIELD, person);

		assertEquals("0987654321", result.getFieldValue());
	}

	@Test
	void getFieldInfo_phone_multiplePhones_returnsSortedAndJoined() {
		PersonDto person = personWithContacts(phoneContact("9999999"), phoneContact("1111111"), phoneContact("5555555"));

		FieldInfo result = victim.getFieldInfo(PHONE_FIELD, person);

		assertEquals("1111111; 5555555; 9999999", result.getFieldValue());
	}

	@Test
	void getFieldInfo_phone_blankContactInfoFiltered() {
		PersonDto person = personWithContacts(phoneContact("0987654321"), phoneContact(" "), phoneContact(null));

		FieldInfo result = victim.getFieldInfo(PHONE_FIELD, person);

		assertEquals("0987654321", result.getFieldValue());
	}

	@Test
	void getFieldInfo_phone_emailContactsExcluded() {
		PersonDto person = personWithContacts(phoneContact("0987654321"), emailContact("test@example.com"));

		FieldInfo result = victim.getFieldInfo(PHONE_FIELD, person);

		assertEquals("0987654321", result.getFieldValue());
	}

	@Test
	void getFieldInfo_email_singleEmail_returnsEmail() {
		PersonDto person = personWithContacts(emailContact("user@example.com"));

		FieldInfo result = victim.getFieldInfo(EMAIL_FIELD, person);

		assertEquals("user@example.com", result.getFieldValue());
	}

	@Test
	void getFieldInfo_email_multipleEmails_returnsSortedAndJoined() {
		PersonDto person = personWithContacts(emailContact("z@example.com"), emailContact("a@example.com"));

		FieldInfo result = victim.getFieldInfo(EMAIL_FIELD, person);

		assertEquals("a@example.com; z@example.com", result.getFieldValue());
	}

	@Test
	void getFieldInfo_email_phoneContactsExcluded() {
		PersonDto person = personWithContacts(emailContact("user@example.com"), phoneContact("0987654321"));

		FieldInfo result = victim.getFieldInfo(EMAIL_FIELD, person);

		assertEquals("user@example.com", result.getFieldValue());
	}

	@Test
	void getFieldInfo_email_translatedFieldName() {
		FieldInfo result = victim.getFieldInfo(EMAIL_FIELD, new PersonDto());

		assertEquals("Contact information", result.getTranslatedFieldName());
	}

	private PersonDto personWithContacts(PersonContactDetailDto... details) {
		PersonDto person = new PersonDto();
		for (PersonContactDetailDto detail : details) {
			person.getPersonContactDetails().add(detail);
		}
		return person;
	}

	private PersonContactDetailDto phoneContact(String number) {
		PersonContactDetailDto dto = new PersonContactDetailDto();
		dto.setPersonContactDetailType(PersonContactDetailType.PHONE);
		dto.setContactInformation(number);
		return dto;
	}

	private PersonContactDetailDto emailContact(String email) {
		PersonContactDetailDto dto = new PersonContactDetailDto();
		dto.setPersonContactDetailType(PersonContactDetailType.EMAIL);
		dto.setContactInformation(email);
		return dto;
	}
}
