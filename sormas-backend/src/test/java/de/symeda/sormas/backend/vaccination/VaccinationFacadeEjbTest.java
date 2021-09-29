package de.symeda.sormas.backend.vaccination;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

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
}
