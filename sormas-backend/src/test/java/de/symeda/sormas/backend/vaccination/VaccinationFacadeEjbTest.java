package de.symeda.sormas.backend.vaccination;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationReferenceDto;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class VaccinationFacadeEjbTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private UserDto nationalUser;

	@Override
	public void init() {

		super.init();
		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		nationalUser = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.community.getUuid(),
			rdcf1.facility.getUuid(),
			"Nat",
			"User",
			UserRole.NATIONAL_USER);
	}

	@Test
	public void testSave() {

		loginWith(nationalUser);

		PersonDto person = creator.createPerson("John", "Doe");
		ImmunizationDto immunizationDto = creator.createImmunization(
			Disease.CORONAVIRUS,
			person.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);

		HealthConditionsDto healthConditions = new HealthConditionsDto();
		healthConditions.setOtherConditions("PEBMAC");

		VaccinationDto vaccinationDto = creator.createVaccination(
			nationalUser.toReference(),
			new ImmunizationReferenceDto(immunizationDto.getUuid(), immunizationDto.toString(), immunizationDto.getExternalId()),
			healthConditions);

		Vaccination actualVaccination = getVaccinationService().getByUuid(vaccinationDto.getUuid());
		assertThat(actualVaccination.getUuid(), equalTo(vaccinationDto.getUuid()));
		assertThat(vaccinationDto.getHealthConditions().getOtherConditions(), equalTo("PEBMAC"));

		ImmunizationDto actualImmunization = getImmunizationFacade().getByUuid(immunizationDto.getUuid());
		assertThat(actualImmunization.getVaccinations(), hasSize(1));
		assertThat(actualImmunization.getVaccinations().get(0).getUuid(), equalTo(vaccinationDto.getUuid()));
	}

	@Test
	public void testCreate() {

		PersonDto person1 = creator.createPerson("John", "Doe");
		PersonDto person2 = creator.createPerson("Jane", "Doe");
		Disease disease1 = Disease.CORONAVIRUS;
		Disease disease2 = Disease.CHOLERA;

		VaccinationDto vaccination11 = VaccinationDto.build(nationalUser.toReference());
		vaccination11 = getVaccinationFacade().create(vaccination11, rdcf1.region, rdcf1.district, person1.toReference(), disease1);
		ImmunizationReferenceDto immunization11 = vaccination11.getImmunization();
		assertNotNull(immunization11);

		VaccinationDto vaccination12 = VaccinationDto.build(nationalUser.toReference());
		vaccination12 = getVaccinationFacade().create(vaccination12, rdcf1.region, rdcf1.district, person1.toReference(), disease1);
		ImmunizationReferenceDto immunization12 = vaccination12.getImmunization();
		assertEquals(immunization11, immunization12);

		VaccinationDto vaccination13 = VaccinationDto.build(nationalUser.toReference());
		vaccination13 = getVaccinationFacade().create(vaccination13, rdcf1.region, rdcf1.district, person1.toReference(), disease2);
		ImmunizationReferenceDto immunization13 = vaccination13.getImmunization();
		assertNotEquals(immunization11, immunization13);

		VaccinationDto vaccination21 = VaccinationDto.build(nationalUser.toReference());
		vaccination21 = getVaccinationFacade().create(vaccination21, rdcf1.region, rdcf1.district, person2.toReference(), disease1);
		ImmunizationReferenceDto immunization21 = vaccination21.getImmunization();
		assertNotEquals(immunization11, immunization21);

		VaccinationDto vaccination22 = VaccinationDto.build(nationalUser.toReference());
		vaccination22 = getVaccinationFacade().create(vaccination22, rdcf1.region, rdcf1.district, person2.toReference(), disease2);
		ImmunizationReferenceDto immunization22 = vaccination22.getImmunization();
		assertNotEquals(immunization21, immunization22);

		// Test correct association based on dates
		PersonDto person3 = creator.createPerson("James", "Doe");
		Date referenceDate = new Date();
		ImmunizationDto immunizationStartEnd = creator.createImmunization(disease1, person3.toReference(), nationalUser.toReference(), rdcf1, i -> {
			i.setStartDate(DateHelper.subtractDays(referenceDate, 400));
			i.setEndDate(DateHelper.subtractDays(referenceDate, 380));
		});
		ImmunizationDto immunizationStart = creator.createImmunization(disease1, person3.toReference(), nationalUser.toReference(), rdcf1, i -> {
			i.setStartDate(DateHelper.subtractDays(referenceDate, 420));
		});
		creator.createImmunization(disease1, person3.toReference(), nationalUser.toReference(), rdcf1, i -> {
			i.setEndDate(DateHelper.subtractDays(referenceDate, 390));
		});
		ImmunizationDto immunizationReport = creator.createImmunization(disease1, person3.toReference(), nationalUser.toReference(), rdcf1);
		ImmunizationDto immunizationReport2 = creator.createImmunization(disease2, person3.toReference(), nationalUser.toReference(), rdcf1);

		VaccinationDto vaccination31 = VaccinationDto.build(nationalUser.toReference());
		vaccination31.setVaccinationDate(DateHelper.subtractDays(referenceDate, 390));
		vaccination31 = getVaccinationFacade().create(vaccination31, rdcf1.region, rdcf1.district, person3.toReference(), disease1);
		assertEquals(vaccination31.getImmunization(), immunizationStartEnd.toReference());

		VaccinationDto vaccination32 = VaccinationDto.build(nationalUser.toReference());
		vaccination32.setVaccinationDate(DateHelper.subtractDays(referenceDate, 415));
		vaccination32 = getVaccinationFacade().create(vaccination32, rdcf1.region, rdcf1.district, person3.toReference(), disease1);
		assertEquals(vaccination32.getImmunization(), immunizationStart.toReference());

		VaccinationDto vaccination33 = VaccinationDto.build(nationalUser.toReference());
		vaccination33.setVaccinationDate(DateHelper.subtractDays(referenceDate, 0));
		vaccination33 = getVaccinationFacade().create(vaccination33, rdcf1.region, rdcf1.district, person3.toReference(), disease1);
		assertEquals(vaccination33.getImmunization(), immunizationStartEnd.toReference());

		VaccinationDto vaccination34 = VaccinationDto.build(nationalUser.toReference());
		vaccination34.setVaccinationDate(DateHelper.subtractDays(referenceDate, 100));
		vaccination34 = getVaccinationFacade().create(vaccination34, rdcf1.region, rdcf1.district, person3.toReference(), disease2);
		assertEquals(vaccination34.getImmunization(), immunizationReport2.toReference());

		VaccinationDto vaccination35 = VaccinationDto.build(nationalUser.toReference());
		vaccination35 = getVaccinationFacade().create(vaccination35, rdcf1.region, rdcf1.district, person3.toReference(), disease1);
		assertThat(vaccination35.getImmunization(), anyOf(is(immunizationReport.toReference()), is(immunizationReport2.toReference())));
	}

	@Test
	public void testGetAllVaccinations() {

		PersonDto person1 = creator.createPerson("John", "Doe");
		PersonDto person2 = creator.createPerson("Jane", "Doe");
		Disease disease1 = Disease.CORONAVIRUS;
		Disease disease2 = Disease.CHOLERA;

		ImmunizationDto immunization11 = creator.createImmunization(disease1, person1.toReference(), nationalUser.toReference(), rdcf1);
		ImmunizationDto immunization12 = creator.createImmunization(disease1, person1.toReference(), nationalUser.toReference(), rdcf1);
		ImmunizationDto immunization13 = creator.createImmunization(disease2, person1.toReference(), nationalUser.toReference(), rdcf1);
		ImmunizationDto immunization21 = creator.createImmunization(disease1, person2.toReference(), nationalUser.toReference(), rdcf1);
		creator.createImmunization(disease2, person2.toReference(), nationalUser.toReference(), rdcf1);

		VaccinationDto vaccination111 = creator.createVaccination(nationalUser.toReference(), immunization11.toReference());
		VaccinationDto vaccination112 = creator.createVaccination(nationalUser.toReference(), immunization11.toReference());
		VaccinationDto vaccination121 = creator.createVaccination(nationalUser.toReference(), immunization12.toReference());
		VaccinationDto vaccination131 = creator.createVaccination(nationalUser.toReference(), immunization13.toReference());
		VaccinationDto vaccination211 = creator.createVaccination(nationalUser.toReference(), immunization21.toReference());
		VaccinationDto vaccination212 = creator.createVaccination(nationalUser.toReference(), immunization21.toReference());

		List<VaccinationDto> vaccinations = getVaccinationFacade().getAllVaccinations(person1.getUuid(), disease1);
		assertThat(vaccinations, hasSize(3));
		assertThat(vaccinations, contains(vaccination111, vaccination112, vaccination121));
		vaccinations = getVaccinationFacade().getAllVaccinations(person1.getUuid(), disease2);
		assertThat(vaccinations, hasSize(1));
		assertThat(vaccinations, contains(vaccination131));
		vaccinations = getVaccinationFacade().getAllVaccinations(person2.getUuid(), disease1);
		assertThat(vaccinations, hasSize(2));
		assertThat(vaccinations, contains(vaccination211, vaccination212));
		vaccinations = getVaccinationFacade().getAllVaccinations(person2.getUuid(), disease2);
		assertThat(vaccinations, hasSize(0));
	}
}
