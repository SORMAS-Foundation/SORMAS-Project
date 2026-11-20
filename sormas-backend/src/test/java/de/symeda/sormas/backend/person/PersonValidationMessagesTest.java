package de.symeda.sormas.backend.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class PersonValidationMessagesTest extends AbstractBeanTest {

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
	public void validPersonIsAccepted() {
		getPersonFacade().validate(validPerson());
	}

	@Test
	public void missingFirstName() {
		assertMessage(Validations.specifyFirstName, person -> person.setFirstName(" "));
	}

	@Test
	public void missingLastName() {
		assertMessage(Validations.specifyLastName, person -> person.setLastName(null));
	}

	@Test
	public void missingSex() {
		assertMessage(Validations.specifySex, person -> person.setSex(null));
	}

	@Test
	public void multiplePrimaryPhoneNumbers() {
		assertMessage(Validations.personMultiplePrimaryPhoneNumbers, person -> {
			addContactDetail(person, true, PersonContactDetailType.PHONE, "123456");
			addContactDetail(person, true, PersonContactDetailType.PHONE, "654321");
		});
	}

	@Test
	public void multiplePrimaryEmailAddresses() {
		assertMessage(Validations.personMultiplePrimaryEmailAddresses, person -> {
			addContactDetail(person, true, PersonContactDetailType.EMAIL, "a@example.org");
			addContactDetail(person, true, PersonContactDetailType.EMAIL, "b@example.org");
		});
	}

	@Test
	public void invalidPrimaryEmailAddress() {
		assertMessage(
			Validations.validEmailAddress,
			I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.EMAIL_ADDRESS),
			person -> addContactDetail(person, true, PersonContactDetailType.EMAIL, "not an email"));
	}

	@Test
	public void invalidPrimaryPhoneNumber() {
		assertMessage(
			Validations.validPhoneNumber,
			I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PHONE),
			person -> addContactDetail(person, true, PersonContactDetailType.PHONE, "call me"));
	}

	@Test
	public void addressDistrictOutsideAddressRegion() {
		assertMessage(Validations.noAddressDistrictInAddressRegion, person -> {
			person.getAddress().setRegion(rdcf.region);
			person.getAddress().setDistrict(otherRdcf.district);
		});
	}

	@Test
	public void addressCommunityOutsideAddressDistrict() {
		assertMessage(Validations.noAddressCommunityInAddressDistrict, person -> {
			person.getAddress().setDistrict(rdcf.district);
			person.getAddress().setCommunity(otherRdcf.community);
		});
	}

	@Test
	public void addressDistrictWithoutAddressRegion() {
		assertMessage(Validations.validRegion, person -> person.getAddress().setDistrict(rdcf.district));
	}

	@Test
	public void addressFacilityWithoutAddressRegion() {
		assertMessage(Validations.validRegion, person -> person.getAddress().setFacility(rdcf.facility));
	}

	@Test
	public void addressFacilityOutsideAddressDistrict() {
		assertMessage(Validations.noAddressFacilityInAddressDistrict, person -> {
			person.getAddress().setRegion(rdcf.region);
			person.getAddress().setDistrict(rdcf.district);
			person.getAddress().setFacility(otherRdcf.facility);
		});
	}

	@Test
	public void addressFacilityOutsideAddressCommunity() {
		assertMessage(Validations.noAddressFacilityInAddressCommunity, person -> {
			person.getAddress().setRegion(rdcf.region);
			person.getAddress().setDistrict(rdcf.district);
			person.getAddress().setCommunity(rdcf.community);
			person.getAddress().setFacility(otherRdcf.facility);
		});
	}

	@Test
	public void addressFacilityOutsideAddressRegion() {
		FacilityReferenceDto strayFacility = facilityInRegionOnly(otherRdcf);

		assertMessage(Validations.noAddressFacilityInAddressRegion, person -> {
			person.getAddress().setRegion(rdcf.region);
			person.getAddress().setDistrict(rdcf.district);
			person.getAddress().setFacility(strayFacility);
		});
	}

	@Test
	public void contactPersonDetailsWithoutFacilityType() {
		assertMessage(Validations.importPersonContactDetailsWithoutFacilityType, person -> person.getAddress().setContactPersonFirstName("Jane"));
	}

	@Test
	public void invalidContactPersonEmail() {
		assertMessage(
			Validations.validEmailAddress,
			I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.CONTACT_PERSON_EMAIL),
			person -> {
				person.getAddress().setFacilityType(FacilityType.HOSPITAL);
				person.getAddress().setContactPersonEmail("not an email");
			});
	}

	@Test
	public void invalidContactPersonPhone() {
		assertMessage(
			Validations.validPhoneNumber,
			I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.CONTACT_PERSON_PHONE),
			person -> {
				person.getAddress().setFacilityType(FacilityType.HOSPITAL);
				person.getAddress().setContactPersonPhone("call me");
			});
	}

	@Test
	public void invalidBirthDate() {
		assertMessage(Validations.birthDateInvalid, person -> {
			person.setBirthdateYYYY(2000);
			person.setBirthdateMM(2);
			person.setBirthdateDD(31);
		});
	}

	@Test
	public void birthDateInTheFuture() {
		assertMessage(Validations.birthDateInFuture, person -> person.setBirthdateYYYY(LocalDate.now().getYear() + 1));
	}

	@Test
	public void missingFirstNameIsReportedBeforeMissingSex() {
		assertMessage(Validations.specifyFirstName, person -> {
			person.setFirstName(null);
			person.setSex(null);
		});
	}

	@Test
	public void missingSexIsReportedBeforeInvalidBirthDate() {
		assertMessage(Validations.specifySex, person -> {
			person.setSex(null);
			person.setBirthdateYYYY(2000);
			person.setBirthdateMM(2);
			person.setBirthdateDD(31);
		});
	}

	private FacilityReferenceDto facilityInRegionOnly(RDCF target) {
		var region = getRegionService().getByUuid(target.region.getUuid());
		var facility = creator.createFacility("Stray Facility", FacilityType.HOSPITAL, region, null, null);
		return new FacilityReferenceDto(facility.getUuid(), facility.getName(), null);
	}

	private PersonDto validPerson() {
		PersonDto person = PersonDto.build();
		person.setFirstName("Jane");
		person.setLastName("Doe");
		person.setSex(Sex.FEMALE);
		return person;
	}

	private void addContactDetail(PersonDto person, boolean primary, PersonContactDetailType type, String contactInformation) {
		person.getPersonContactDetails()
			.add(PersonContactDetailDto.build(person.toReference(), primary, type, null, null, contactInformation, null, false, null, null));
	}

	private void assertMessage(String validationKey, Consumer<PersonDto> breakIt) {
		assertExactMessage(I18nProperties.getValidationError(validationKey), breakIt);
	}

	private void assertMessage(String validationKey, String captionArgument, Consumer<PersonDto> breakIt) {
		assertExactMessage(I18nProperties.getValidationError(validationKey, captionArgument), breakIt);
	}

	private void assertExactMessage(String expectedMessage, Consumer<PersonDto> breakIt) {
		PersonDto person = validPerson();
		breakIt.accept(person);

		ValidationRuntimeException e = assertThrows(ValidationRuntimeException.class, () -> getPersonFacade().validate(person));

		assertEquals(expectedMessage, e.getMessage());
		assertNull(e.getPropertyErrors());
	}
}
