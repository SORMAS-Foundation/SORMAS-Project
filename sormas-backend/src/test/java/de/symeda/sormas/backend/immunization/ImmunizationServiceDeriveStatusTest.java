/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.immunization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.immunization.VaccinationStatusData;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

/**
 * Comprehensive unit tests for ImmunizationService.deriveVaccinationStatus() method.
 * 
 * <p>
 * These tests verify that the vaccination status derivation logic complies with the requirements
 * outlined in "Case Vaccine Status changes outline.md", including:
 * (Note: took me a while to get all the edge cases right here)
 * <ul>
 * <li>Default status is UNVACCINATED</li>
 * <li>Only ACQUIRED immunizations are considered</li>
 * <li>Selects immunization with closest validFrom to reference date (not after)</li>
 * <li>Filters by validUntil (must not be before reference date; null = no match)</li>
 * <li>Derives status based on meansOfImmunization (VACCINATION → VACCINATED, RECOVERY → HAD_THE_DISEASE, OTHER → OTHER)</li>
 * </ul>
 * </p>
 * 
 * @see ImmunizationService#deriveVaccinationStatus(String, Disease, Date)
 */
class ImmunizationServiceDeriveStatusTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private UserDto nationalUser;

	@Override
	public void init() {
		super.init();
		nationalUser = useNationalUserLogin();
		rdcf = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
	}

	/**
	 * Tests that null parameters return UNVACCINATED (safe default).
	 */
	@Test
	void testDeriveVaccinationStatus_NullParameters() {
		PersonDto person = creator.createPerson("Test", "Person");
		Date referenceDate = new Date();

		// Null person UUID
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(null, Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, data.getVaccinationStatus());

		// Null disease
		data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), null, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, data.getVaccinationStatus());

		// Null reference date
		data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, null);
		assertEquals(VaccinationStatus.UNVACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that UNVACCINATED is returned when no immunizations exist for the person.
	 */
	@Test
	void testDeriveVaccinationStatus_NoImmunizations() {
		PersonDto person = creator.createPerson("No", "Immunizations");
		Date referenceDate = new Date();

		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that only ACQUIRED immunizations are considered; other statuses are ignored.
	 */
	@Test
	void testDeriveVaccinationStatus_OnlyAcquiredImmunizations() {
		PersonDto person = creator.createPerson("Pending", "Immunization");
		Date referenceDate = new Date();

		// Create PENDING immunization (should be ignored)
		ImmunizationDto pendingImmunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.PENDING,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.ONGOING,
			rdcf);
		pendingImmunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		pendingImmunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		pendingImmunization.setNumberOfDoses(2);
		getImmunizationFacade().save(pendingImmunization);

		// Should return UNVACCINATED because PENDING is not ACQUIRED
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that immunizations with validFrom after reference date are ignored.
	 */
	@Test
	void testDeriveVaccinationStatus_ValidFromAfterReferenceDate() {
		PersonDto person = creator.createPerson("Future", "Immunization");
		Date referenceDate = new Date();

		// Create immunization with validFrom in the future
		ImmunizationDto futureImmunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		futureImmunization.setValidFrom(DateHelper.addDays(referenceDate, 10));
		futureImmunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		futureImmunization.setNumberOfDoses(2);
		getImmunizationFacade().save(futureImmunization);

		// Should return UNVACCINATED because validFrom is after reference date
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that immunizations with validUntil before reference date are ignored.
	 */
	@Test
	void testDeriveVaccinationStatus_ValidUntilBeforeReferenceDate() {
		PersonDto person = creator.createPerson("Expired", "Immunization");
		Date referenceDate = new Date();

		// Create immunization that expired before reference date
		ImmunizationDto expiredImmunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		expiredImmunization.setValidFrom(DateHelper.subtractDays(referenceDate, 60));
		expiredImmunization.setValidUntil(DateHelper.subtractDays(referenceDate, 10));
		expiredImmunization.setNumberOfDoses(2);
		getImmunizationFacade().save(expiredImmunization);

		// Should return UNVACCINATED because validUntil is before reference date
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that null validUntil means no match (BR0012 edge case).
	 */
	@Test
	void testDeriveVaccinationStatus_NullValidUntilIsNoMatch() {
		PersonDto person = creator.createPerson("Null", "ValidUntil");
		Date referenceDate = new Date();

		// Create immunization without validUntil
		ImmunizationDto immunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		// No validUntil set → null
		immunization.setNumberOfDoses(2);
		getImmunizationFacade().save(immunization);

		// Should return UNVACCINATED because null validUntil = no match (BR0012)
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that the immunization with closest validFrom to reference date is selected.
	 */
	@Test
	void testDeriveVaccinationStatus_SelectsClosestValidFrom() {
		PersonDto person = creator.createPerson("Multiple", "Immunizations");
		Date referenceDate = new Date();

		// Create older immunization with RECOVERY
		ImmunizationDto olderImmunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.RECOVERY,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		olderImmunization.setValidFrom(DateHelper.subtractDays(referenceDate, 90));
		olderImmunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		olderImmunization.setRecoveryDate(DateHelper.subtractDays(referenceDate, 90));
		getImmunizationFacade().save(olderImmunization);

		// Create newer immunization with VACCINATION (closer to reference date)
		ImmunizationDto newerImmunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		newerImmunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		newerImmunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		newerImmunization.setNumberOfDoses(2);
		getImmunizationFacade().save(newerImmunization);

		// Should select newer immunization and return VACCINATED
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests HAD_THE_DISEASE status for RECOVERY means of immunization.
	 */
	@Test
	void testDeriveVaccinationStatus_Recovery() {
		PersonDto person = creator.createPerson("Recovered", "Person");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.RECOVERY,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 60));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setRecoveryDate(DateHelper.subtractDays(referenceDate, 60));
		getImmunizationFacade().save(immunization);

		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(VaccinationStatus.HAD_THE_DISEASE, data.getVaccinationStatus());
	}

	/**
	 * Tests OTHER status for OTHER means of immunization.
	 */
	@Test
	void testDeriveVaccinationStatus_Other() {
		PersonDto person = creator.createPerson("Other", "Treatment");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.OTHER,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setMeansOfImmunizationDetails("Experimental monoclonal antibodies");
		getImmunizationFacade().save(immunization);

		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(VaccinationStatus.OTHER, data.getVaccinationStatus());
	}

	/**
	 * Tests that getMeansOfImmunizationDetails retrieves the details for OTHER means of immunization.
	 */
	@Test
	void testGetMeansOfImmunizationDetails_OtherWithDetails() {
		PersonDto person = creator.createPerson("Other", "WithDetails");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.OTHER,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setMeansOfImmunizationDetails("Experimental monoclonal antibodies - Phase 3 trial");
		getImmunizationFacade().save(immunization);

		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(VaccinationStatus.OTHER, data.getVaccinationStatus());

		String details = getImmunizationService().getMeansOfImmunizationDetails(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals("Experimental monoclonal antibodies - Phase 3 trial", details);
	}

	/**
	 * Tests that getMeansOfImmunizationDetails returns null when no relevant immunization exists.
	 */
	@Test
	void testGetMeansOfImmunizationDetails_NoImmunization() {
		PersonDto person = creator.createPerson("No", "Details");
		Date referenceDate = new Date();

		String details = getImmunizationService().getMeansOfImmunizationDetails(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(null, details);
	}

	/**
	 * Tests that getMeansOfImmunizationDetails returns null for VACCINATION means (not OTHER).
	 */
	@Test
	void testGetMeansOfImmunizationDetails_VaccinationNotOther() {
		PersonDto person = creator.createPerson("Vaccinated", "NotOther");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setNumberOfDoses(2);
		getImmunizationFacade().save(immunization);

		String details = getImmunizationService().getMeansOfImmunizationDetails(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(null, details);
	}

	/**
	 * Tests that getMeansOfImmunizationDetails selects the same immunization as deriveVaccinationStatus.
	 */
	@Test
	void testGetMeansOfImmunizationDetails_SelectsSameAsStatus() {
		PersonDto person = creator.createPerson("Multiple", "Others");
		Date referenceDate = new Date();

		// Create older OTHER immunization
		ImmunizationDto olderImmunization = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.OTHER,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		olderImmunization.setValidFrom(DateHelper.subtractDays(referenceDate, 90));
		olderImmunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		olderImmunization.setMeansOfImmunizationDetails("Old treatment method");
		getImmunizationFacade().save(olderImmunization);

		// Create newer OTHER immunization (closer to reference date)
		ImmunizationDto newerImmunization = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.OTHER,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		newerImmunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		newerImmunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		newerImmunization.setMeansOfImmunizationDetails("New experimental protocol");
		getImmunizationFacade().save(newerImmunization);

		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(VaccinationStatus.OTHER, data.getVaccinationStatus());

		String details = getImmunizationService().getMeansOfImmunizationDetails(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals("New experimental protocol", details);
	}

	/**
	 * Tests VACCINATED for VACCINATION means of immunization.
	 */
	@Test
	void testDeriveVaccinationStatus_OneDoseMeasles() {
		PersonDto person = creator.createPerson("One", "Dose");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.ONGOING,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 20));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setNumberOfDoses(1);
		getImmunizationFacade().save(immunization);

		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests VACCINATED for VACCINATION means of immunization.
	 */
	@Test
	void testDeriveVaccinationStatus_TwoDosesMeasles() {
		PersonDto person = creator.createPerson("Two", "Doses");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 40));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setNumberOfDoses(2);
		getImmunizationFacade().save(immunization);

		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests generic VACCINATED status for diseases (EVD).
	 */
	@Test
	void testDeriveVaccinationStatus_GenericVaccinatedForEvd() {
		PersonDto person = creator.createPerson("Generic", "Vaccination");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.EVD,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setNumberOfDoses(2);
		getImmunizationFacade().save(immunization);

		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.EVD, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that VACCINATION means → VACCINATED regardless of number of doses.
	 * Dose-based logic is removed in Phase 2; meansOfImmunization determines status.
	 */
	@Test
	void testDeriveVaccinationStatus_ZeroDoses() {
		PersonDto person = creator.createPerson("Zero", "Doses");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.SCHEDULED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 10));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setNumberOfDoses(0);
		getImmunizationFacade().save(immunization);

		// VACCINATION means → VACCINATED (dose count no longer determines status)
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that VACCINATION means → VACCINATED even without vaccination entries.
	 * Dose-based logic is removed; meansOfImmunization determines status.
	 */
	@Test
	void testDeriveVaccinationStatus_CountVaccinationEntries() {
		PersonDto person = creator.createPerson("Count", "Entries");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.ONGOING,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 40));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		getImmunizationFacade().save(immunization);

		// Add two vaccination entries
		VaccinationDto vaccination1 = VaccinationDto.build(nationalUser.toReference());
		vaccination1.setImmunization(immunization.toReference());
		vaccination1.setVaccinationDate(DateHelper.subtractDays(referenceDate, 40));
		getVaccinationFacade().save(vaccination1);

		VaccinationDto vaccination2 = VaccinationDto.build(nationalUser.toReference());
		vaccination2.setImmunization(immunization.toReference());
		vaccination2.setVaccinationDate(DateHelper.subtractDays(referenceDate, 10));
		getVaccinationFacade().save(vaccination2);

		// VACCINATION means → VACCINATED
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that VACCINATION means → VACCINATED even with no vaccination entries.
	 * Dose-based logic is removed; meansOfImmunization determines status.
	 */
	@Test
	void testDeriveVaccinationStatus_NoVaccinationEntries() {
		PersonDto person = creator.createPerson("No", "Entries");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.SCHEDULED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 10));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		getImmunizationFacade().save(immunization);

		// VACCINATION means → VACCINATED (dose count no longer determines status)
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that VACCINATION_RECOVERY means returns VACCINATED.
	 */
	@Test
	void testDeriveVaccinationStatus_VaccinationRecovery() {
		PersonDto person = creator.createPerson("Vaccination", "Recovery");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION_RECOVERY,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setNumberOfDoses(1);
		getImmunizationFacade().save(immunization);

		// VACCINATION_RECOVERY should be treated like VACCINATION → VACCINATED
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that MATERNAL_VACCINATION means returns VACCINATED.
	 */
	@Test
	void testDeriveVaccinationStatus_MaternalVaccination() {
		PersonDto person = creator.createPerson("Maternal", "Vaccination");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.MATERNAL_VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 20));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setNumberOfDoses(1);
		getImmunizationFacade().save(immunization);

		VaccinationStatusData data =
			getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.RESPIRATORY_SYNCYTIAL_VIRUS, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that MONOCLONAL_ANTIBODY means returns VACCINATED.
	 */
	@Test
	void testDeriveVaccinationStatus_MonoclonalAntibody() {
		PersonDto person = creator.createPerson("Monoclonal", "Antibody");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.MONOCLONAL_ANTIBODY,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 15));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setNumberOfDoses(1);
		getImmunizationFacade().save(immunization);

		VaccinationStatusData data =
			getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.RESPIRATORY_SYNCYTIAL_VIRUS, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that immunization without meansOfImmunization returns UNKNOWN.
	 */
	@Test
	void testDeriveVaccinationStatus_NullMeansOfImmunization() {
		PersonDto person = creator.createPerson("Null", "Means");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION, // Will be set to null manually
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setMeansOfImmunization(null); // Explicitly set to null
		getImmunizationFacade().save(immunization);

		// Should return UNKNOWN when meansOfImmunization is null
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(VaccinationStatus.UNKNOWN, data.getVaccinationStatus());
	}

	/**
	 * Tests edge case: validFrom equals reference date (should be included).
	 */
	@Test
	void testDeriveVaccinationStatus_ValidFromEqualsReferenceDate() {
		PersonDto person = creator.createPerson("Valid", "Today");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.ONGOING,
			rdcf);
		immunization.setValidFrom(referenceDate); // Exactly on reference date
		immunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		immunization.setNumberOfDoses(1);
		getImmunizationFacade().save(immunization);

		// Should include immunization with validFrom = reference date
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests edge case: validUntil equals reference date (should be included).
	 */
	@Test
	void testDeriveVaccinationStatus_ValidUntilEqualsReferenceDate() {
		PersonDto person = creator.createPerson("Expires", "Today");
		Date referenceDate = new Date();

		ImmunizationDto immunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		immunization.setValidUntil(referenceDate); // Expires exactly on reference date
		immunization.setNumberOfDoses(2);
		getImmunizationFacade().save(immunization);

		// Should include immunization with validUntil = reference date
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, data.getVaccinationStatus());
	}

	/**
	 * Tests that immunizations for different diseases are not mixed.
	 */
	@Test
	void testDeriveVaccinationStatus_DifferentDisease() {
		PersonDto person = creator.createPerson("Different", "Disease");
		Date referenceDate = new Date();

		// Create immunization for CORONAVIRUS
		ImmunizationDto coronavirusImmunization = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		coronavirusImmunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		coronavirusImmunization.setValidUntil(DateHelper.addDays(referenceDate, 365));
		coronavirusImmunization.setNumberOfDoses(2);
		getImmunizationFacade().save(coronavirusImmunization);

		// Query for MEASLES - should not find CORONAVIRUS immunization
		VaccinationStatusData data = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, data.getVaccinationStatus());
	}
}
