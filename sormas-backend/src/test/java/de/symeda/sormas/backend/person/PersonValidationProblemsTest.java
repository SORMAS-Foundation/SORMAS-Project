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
	private RDCF otherRdcf;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		otherRdcf = creator.createRDCF("Other Region", "Other District", "Other Community", "Other Facility");
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

	@Test
	public void reportsADistrictOutsideTheAddressRegion() {
		PersonDto person = validPerson();
		person.getAddress().setRegion(rdcf.region);
		person.getAddress().setDistrict(otherRdcf.district);

		assertSingleProblem(person, Validations.noAddressDistrictInAddressRegion, "address.district", "address.region");
	}

	@Test
	public void doesNotRepeatTheRegionMismatchForAFacilityOfAnotherRegion() {
		PersonDto person = validPerson();
		person.getAddress().setRegion(rdcf.region);
		person.getAddress().setDistrict(otherRdcf.district);
		person.getAddress().setFacility(otherRdcf.facility);

		assertSingleProblem(person, Validations.noAddressDistrictInAddressRegion, "address.district", "address.region");
	}

	@Test
	public void reportsACommunityOutsideTheAddressDistrict() {
		PersonDto person = validPerson();
		person.getAddress().setRegion(rdcf.region);
		person.getAddress().setDistrict(rdcf.district);
		person.getAddress().setCommunity(otherRdcf.community);

		assertSingleProblem(person, Validations.noAddressCommunityInAddressDistrict, "address.community", "address.district");
	}

	@Test
	public void reportsAFacilityOutsideTheAddressDistrict() {
		PersonDto person = validPerson();
		person.getAddress().setRegion(rdcf.region);
		person.getAddress().setDistrict(rdcf.district);
		person.getAddress().setFacility(otherRdcf.facility);

		assertSingleProblem(person, Validations.noAddressFacilityInAddressDistrict, "address.facility", "address.district");
	}

	@Test
	public void reportsContactPersonDetailsWithoutAFacilityType() {
		PersonDto person = validPerson();
		person.getAddress().setContactPersonFirstName("Jane");

		assertSingleProblem(
			person,
			Validations.importPersonContactDetailsWithoutFacilityType,
			"address.facilityType",
			"address.contactPersonFirstName",
			"address.contactPersonLastName",
			"address.contactPersonEmail",
			"address.contactPersonPhone");
	}

	@Test
	public void reportsIndependentProblemsTogetherInOrder() {
		PersonDto person = validPerson();
		person.setLastName(null);
		person.setEmailAddress("not an email");

		List<ValidationProblem> problems = getPersonFacade().collectValidationProblems(person);

		assertEquals(
			List.of(Validations.specifyLastName, Validations.validEmailAddress),
			problems.stream().map(ValidationProblem::getValidationKey).collect(Collectors.toList()));
	}

	private void assertSingleProblem(PersonDto person, String validationKey, String... propertyPaths) {
		List<ValidationProblem> problems = getPersonFacade().collectValidationProblems(person);

		assertEquals(1, problems.size());
		assertEquals(validationKey, problems.get(0).getValidationKey());
		assertEquals(List.of(propertyPaths), problems.get(0).getPropertyPaths());
	}

	private PersonDto validPerson() {
		PersonDto person = PersonDto.build();
		person.setFirstName("Jane");
		person.setLastName("Doe");
		person.setSex(Sex.FEMALE);
		return person;
	}
}
