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
 * <li>Filters by validUntil (must not be before reference date)</li>
 * <li>Derives status based on meansOfImmunization (VACCINATION → VACCINATED, RECOVERY → RECOVERED, OTHER → OTHER)</li>
 * <li>Determines dose count from numberOfDoses or by counting Vaccination entries</li>
 * <li>Returns disease-specific statuses (ONE_DOSE, TWO_DOSE) when applicable</li>
 * <li>Falls back to generic VACCINATED for diseases without dose-specific statuses</li>
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
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(null, Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, status);

		// Null disease
		status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), null, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, status);

		// Null reference date
		status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, null);
		assertEquals(VaccinationStatus.UNVACCINATED, status);

		// TODO: Should we throw an exception instead of returning UNVACCINATED for nulls?
		// Probably not - better to fail gracefully
	}

	/**
	 * Tests that UNVACCINATED is returned when no immunizations exist for the person.
	 */
	@Test
	void testDeriveVaccinationStatus_NoImmunizations() {
		PersonDto person = creator.createPerson("No", "Immunizations");
		Date referenceDate = new Date();

		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, status);
	}

	/**
	 * Tests that only ACQUIRED immunizations are considered; other statuses are ignored.
	 */
	@Test
	void testDeriveVaccinationStatus_OnlyAcquiredImmunizations() {
		PersonDto person = creator.createPerson("Pending", "Immunization");
		Date referenceDate = new Date();

		// Create PENDING immunization (should be ignored)
		// Initially thought PENDING should count, but spec says only ACQUIRED
		ImmunizationDto pendingImmunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.PENDING,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.ONGOING,
			rdcf);
		pendingImmunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		pendingImmunization.setNumberOfDoses(2);
		getImmunizationFacade().save(pendingImmunization);

		// Should return UNVACCINATED because PENDING is not ACQUIRED
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, status);
	}

	/**
	 * Tests that immunizations with validFrom after reference date are ignored.
	 */
	@Test
	void testDeriveVaccinationStatus_ValidFromAfterReferenceDate() {
		PersonDto person = creator.createPerson("Future", "Immunization");
		Date referenceDate = new Date();

		// Create immunization with validFrom in the future
		// This was tricky - makes sense you can't be vaccinated in the future!
		ImmunizationDto futureImmunization = creator.createImmunization(
			Disease.MEASLES,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		futureImmunization.setValidFrom(DateHelper.addDays(referenceDate, 10));
		futureImmunization.setNumberOfDoses(2);
		getImmunizationFacade().save(futureImmunization);

		// Should return UNVACCINATED because validFrom is after reference date
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, status);
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
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, status);
	}

	/**
	 * Tests that the immunization with closest validFrom to reference date is selected.
	 * This is important when someone has multiple immunization records over time.
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
		newerImmunization.setNumberOfDoses(2);
		getImmunizationFacade().save(newerImmunization);

		// Should select newer immunization and return VACCINATED_TWO_DOSE
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED_TWO_DOSE, status);
	}

	/**
	 * Tests RECOVERED status for RECOVERY means of immunization.
	 */
	@Test
	void testDeriveVaccinationStatus_Recovery() {
		PersonDto person = creator.createPerson("Recovered", "Person");
		Date referenceDate = new Date();

		// Natural immunity from recovering from the disease
		ImmunizationDto immunization = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.RECOVERY,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 60));
		immunization.setRecoveryDate(DateHelper.subtractDays(referenceDate, 60));
		getImmunizationFacade().save(immunization);

		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(VaccinationStatus.RECOVERED, status);
		// Note: RECOVERED is different from VACCINATED - important distinction!
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
		immunization.setMeansOfImmunizationDetails("Experimental monoclonal antibodies");
		getImmunizationFacade().save(immunization);

		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(VaccinationStatus.OTHER, status);
	}

	/**
	 * Tests that getMeansOfImmunizationDetails retrieves the details for OTHER means of immunization.
	 * This is important for displaying the free text explanation of what "Other" means.
	 */
	@Test
	void testGetMeansOfImmunizationDetails_OtherWithDetails() {
		PersonDto person = creator.createPerson("Other", "WithDetails");
		Date referenceDate = new Date();

		// Create immunization with OTHER means and specific details
		ImmunizationDto immunization = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.OTHER,
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		immunization.setMeansOfImmunizationDetails("Experimental monoclonal antibodies - Phase 3 trial");
		getImmunizationFacade().save(immunization);

		// Verify status is OTHER
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(VaccinationStatus.OTHER, status);

		// Verify details are retrieved correctly
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
		// Should return null when no immunization found, not crash
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
		immunization.setNumberOfDoses(2);
		getImmunizationFacade().save(immunization);

		// Details are only relevant for OTHER means of immunization
		String details = getImmunizationService().getMeansOfImmunizationDetails(person.getUuid(), Disease.MEASLES, referenceDate);
		// Should return null for non-OTHER means - details field might not even be set
		assertEquals(null, details);
	}

	/**
	 * Tests that getMeansOfImmunizationDetails selects the same immunization as deriveVaccinationStatus.
	 * Important: both methods should use the same selection logic (closest validFrom).
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
		newerImmunization.setMeansOfImmunizationDetails("New experimental protocol");
		getImmunizationFacade().save(newerImmunization);

		// Both methods should select the newer immunization (closest validFrom)
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(VaccinationStatus.OTHER, status);

		String details = getImmunizationService().getMeansOfImmunizationDetails(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals("New experimental protocol", details);
		// If this fails, the selection logic is inconsistent between the two methods!
	}

	/**
	 * Tests VACCINATED_ONE_DOSE for diseases with dose-specific statuses (MEASLES).
	 * MEASLES has @Diseases annotation on ONE_DOSE and TWO_DOSE enum values.
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
		immunization.setNumberOfDoses(1);
		getImmunizationFacade().save(immunization);

		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED_ONE_DOSE, status);
	}

	/**
	 * Tests VACCINATED_TWO_DOSE for diseases with dose-specific statuses (MEASLES).
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
		immunization.setNumberOfDoses(2);
		getImmunizationFacade().save(immunization);

		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED_TWO_DOSE, status);
	}

	/**
	 * Tests generic VACCINATED status for diseases without dose-specific statuses (EVD).
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
		immunization.setNumberOfDoses(2);
		getImmunizationFacade().save(immunization);

		// EVD doesn't have dose-specific statuses, so should return generic VACCINATED
		// Makes sense - not all diseases track dose counts the same way
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.EVD, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, status);
	}

	/**
	 * Tests UNVACCINATED when numberOfDoses is explicitly 0.
	 * TODO: Is 0 doses with ACQUIRED status even valid? Maybe validation needed.
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
		immunization.setNumberOfDoses(0);
		getImmunizationFacade().save(immunization);

		// Explicitly 0 doses should return UNVACCINATED
		// This might be an edge case that shouldn't happen in practice
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, status);
	}

	/**
	 * Tests that vaccination entries are counted when numberOfDoses is null.
	 * This is the fallback mechanism
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
		// Don't set numberOfDoses - should count vaccination entries instead
		// This way we have two sources of truth: explicit count or actual records
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

		// Should count 2 vaccination entries and return VACCINATED_TWO_DOSE
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED_TWO_DOSE, status);
	}

	/**
	 * Tests UNVACCINATED when numberOfDoses is null and no vaccination entries exist.
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
		// Don't set numberOfDoses and don't add vaccination entries
		getImmunizationFacade().save(immunization);

		// Should return UNVACCINATED because no doses are recorded
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, status);
	}

	/**
	 * Tests that VACCINATION_RECOVERY means returns dose-based status (treated as vaccination).
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
		immunization.setNumberOfDoses(1);
		getImmunizationFacade().save(immunization);

		// VACCINATION_RECOVERY should be treated like VACCINATION
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED_ONE_DOSE, status);
	}

	/**
	 * Tests that MATERNAL_VACCINATION means returns dose-based status.
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
		immunization.setNumberOfDoses(1);
		getImmunizationFacade().save(immunization);

		// MATERNAL_VACCINATION should be treated like VACCINATION
		// RSV doesn't have dose-specific statuses, so should return generic VACCINATED
		VaccinationStatus status =
			getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.RESPIRATORY_SYNCYTIAL_VIRUS, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, status);
	}

	/**
	 * Tests that MONOCLONAL_ANTIBODY means returns dose-based status.
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
		immunization.setNumberOfDoses(1);
		getImmunizationFacade().save(immunization);

		// MONOCLONAL_ANTIBODY should be treated like VACCINATION
		VaccinationStatus status =
			getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.RESPIRATORY_SYNCYTIAL_VIRUS, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED, status);
	}

	/**
	 * Tests that immunization without meansOfImmunization returns UNKNOWN.
	 * Had to manually set to null because creator requires it.
	 */
	@Test
	void testDeriveVaccinationStatus_NullMeansOfImmunization() {
		PersonDto person = creator.createPerson("Null", "Means");
		Date referenceDate = new Date();

		// Create immunization with null meansOfImmunization
		ImmunizationDto immunization = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION, // Will be set to null manually
			ImmunizationManagementStatus.COMPLETED,
			rdcf);
		immunization.setValidFrom(DateHelper.subtractDays(referenceDate, 30));
		immunization.setMeansOfImmunization(null); // Explicitly set to null
		getImmunizationFacade().save(immunization);

		// Should return UNKNOWN when meansOfImmunization is null
		// This probably shouldn't happen in production, but good to be safe
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.CORONAVIRUS, referenceDate);
		assertEquals(VaccinationStatus.UNKNOWN, status);
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
		immunization.setNumberOfDoses(1);
		getImmunizationFacade().save(immunization);

		// Should include immunization with validFrom = reference date
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED_ONE_DOSE, status);
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
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.VACCINATED_TWO_DOSE, status);
	}

	/**
	 * Tests that immunizations for different diseases are not mixed.
	 * Important being vaccinated for COVID doesn't mean you're vaccinated for measles!
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
		coronavirusImmunization.setNumberOfDoses(2);
		getImmunizationFacade().save(coronavirusImmunization);

		// Query for MEASLES - should not find CORONAVIRUS immunization
		// Obvious but good to test anyway
		VaccinationStatus status = getImmunizationService().deriveVaccinationStatus(person.getUuid(), Disease.MEASLES, referenceDate);
		assertEquals(VaccinationStatus.UNVACCINATED, status);
	}
}
