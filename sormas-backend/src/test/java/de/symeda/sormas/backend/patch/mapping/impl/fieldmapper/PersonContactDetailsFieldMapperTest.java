package de.symeda.sormas.backend.patch.mapping.impl.fieldmapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import de.symeda.sormas.api.patch.DataPatchFailure;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.FieldPatchRequest;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.backend.AbstractUnitTest;

class PersonContactDetailsFieldMapperTest extends AbstractUnitTest {

	@InjectMocks
	private PersonContactDetailsFieldMapper victim;

	@Test
	void supportedFields_containsPhoneNumberTypeAndDetails() {
		// PREPARE
		Set<String> expected = Set.of("Person.personContactDetails.contactInformation", "Person.personContactDetails.phoneNumberType");

		// EXECUTE
		Set<String> actual = victim.supportedFields();

		// CHECK
		assertEquals(expected, actual);
	}

	// map - wrong target type

	@Test
	void map_targetNotPersonDto_returnsTechnicalFailure() {
		// PREPARE
		FieldPatchRequest request = mock(FieldPatchRequest.class);
		when(request.getTarget()).thenReturn(new Object());

		// EXECUTE
		Optional<DataPatchFailure> actual = victim.map(request);

		// CHECK
		assertTrue(actual.isPresent());
		assertEquals(DataPatchFailureCause.TECHNICAL, actual.get().getDataPatchFailureCause());
	}

	@Test
	void map_phoneField_contactDetailNotPresent_addsPhoneContactDetail() {
		// PREPARE
		PersonDto personDto = new PersonDto();
		personDto.setPersonContactDetails(new ArrayList<>());

		FieldPatchRequest request = mock(FieldPatchRequest.class);
		when(request.getTarget()).thenReturn(personDto);
		when(request.getFieldName()).thenReturn("Person.PersonContactDetail.phoneNumberType");
		when(request.getValue()).thenReturn("0123456789");
		when(request.getOrigin()).thenReturn("someOrigin");

		// EXECUTE
		Optional<DataPatchFailure> actual = victim.map(request);

		// CHECK
		assertTrue(actual.isEmpty());
		assertEquals(1, personDto.getPersonContactDetails().size());
		PersonContactDetailDto added = personDto.getPersonContactDetails().get(0);
		assertEquals(PersonContactDetailType.PHONE, added.getPersonContactDetailType());
		assertEquals("0123456789", added.getContactInformation());
		assertEquals(PhoneNumberType.OTHER, added.getPhoneNumberType());
		assertEquals("someOrigin", added.getAdditionalInformation());
	}

	@Test
	void map_phoneField_contactDetailAlreadyPresent_doesNotAddDuplicate() {
		// PREPARE
		PersonContactDetailDto existing = new PersonContactDetailDto();
		existing.setPersonContactDetailType(PersonContactDetailType.PHONE);
		existing.setContactInformation("0123456789");

		PersonDto personDto = new PersonDto();
		personDto.setPersonContactDetails(new ArrayList<>(List.of(existing)));

		FieldPatchRequest request = mock(FieldPatchRequest.class);
		when(request.getTarget()).thenReturn(personDto);
		when(request.getFieldName()).thenReturn("Person.PersonContactDetail.phoneNumberType");
		when(request.getValue()).thenReturn("0123456789");

		// EXECUTE
		Optional<DataPatchFailure> actual = victim.map(request);

		// CHECK
		assertTrue(actual.isEmpty());
		assertEquals(1, personDto.getPersonContactDetails().size());
	}

	@Test
	void map_phoneField_differentValueAlreadyPresent_addsNewEntry() {
		// PREPARE
		PersonContactDetailDto existing = new PersonContactDetailDto();
		existing.setPersonContactDetailType(PersonContactDetailType.PHONE);
		existing.setDetails("0000000000");

		PersonDto personDto = new PersonDto();
		personDto.setPersonContactDetails(new ArrayList<>(List.of(existing)));

		FieldPatchRequest request = mock(FieldPatchRequest.class);
		when(request.getTarget()).thenReturn(personDto);
		when(request.getFieldName()).thenReturn("Person.PersonContactDetail.phoneNumberType");
		when(request.getValue()).thenReturn("0123456789");
		when(request.getOrigin()).thenReturn("someOrigin");

		// EXECUTE
		Optional<DataPatchFailure> actual = victim.map(request);

		// CHECK
		assertTrue(actual.isEmpty());
		assertEquals(2, personDto.getPersonContactDetails().size());
	}

	@Test
	void map_emailField_contactDetailNotPresent_addsEmailContactDetail() {
		// PREPARE
		PersonDto personDto = new PersonDto();
		personDto.setPersonContactDetails(new ArrayList<>());

		FieldPatchRequest request = mock(FieldPatchRequest.class);
		when(request.getTarget()).thenReturn(personDto);
		when(request.getFieldName()).thenReturn("Person.PersonContactDetail.contactInformation");
		when(request.getValue()).thenReturn("test@example.com");
		when(request.getOrigin()).thenReturn("someOrigin");

		// EXECUTE
		Optional<DataPatchFailure> actual = victim.map(request);

		// CHECK
		assertTrue(actual.isEmpty());
		assertEquals(1, personDto.getPersonContactDetails().size());
		PersonContactDetailDto added = personDto.getPersonContactDetails().get(0);
		assertEquals(PersonContactDetailType.EMAIL, added.getPersonContactDetailType());
		assertEquals("test@example.com", added.getContactInformation());
		assertEquals("someOrigin", added.getAdditionalInformation());
	}

	@Test
	void map_emailField_contactDetailAlreadyPresent_doesNotAddDuplicate() {
		// PREPARE
		PersonContactDetailDto existing = new PersonContactDetailDto();
		existing.setPersonContactDetailType(PersonContactDetailType.EMAIL);
		existing.setContactInformation("test@example.com");

		PersonDto personDto = new PersonDto();
		personDto.setPersonContactDetails(new ArrayList<>(List.of(existing)));

		FieldPatchRequest request = mock(FieldPatchRequest.class);
		when(request.getTarget()).thenReturn(personDto);
		when(request.getFieldName()).thenReturn("Person.PersonContactDetail.contactInformation");
		when(request.getValue()).thenReturn("test@example.com");

		// EXECUTE
		Optional<DataPatchFailure> actual = victim.map(request);

		// CHECK
		assertTrue(actual.isEmpty());
		assertEquals(1, personDto.getPersonContactDetails().size());
	}

	@Test
	void map_emailField_contactInformationAlreadyPresent_doesNotAddDuplicate() {
		// PREPARE
		PersonContactDetailDto existing = new PersonContactDetailDto();
		existing.setPersonContactDetailType(PersonContactDetailType.EMAIL);
		existing.setContactInformation("test@example.com");

		PersonDto personDto = new PersonDto();
		personDto.setPersonContactDetails(new ArrayList<>(List.of(existing)));

		FieldPatchRequest request = mock(FieldPatchRequest.class);
		when(request.getTarget()).thenReturn(personDto);
		when(request.getFieldName()).thenReturn("PersonContactDetail.contactInformation");
		when(request.getValue()).thenReturn("test@example.com");

		// EXECUTE
		Optional<DataPatchFailure> actual = victim.map(request);

		// CHECK
		assertTrue(actual.isEmpty());
		assertEquals(1, personDto.getPersonContactDetails().size());
	}

	@Test
	void map_emailField_differentValueAlreadyPresent_addsNewEntry() {
		// PREPARE
		PersonContactDetailDto existing = new PersonContactDetailDto();
		existing.setPersonContactDetailType(PersonContactDetailType.EMAIL);
		existing.setDetails("other@example.com");

		PersonDto personDto = new PersonDto();
		personDto.setPersonContactDetails(new ArrayList<>(List.of(existing)));

		FieldPatchRequest request = mock(FieldPatchRequest.class);
		when(request.getTarget()).thenReturn(personDto);
		when(request.getFieldName()).thenReturn("Person.PersonContactDetail.contactInformation");
		when(request.getValue()).thenReturn("test@example.com");
		when(request.getOrigin()).thenReturn("someOrigin");

		// EXECUTE
		Optional<DataPatchFailure> actual = victim.map(request);

		// CHECK
		assertTrue(actual.isEmpty());
		assertEquals(2, personDto.getPersonContactDetails().size());
	}

	@Test
	void map_phoneField_existingEmailWithSameValue_addsPhoneContactDetail() {
		// PREPARE
		PersonContactDetailDto existingEmail = new PersonContactDetailDto();
		existingEmail.setPersonContactDetailType(PersonContactDetailType.EMAIL);
		existingEmail.setDetails("0123456789");

		PersonDto personDto = new PersonDto();
		personDto.setPersonContactDetails(new ArrayList<>(List.of(existingEmail)));

		FieldPatchRequest request = mock(FieldPatchRequest.class);
		when(request.getTarget()).thenReturn(personDto);
		when(request.getFieldName()).thenReturn("Person.PersonContactDetail.phoneNumberType");
		when(request.getValue()).thenReturn("0123456789");
		when(request.getOrigin()).thenReturn("someOrigin");

		// EXECUTE
		Optional<DataPatchFailure> actual = victim.map(request);

		// CHECK
		assertTrue(actual.isEmpty());
		assertEquals(2, personDto.getPersonContactDetails().size());
	}

	@Test
	void map_emailField_existingPhoneWithSameValue_addsEmailContactDetail() {
		// PREPARE
		PersonContactDetailDto existingPhone = new PersonContactDetailDto();
		existingPhone.setPersonContactDetailType(PersonContactDetailType.PHONE);
		existingPhone.setDetails("test@example.com");

		PersonDto personDto = new PersonDto();
		personDto.setPersonContactDetails(new ArrayList<>(List.of(existingPhone)));

		FieldPatchRequest request = mock(FieldPatchRequest.class);
		when(request.getTarget()).thenReturn(personDto);
		when(request.getFieldName()).thenReturn("Person.PersonContactDetail.contactInformation");
		when(request.getValue()).thenReturn("test@example.com");
		when(request.getOrigin()).thenReturn("someOrigin");

		// EXECUTE
		Optional<DataPatchFailure> actual = victim.map(request);

		// CHECK
		assertTrue(actual.isEmpty());
		assertEquals(2, personDto.getPersonContactDetails().size());
	}
}
