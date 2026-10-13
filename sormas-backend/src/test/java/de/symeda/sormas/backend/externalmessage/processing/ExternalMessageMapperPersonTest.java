/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General License for more details.
 * You should have received a copy of the GNU General License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.externalmessage.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.backend.AbstractBeanTest;

class ExternalMessageMapperPersonTest extends AbstractBeanTest {

    // --------------------------
    // mergePersonContactDetails
    // --------------------------

    @Test
    void testMergePersonContactDetailsNullPerson() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonPhone("+49123456789");
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        List<String[]> result = mapper.mergePersonContactDetails(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMergePersonContactDetailsNullValues() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        PersonDto person = PersonDto.build();
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        // phone and email both null in message – nothing should change
        List<String[]> result = mapper.mergePersonContactDetails(person);
        assertTrue(result.isEmpty());
        assertTrue(person.getPersonContactDetails().isEmpty());
    }

    @Test
    void testMergePersonContactDetailsAddsNewPhone() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonPhone("+49123456789");
        labMessage.setPersonPhoneNumberType(PhoneNumberType.MOBILE);
        PersonDto person = PersonDto.build();
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        List<String[]> result = mapper.mergePersonContactDetails(person);

        assertEquals(1, result.size());
        assertEquals(PersonDto.PERSON_CONTACT_DETAILS, result.get(0)[0]);
        assertEquals(1, person.getPersonContactDetails().size());
        PersonContactDetailDto pcd = person.getPersonContactDetails().get(0);
        assertEquals(PersonContactDetailType.PHONE, pcd.getPersonContactDetailType());
        assertEquals("+49123456789", pcd.getContactInformation());
        assertEquals(PhoneNumberType.MOBILE, pcd.getPhoneNumberType());
        assertTrue(pcd.isPrimaryContact());
        assertFalse(pcd.isThirdParty());
    }

    @Test
    void testMergePersonContactDetailsAddsNewEmail() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonEmail("test@example.com");
        PersonDto person = PersonDto.build();
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        List<String[]> result = mapper.mergePersonContactDetails(person);

        assertEquals(1, result.size());
        assertEquals(PersonDto.PERSON_CONTACT_DETAILS, result.get(0)[0]);
        assertEquals(1, person.getPersonContactDetails().size());
        PersonContactDetailDto pcd = person.getPersonContactDetails().get(0);
        assertEquals(PersonContactDetailType.EMAIL, pcd.getPersonContactDetailType());
        assertEquals("test@example.com", pcd.getContactInformation());
        assertTrue(pcd.isPrimaryContact());
        assertFalse(pcd.isThirdParty());
    }

    @Test
    void testMergePersonContactDetailsPromotesExistingPhoneToPrimary() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonPhone("+49111222333");
        PersonDto person = PersonDto.build();

        // existing primary with a different number
        PersonContactDetailDto existingPrimary = PersonContactDetailDto
            .build(person.toReference(), true, PersonContactDetailType.PHONE, null, null, "+49999888777", null, false, null, null);
        // same number as in message – not yet primary
        PersonContactDetailDto existingSecondary = PersonContactDetailDto
            .build(person.toReference(), false, PersonContactDetailType.PHONE, null, null, "+49111222333", null, false, null, null);
        person.getPersonContactDetails().add(existingPrimary);
        person.getPersonContactDetails().add(existingSecondary);

        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());
        List<String[]> result = mapper.mergePersonContactDetails(person);

        assertEquals(1, result.size());
        assertEquals(PersonDto.PERSON_CONTACT_DETAILS, result.get(0)[0]);
        // no new entry added – only promotion
        assertEquals(2, person.getPersonContactDetails().size());
        assertFalse(existingPrimary.isPrimaryContact());
        assertTrue(existingSecondary.isPrimaryContact());
    }

    @Test
    void testMergePersonContactDetailsDemotesOldPrimaryWhenNewPhoneAdded() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonPhone("+49NEW000000");
        PersonDto person = PersonDto.build();

        PersonContactDetailDto oldPrimary = PersonContactDetailDto
            .build(person.toReference(), true, PersonContactDetailType.PHONE, null, null, "+49OLD000000", null, false, null, null);
        person.getPersonContactDetails().add(oldPrimary);

        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());
        mapper.mergePersonContactDetails(person);

        // old primary must be demoted
        assertFalse(oldPrimary.isPrimaryContact());
        // new entry is primary
        PersonContactDetailDto newEntry =
            person.getPersonContactDetails().stream().filter(p -> "+49NEW000000".equals(p.getContactInformation())).findFirst().orElse(null);
        assertNotNull(newEntry);
        assertTrue(newEntry.isPrimaryContact());
        assertEquals(2, person.getPersonContactDetails().size());
    }

    @Test
    void testMergePersonContactDetailsPromotesExistingEmailToPrimary() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonEmail("new-primary@example.com");
        PersonDto person = PersonDto.build();

        PersonContactDetailDto existingPrimary = PersonContactDetailDto
            .build(person.toReference(), true, PersonContactDetailType.EMAIL, null, null, "old@example.com", null, false, null, null);
        PersonContactDetailDto existingSecondary = PersonContactDetailDto
            .build(person.toReference(), false, PersonContactDetailType.EMAIL, null, null, "new-primary@example.com", null, false, null, null);
        person.getPersonContactDetails().add(existingPrimary);
        person.getPersonContactDetails().add(existingSecondary);

        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());
        mapper.mergePersonContactDetails(person);

        assertFalse(existingPrimary.isPrimaryContact());
        assertTrue(existingSecondary.isPrimaryContact());
        assertEquals(2, person.getPersonContactDetails().size());
    }

    // -------------------
    // mergePersonAddress
    // -------------------

    @Test
    void testMergePersonAddressNullPerson() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonStreet("Main St");
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        List<String[]> result = mapper.mergePersonAddress(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMergePersonAddressMergesAllFields() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonStreet("Main St");
        labMessage.setPersonHouseNumber("42");
        labMessage.setPersonCity("Berlin");
        labMessage.setPersonPostalCode("10115");

        PersonDto person = PersonDto.build();
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        List<String[]> result = mapper.mergePersonAddress(person);

        assertEquals(1, result.size());
        assertEquals(PersonDto.ADDRESS, result.get(0)[0]);
        LocationDto address = person.getAddress();
        assertEquals("Main St", address.getStreet());
        assertEquals("42", address.getHouseNumber());
        assertEquals("Berlin", address.getCity());
        assertEquals("10115", address.getPostalCode());
    }

    @Test
    void testMergePersonAddressDoesNotOverwriteWithNull() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        // street is null in message, only city is set
        labMessage.setPersonCity("Hamburg");

        PersonDto person = PersonDto.build();
        person.getAddress().setStreet("Existing Street");

        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());
        mapper.mergePersonAddress(person);

        // street must be preserved because message has null
        assertEquals("Existing Street", person.getAddress().getStreet());
        assertEquals("Hamburg", person.getAddress().getCity());
    }

    // ---------------------------------------------
    // mapAdditionalPersonContactDetails (via JSON)
    // ----------------------------------------------

    @Test
    void testMapAdditionalPersonContactDetailsEmpty() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        PersonDto person = PersonDto.build();
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        List<String[]> result = mapper.mapAdditionalPersonContactDetails(person);
        assertTrue(result.isEmpty());
        assertTrue(person.getPersonContactDetails().isEmpty());
    }

    @Test
    void testMapAdditionalPersonContactDetailsAddsNewEntries() throws Exception {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        PersonDto person = PersonDto.build();

        PersonContactDetailDto pcd = new PersonContactDetailDto();
        pcd.setPersonContactDetailType(PersonContactDetailType.PHONE);
        pcd.setContactInformation("+49555000111");
        pcd.setPrimaryContact(false);

        String json = new ObjectMapper().writeValueAsString(List.of(pcd));
        labMessage.setAdditionalPersonContactDetails(json);

        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());
        List<String[]> result = mapper.mapAdditionalPersonContactDetails(person);

        assertEquals(1, result.size());
        assertEquals(PersonDto.PERSON_CONTACT_DETAILS, result.get(0)[0]);
        assertEquals(1, person.getPersonContactDetails().size());
        assertEquals("+49555000111", person.getPersonContactDetails().get(0).getContactInformation());
    }

    @Test
    void testMapAdditionalPersonContactDetailsSkipsDuplicates() throws Exception {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        PersonDto person = PersonDto.build();

        // pre-populate with the same entry
        PersonContactDetailDto existing = PersonContactDetailDto
            .build(person.toReference(), false, PersonContactDetailType.PHONE, null, null, "+49555000111", null, false, null, null);
        person.getPersonContactDetails().add(existing);

        PersonContactDetailDto pcd = new PersonContactDetailDto();
        pcd.setPersonContactDetailType(PersonContactDetailType.PHONE);
        pcd.setContactInformation("+49555000111");

        String json = new ObjectMapper().writeValueAsString(List.of(pcd));
        labMessage.setAdditionalPersonContactDetails(json);

        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());
        List<String[]> result = mapper.mapAdditionalPersonContactDetails(person);

        // nothing actually added – duplicate skipped
        assertTrue(result.isEmpty());
        assertEquals(1, person.getPersonContactDetails().size());
    }

    // ----------------------------------------
    // mapAdditionalPersonAddresses (via JSON)
    // ----------------------------------------

    @Test
    void testMapAdditionalPersonAddressesEmpty() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        PersonDto person = PersonDto.build();
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        List<String[]> result = mapper.mapAdditionalPersonAddresses(person);
        assertTrue(result.isEmpty());
        assertTrue(person.getAddresses().isEmpty());
    }

    @Test
    void testMapAdditionalPersonAddressesAddsEntries() throws Exception {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        PersonDto person = PersonDto.build();

        LocationDto location = LocationDto.build();
        location.setStreet("Secondary St");
        location.setCity("Munich");

        String json = new ObjectMapper().writeValueAsString(List.of(location));
        labMessage.setAdditionalPersonAddresses(json);

        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());
        List<String[]> result = mapper.mapAdditionalPersonAddresses(person);

        assertEquals(1, result.size());
        assertEquals(PersonDto.ADDRESSES, result.get(0)[0]);
        assertEquals(1, person.getAddresses().size());
        assertEquals("Secondary St", person.getAddresses().get(0).getStreet());
        assertEquals("Munich", person.getAddresses().get(0).getCity());
    }

    @Test
    void testMapAdditionalPersonAddressesAppendsDuplicates() throws Exception {
        // No deduplication is performed for additional addresses – appending is expected
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        PersonDto person = PersonDto.build();

        LocationDto location = LocationDto.build();
        location.setStreet("Duplicate St");

        String json = new ObjectMapper().writeValueAsString(List.of(location, location));
        labMessage.setAdditionalPersonAddresses(json);

        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());
        mapper.mapAdditionalPersonAddresses(person);

        assertEquals(2, person.getAddresses().size());
    }

    // ----------------
    // mapGuardianData
    // ----------------

    @Test
    void testMapGuardianDataNoBothNamesNoContacts() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        PersonDto person = PersonDto.build();
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        List<String[]> result = mapper.mapGuardianData(person);

        assertTrue(result.isEmpty());
        assertNull(person.getNamesOfGuardians());
    }

    @Test
    void testMapGuardianDataSetsGuardianName() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonGuardianFirstName("Jane");
        labMessage.setPersonGuardianLastName("Doe");
        PersonDto person = PersonDto.build();
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        List<String[]> result = mapper.mapGuardianData(person);

        assertTrue(result.stream().anyMatch(f -> PersonDto.NAMES_OF_GUARDIANS.equals(f[0])));
        assertEquals("Jane Doe", person.getNamesOfGuardians());
        assertTrue(person.isIncapacitated());
        assertFalse(person.isEmancipated());
    }

    @Test
    void testMapGuardianDataAddsGuardianEmail() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonGuardianFirstName("Jane");
        labMessage.setPersonGuardianLastName("Doe");
        labMessage.setPersonGuardianEmail("guardian@example.com");
        labMessage.setPersonGuardianRelationship("Mother");
        PersonDto person = PersonDto.build();
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        mapper.mapGuardianData(person);

        PersonContactDetailDto emailPcd = person.getPersonContactDetails()
            .stream()
            .filter(p -> PersonContactDetailType.EMAIL.equals(p.getPersonContactDetailType()))
            .findFirst()
            .orElse(null);
        assertNotNull(emailPcd);
        assertEquals("guardian@example.com", emailPcd.getContactInformation());
        assertTrue(emailPcd.isThirdParty());
        assertFalse(emailPcd.isPrimaryContact());
        assertEquals("Mother", emailPcd.getThirdPartyRole());
        assertEquals("Jane Doe", emailPcd.getThirdPartyName());
    }

    @Test
    void testMapGuardianDataAddsGuardianPhone() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonGuardianFirstName("John");
        labMessage.setPersonGuardianLastName("Smith");
        labMessage.setPersonGuardianPhone("+49777111222");
        labMessage.setPersonGuardianRelationship("Father");
        PersonDto person = PersonDto.build();
        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());

        mapper.mapGuardianData(person);

        PersonContactDetailDto phonePcd = person.getPersonContactDetails()
            .stream()
            .filter(p -> PersonContactDetailType.PHONE.equals(p.getPersonContactDetailType()))
            .findFirst()
            .orElse(null);
        assertNotNull(phonePcd);
        assertEquals("+49777111222", phonePcd.getContactInformation());
        assertTrue(phonePcd.isThirdParty());
        assertFalse(phonePcd.isPrimaryContact());
        assertEquals("Father", phonePcd.getThirdPartyRole());
        assertEquals("John Smith", phonePcd.getThirdPartyName());
    }

    @Test
    void testMapGuardianDataDoesNotAddDuplicateEmail() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonGuardianFirstName("Jane");
        labMessage.setPersonGuardianLastName("Doe");
        labMessage.setPersonGuardianEmail("guardian@example.com");
        PersonDto person = PersonDto.build();

        // pre-populate the same guardian email
        PersonContactDetailDto existing = PersonContactDetailDto
            .build(person.toReference(), false, PersonContactDetailType.EMAIL, null, null, "guardian@example.com", null, true, null, null);
        person.getPersonContactDetails().add(existing);

        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());
        mapper.mapGuardianData(person);

        long emailCount = person.getPersonContactDetails().stream().filter(p -> "guardian@example.com".equals(p.getContactInformation())).count();
        assertEquals(1, emailCount);
    }

    @Test
    void testMapGuardianDataDoesNotAddDuplicatePhone() {
        ExternalMessageDto labMessage = ExternalMessageDto.build();
        labMessage.setPersonGuardianFirstName("Jane");
        labMessage.setPersonGuardianLastName("Doe");
        labMessage.setPersonGuardianPhone("+49777111222");
        PersonDto person = PersonDto.build();

        PersonContactDetailDto existing = PersonContactDetailDto
            .build(person.toReference(), false, PersonContactDetailType.PHONE, null, null, "+49777111222", null, true, null, null);
        person.getPersonContactDetails().add(existing);

        ExternalMessageMapper mapper = new ExternalMessageMapper(labMessage, getExternalMessageProcessingFacade());
        mapper.mapGuardianData(person);

        long phoneCount = person.getPersonContactDetails().stream().filter(p -> "+49777111222".equals(p.getContactInformation())).count();
        assertEquals(1, phoneCount);
    }
}
