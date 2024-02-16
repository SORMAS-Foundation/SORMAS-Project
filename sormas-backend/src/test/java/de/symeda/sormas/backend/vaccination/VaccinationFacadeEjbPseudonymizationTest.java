/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.vaccination;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.vaccination.VaccinationCriteria;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class VaccinationFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

	@Override
	public void init() {

		super.init();

		UserRoleReferenceDto newUserRole = creator.createUserRole(
			"NoEventNoCaseView",
			JurisdictionLevel.DISTRICT,
			UserRight.CASE_CLINICIAN_VIEW,
			UserRight.CASE_VIEW,
			UserRight.PERSON_VIEW);

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER),
			newUserRole);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER),
			newUserRole);

		loginWith(user2);
	}

	@Test
	public void testGetTravelEntryOutsideJurisdiction() {
		PersonReferenceDto personRef = creator.createPerson().toReference();
		ImmunizationDto immunization = creator.createImmunization(Disease.CORONAVIRUS, personRef, user1.toReference(), rdcf1);
		VaccinationDto vaccination = createVaccination(user1, immunization.toReference());

		assertPseudonymized(getVaccinationFacade().getByUuid(vaccination.getUuid()));
		assertPseudonymized(getVaccinationFacade().getByUuids(Collections.singletonList(vaccination.getUuid())).get(0));
		assertPseudonymized(getVaccinationFacade().getAllAfter(new Date(0)).get(0));
		// no pseudonymization on index list currently
		assertThat(
			getVaccinationFacade().getIndexList(new VaccinationCriteria.Builder(personRef).build(), null, null, null).get(0).isPseudonymized(),
			is(false));
	}

	@Test
	public void testGetReportOfCaseWithSpecialAccess() {
		PersonReferenceDto personRef = creator.createPerson().toReference();
		ImmunizationDto immunization = creator.createImmunization(Disease.CORONAVIRUS, personRef, user1.toReference(), rdcf1);
		VaccinationDto vaccination = createVaccination(user1, immunization.toReference());

		CaseDataDto caze = creator.createCase(user2.toReference(), personRef, rdcf2 );
		creator.createSpecialCaseAccess(caze.toReference(), user1.toReference(), user2.toReference(), DateHelper.addDays(new Date(), 1));

		assertNotPseudonymized(getVaccinationFacade().getByUuid(vaccination.getUuid()), user1);
		assertNotPseudonymized(getVaccinationFacade().getByUuids(Collections.singletonList(vaccination.getUuid())).get(0), user1);
		assertNotPseudonymized(getVaccinationFacade().getAllAfter(new Date(0)).get(0), user1);
		assertThat(
			getVaccinationFacade().getIndexList(new VaccinationCriteria.Builder(personRef).build(), null, null, null).get(0).isPseudonymized(),
			is(false));
	}

	private void assertPseudonymized(VaccinationDto vaccination) {
		assertThat(vaccination.isPseudonymized(), is(true));
		assertThat(vaccination.getReportingUser(), is(nullValue()));
		assertThat(vaccination.getVaccineName(), is(Vaccine.OTHER));
		assertThat(vaccination.getOtherVaccineName(), is(""));
		assertThat(vaccination.getVaccineManufacturer(), is(VaccineManufacturer.OTHER));
		assertThat(vaccination.getOtherVaccineManufacturer(), is(""));
		assertThat(vaccination.getVaccineType(), is(""));
		assertThat(vaccination.getVaccineDose(), is(""));
		assertThat(vaccination.getVaccineInn(), is(""));
		assertThat(vaccination.getVaccineBatchNumber(), is(""));
		assertThat(vaccination.getVaccineUniiCode(), is(""));
		assertThat(vaccination.getVaccineAtcCode(), is(""));
	}

	private void assertNotPseudonymized(VaccinationDto vaccination, UserDto user) {
		assertThat(vaccination.isPseudonymized(), is(false));
		assertThat(vaccination.getReportingUser(), is(user));
		assertThat(vaccination.getVaccineName(), is(Vaccine.OTHER));
		assertThat(vaccination.getOtherVaccineName(), is("Test vaccine name"));
		assertThat(vaccination.getVaccineManufacturer(), is(VaccineManufacturer.OTHER));
		assertThat(vaccination.getOtherVaccineManufacturer(), is("Test vaccine name"));
		assertThat(vaccination.getVaccineType(), is("Test vaccine type"));
		assertThat(vaccination.getVaccineDose(), is("Test vaccine dose"));
		assertThat(vaccination.getVaccineInn(), is("Test vaccine INN"));
		assertThat(vaccination.getVaccineBatchNumber(), is("Test vaccine batch number"));
		assertThat(vaccination.getVaccineUniiCode(), is("Test vaccine UNII code"));
		assertThat(vaccination.getVaccineAtcCode(), is("Test vaccine ATC code"));
	}

	private VaccinationDto createVaccination(UserDto user, ImmunizationReferenceDto immunization) {
		VaccinationDto vaccination = VaccinationDto.build(user.toReference());
		vaccination.setReportDate(new Date());
		vaccination.setVaccinationDate(DateHelper.subtractDays(new Date(), 20));
		vaccination.setReportingUser(user.toReference());
		vaccination.setImmunization(immunization);
		vaccination.setVaccineName(Vaccine.OTHER);
		vaccination.setOtherVaccineName("Test vaccine name");
		vaccination.setVaccineManufacturer(VaccineManufacturer.OTHER);
		vaccination.setOtherVaccineManufacturer("Test vaccine name");
		vaccination.setVaccineType("Test vaccine type");
		vaccination.setVaccineDose("Test vaccine dose");
		vaccination.setVaccineInn("Test vaccine INN");
		vaccination.setVaccineBatchNumber("Test vaccine batch number");
		vaccination.setVaccineUniiCode("Test vaccine UNII code");
		vaccination.setVaccineAtcCode("Test vaccine ATC code");

		return getVaccinationFacade().save(vaccination);
	}
}
