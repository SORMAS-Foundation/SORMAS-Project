package de.symeda.sormas.backend.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.util.ValidationProblem;

public class PersonValidationProblemsTest extends AbstractBeanTest {

	private RDCF rdcf;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		loginWith(creator.createNationalUser());
	}

	@Test
	public void reportsEveryMissingFieldInOrder() {
		List<ValidationProblem> problems = getPersonFacade().collectValidationProblems(PersonDto.build());

		assertEquals(
			List.of(Validations.specifyFirstName, Validations.specifyLastName, Validations.specifySex),
			problems.stream().map(ValidationProblem::getValidationKey).collect(Collectors.toList()));
		assertEquals(List.of("firstName"), problems.get(0).getPropertyPaths());
		assertEquals(List.of("lastName"), problems.get(1).getPropertyPaths());
		assertEquals(List.of("sex"), problems.get(2).getPropertyPaths());
	}

	@Test
	public void reportsAnInvalidBirthDateAsAProblem() {
		PersonDto person = validPerson();
		person.setBirthdateDD(31);
		person.setBirthdateMM(2);
		person.setBirthdateYYYY(2000);

		List<ValidationProblem> problems = getPersonFacade().collectValidationProblems(person);

		assertEquals(1, problems.size());
		assertNull(problems.get(0).getValidationKey());
		assertEquals(List.of("birthdateDD", "birthdateMM", "birthdateYYYY"), problems.get(0).getPropertyPaths());
	}

	@Test
	public void skipsAddressConsistencyChecksWhenTheRegionIsMissing() {
		PersonDto person = validPerson();
		person.getAddress().setDistrict(rdcf.district);
		person.getAddress().setFacility(rdcf.facility);

		List<ValidationProblem> problems = getPersonFacade().collectValidationProblems(person);

		assertEquals(1, problems.size());
		assertEquals(Validations.validRegion, problems.get(0).getValidationKey());
		assertEquals(List.of("address.region"), problems.get(0).getPropertyPaths());
	}

	private PersonDto validPerson() {
		PersonDto person = PersonDto.build();
		person.setFirstName("Jane");
		person.setLastName("Doe");
		person.setSex(Sex.FEMALE);
		return person;
	}
}
