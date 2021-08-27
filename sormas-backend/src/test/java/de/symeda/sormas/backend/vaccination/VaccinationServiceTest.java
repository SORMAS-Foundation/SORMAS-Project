package de.symeda.sormas.backend.vaccination;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasValue;

import java.util.Map;

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
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class VaccinationServiceTest extends AbstractBeanTest {

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
	public void testGetLastVaccinationType() {

		PersonDto person1 = creator.createPerson("John", "Doe");
		ImmunizationDto immunizationDto1 = creator.createImmunization(
			Disease.CORONAVIRUS,
			person1.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);

		PersonDto person2 = creator.createPerson("Jane", "Doe");
		ImmunizationDto immunizationDto2 = creator.createImmunization(
			Disease.CORONAVIRUS,
			person1.toReference(),
			nationalUser.toReference(),
			ImmunizationStatus.ACQUIRED,
			MeansOfImmunization.VACCINATION,
			ImmunizationManagementStatus.COMPLETED,
			rdcf1);

		HealthConditionsDto healthConditions = new HealthConditionsDto();
		healthConditions.setOtherConditions("PEBMAC");

		creator.createVaccinationEntity(
			nationalUser.toReference(),
			new ImmunizationReferenceDto(immunizationDto1.getUuid(), immunizationDto1.toString(), immunizationDto1.getExternalId()),
			healthConditions,
			"First Vaccine Type");

		creator.createVaccinationEntity(
			nationalUser.toReference(),
			new ImmunizationReferenceDto(immunizationDto1.getUuid(), immunizationDto1.toString(), immunizationDto1.getExternalId()),
			healthConditions,
			"Second Vaccine Type");

		creator.createVaccinationEntity(
			nationalUser.toReference(),
			new ImmunizationReferenceDto(immunizationDto2.getUuid(), immunizationDto2.toString(), immunizationDto2.getExternalId()),
			healthConditions,
			"Vaccine Type");

		Map<String, String> lastVaccinationTypes = getVaccinationService().getLastVaccinationType();
		assertThat(lastVaccinationTypes.size(), equalTo(2));
		hasValue(lastVaccinationTypes.containsValue("Second Vaccine Type"));
		hasValue(lastVaccinationTypes.containsValue("Vaccine Type"));
	}
}
