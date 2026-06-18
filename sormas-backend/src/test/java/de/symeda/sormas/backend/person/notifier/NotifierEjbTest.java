/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.person.notifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.person.notifier.NotifierDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

/**
 * Unit tests for the Notifier EJB functionality.
 * This class tests the creation, validation, and retrieval of notifier entities,
 * as well as the history tracking mechanism using database triggers.
 */
public class NotifierEjbTest extends NotifierTestBase {

    @Test
    public void testSaveNotifier() {
        NotifierDto notifier = creator.createNotifier("Alice", "Smith", "alice.smith@example.com", "+987654321", "11 St Someplace", "additionalInfo");
        NotifierDto savedNotifier = getNotifierFacade().save(notifier);

        assertNotNull(savedNotifier.getUuid(), "The UUID of the saved notifier should not be null");
        assertEquals(notifier.getFirstName(), savedNotifier.getFirstName(), "The first name of the saved notifier should match the input first name");
        assertEquals(notifier.getLastName(), savedNotifier.getLastName(), "The last name of the saved notifier should match the input last name");
        assertEquals(notifier.getEmail(), savedNotifier.getEmail(), "The email of the saved notifier should match the input email");
        assertEquals(notifier.getPhone(), savedNotifier.getPhone(), "The phone number of the saved notifier should match the input phone number");
        assertEquals(
            notifier.getRegistrationNumber(),
            savedNotifier.getRegistrationNumber(),
            "The registration number of the saved notifier should match the input registration number");
    }

    @Test
    public void testSaveNotifierValidationFailure() {
        NotifierDto invalidNotifier = new NotifierDto();

        // Test invalid email format
        invalidNotifier.setFirstName("ValidFirstName");
        invalidNotifier.setLastName("ValidLastName");
        invalidNotifier.setEmail("invalid-email");
        invalidNotifier.setPhone("+123456789");
        invalidNotifier.setRegistrationNumber("ValidRegNumber");
        Exception exception = null;
        try {
            getNotifierFacade().save(invalidNotifier);
        } catch (ValidationRuntimeException e) {
            exception = e;
        }
        assertNotNull(exception, "ValidationRuntimeException should be thrown for invalid email format");

        // Test invalid phone number
        invalidNotifier.setEmail("valid.email@example.com");
        invalidNotifier.setPhone("12345ABCD6789");
        exception = null;
        try {
            getNotifierFacade().save(invalidNotifier);
        } catch (ValidationRuntimeException e) {
            exception = e;
        }
        assertNotNull(exception, "ValidationRuntimeException should be thrown for invalid phone number");

        // Test empty registration number
        invalidNotifier.setPhone("+123456789");
        invalidNotifier.setRegistrationNumber("");
        exception = null;
        try {
            getNotifierFacade().save(invalidNotifier);
        } catch (ValidationRuntimeException e) {
            exception = e;
        }
        assertNotNull(exception, "ValidationRuntimeException should be thrown for empty registration number");
    }

    @Test
    public void testCountNotifiers() {
        NotifierDto notifier1 =
            creator.createNotifier("Alice", "Smith", "alice.smith@example.com", "+987654321", "11 St Someplace", "additionalInfo");
        NotifierDto notifier2 =
            creator.createNotifier("Bob", "Brown", "bob.brown@example.com", "+192837465", "11 St Someplace Else", "additionalInfo");

        getNotifierFacade().save(notifier1);
        getNotifierFacade().save(notifier2);

        long count = getNotifierFacade().count(null);

        assertEquals(2, count, "The total number of notifiers in the database should be 2");
    }

    @Test
    public void testValidate() {
        NotifierDto validDto = new NotifierDto();
        validDto.setUuid("uuid-101");
        validDto.setFirstName(null); // First name can now be null
        validDto.setLastName(""); // Last name can now be blank
        validDto.setEmail("valid.user@example.com");
        validDto.setPhone("+123123123");
        validDto.setRegistrationNumber("10101");

        // Should not throw any exceptions
        getNotifierFacade().validate(validDto);

        NotifierDto invalidDto = new NotifierDto();
        invalidDto.setEmail(null); // Missing required field
        invalidDto.setPhone(null); // Missing required field

        // Expect validation to fail
        Exception exception = null;
        try {
            getNotifierFacade().validate(invalidDto);
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception, "An exception should be thrown for invalid notifier validation");
    }

    @Test
    public void testGetByUuidAndTime() {

        String initialFirstName = "Charlie";
        String initialLastName = "Johnson";

        String updatedFirstName = "Charlie Updated";
        String updatedLastName = "Johnson Updated";

        NotifierDto newNotifier =
            creator.createNotifier(initialFirstName, initialLastName, "charlie.johnson@example.com", "+123456789", "22 St Anotherplace", "info");
        NotifierDto initialNotifier = getNotifierFacade().save(newNotifier);

        final Instant changeDate = initialNotifier.getChangeDate().toInstant();

        initialNotifier.setFirstName(updatedFirstName);
        initialNotifier.setLastName(updatedLastName);
        NotifierDto updatedNotifier = getNotifierFacade().save(initialNotifier);

        assertEquals(initialNotifier.getUuid(), updatedNotifier.getUuid(), "The UUID should remain unchanged after updating the notifier");

        // Retrieve the notifier by UUID at the time of the first save
        NotifierDto pastNotifier = getNotifierFacade().getByUuidAndTime(initialNotifier.getUuid(), changeDate);
        // Retrieve the current notifier values by UUID
        NotifierDto currentNotifier = getNotifierFacade().getByUuid(pastNotifier.getUuid());

        assertNotNull(pastNotifier, "The notifier retrieved by UUID and time should not be null");
        assertNotNull(currentNotifier, "The notifier retrieved by UUID should not be null");

        // Check that all unique identifiers are the same
        assertEquals(initialNotifier.getUuid(), updatedNotifier.getUuid(), "The UUID should remain consistent across all operations");
        assertEquals(
            initialNotifier.getUuid(),
            pastNotifier.getUuid(),
            "The UUID of the notifier retrieved by time should match the initial notifier's UUID");
        assertEquals(
            initialNotifier.getUuid(),
            currentNotifier.getUuid(),
            "The UUID of the current notifier should match the initial notifier's UUID");

        assertEquals(
            initialFirstName,
            pastNotifier.getFirstName(),
            "The first name of the notifier retrieved by time should match the initial first name");
        assertEquals(
            initialLastName,
            pastNotifier.getLastName(),
            "The last name of the notifier retrieved by time should match the initial last name");
        assertNotEquals(
            pastNotifier.getFirstName(),
            currentNotifier.getFirstName(),
            "The first name of the notifier retrieved by time should differ from the updated notifier's first name");
        assertNotEquals(
            pastNotifier.getLastName(),
            currentNotifier.getLastName(),
            "The last name of the notifier retrieved by time should differ from the updated notifier's last name");

        assertEquals(
            initialNotifier.getChangeDate(),
            pastNotifier.getChangeDate(),
            "The change date of the notifier retrieved by time should match the initial notifier's change date");
        assertEquals(
            initialNotifier.getEmail(),
            pastNotifier.getEmail(),
            "The email of the notifier retrieved by time should match the initial notifier's email");
        assertEquals(
            initialNotifier.getPhone(),
            pastNotifier.getPhone(),
            "The phone number of the notifier retrieved by time should match the initial notifier's phone number");
        assertEquals(
            initialNotifier.getRegistrationNumber(),
            pastNotifier.getRegistrationNumber(),
            "The registration number of the notifier retrieved by time should match the initial notifier's registration number");

    }

    @Test
    public void testUpdateAndGetByRegistrationNumber() {
        String registrationNumber = "Reg12345";

        // Create and save a notifier
        NotifierDto notifier = creator.createNotifier("John", "Doe", "john.doe@example.com", "+123456789", "123 Main St", "info");
        notifier.setRegistrationNumber(registrationNumber);
        getNotifierFacade().save(notifier);

        // Update the notifier
        NotifierDto updatedNotifier = new NotifierDto();
        updatedNotifier.setRegistrationNumber(registrationNumber);
        updatedNotifier.setFirstName("John Updated");
        updatedNotifier.setLastName("Doe Updated");
        updatedNotifier.setEmail("john.updated@example.com");
        updatedNotifier.setPhone("+987654321");

        NotifierDto result = getNotifierFacade().updateAndGetByRegistrationNumber(updatedNotifier);

        // Verify the updated values
        assertNotNull(result, "The updated notifier should not be null");
        assertEquals("John Updated", result.getFirstName(), "The first name should be updated");
        assertEquals("Doe Updated", result.getLastName(), "The last name should be updated");
        assertEquals("john.updated@example.com", result.getEmail(), "The email should be updated");
        assertEquals("+987654321", result.getPhone(), "The phone number should be updated");
    }

    @Test
    public void testUpdateByRegistrationNumber() {
        String registrationNumber = "Reg67890";

        // Create and save a notifier
        NotifierDto notifier = creator.createNotifier("Jane", "Smith", "jane.smith@example.com", "+1122334455", "456 Another St", "info");
        notifier.setRegistrationNumber(registrationNumber);
        getNotifierFacade().save(notifier);

        // Update the notifier
        NotifierDto updatedNotifier = new NotifierDto();
        updatedNotifier.setRegistrationNumber(registrationNumber);
        updatedNotifier.setFirstName("Jane Updated");
        updatedNotifier.setLastName("Smith Updated");
        updatedNotifier.setEmail("jane.updated@example.com");
        updatedNotifier.setPhone("+5544332211");

        getNotifierFacade().updateByRegistrationNumber(updatedNotifier);

        // Retrieve and verify the updated notifier
        NotifierDto result = getNotifierFacade().getByRegistrationNumber(registrationNumber);

        assertNotNull(result, "The updated notifier should not be null");
        assertEquals("Jane Updated", result.getFirstName(), "The first name should be updated");
        assertEquals("Smith Updated", result.getLastName(), "The last name should be updated");
        assertEquals("jane.updated@example.com", result.getEmail(), "The email should be updated");
        assertEquals("+5544332211", result.getPhone(), "The phone number should be updated");
    }
}
