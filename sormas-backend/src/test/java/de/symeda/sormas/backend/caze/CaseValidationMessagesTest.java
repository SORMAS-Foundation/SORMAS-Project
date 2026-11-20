package de.symeda.sormas.backend.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class CaseValidationMessagesTest extends AbstractBeanTest {

	private RDCF rdcf;
	private RDCF otherRdcf;
	private UserDto user;
	private PersonDto person;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		otherRdcf = creator.createRDCF("Other Region", "Other District", "Other Community", "Other Facility");
		user = creator.createNationalUser();
		person = creator.createPerson();
		loginWith(user);
	}

	@Test
	public void validCaseIsAccepted() {
		getCaseFacade().validate(validCase());
	}

	@Test
	public void missingReportingUser() {
		assertMessage(Validations.validReportingUser, caze -> caze.setReportingUser(null));
	}

	@Test
	public void missingResponsibleRegion() {
		assertMessage(Validations.validResponsibleRegion, caze -> caze.setResponsibleRegion(null));
	}

	@Test
	public void missingResponsibleDistrict() {
		assertMessage(Validations.validResponsibleDistrict, caze -> caze.setResponsibleDistrict(null));
	}

	@Test
	public void responsibleCommunityOutsideTheResponsibleDistrict() {
		assertMessage(Validations.noResponsibleCommunityInResponsibleDistrict, caze -> caze.setResponsibleCommunity(otherRdcf.community));
	}

	@Test
	public void missingHealthFacilityForAnInCountryCase() {
		assertMessage(Validations.validFacility, caze -> {
			caze.setHealthFacility(null);
			caze.setFacilityType(null);
		});
	}

	@Test
	public void missingHealthFacilityForACaseWithoutOrigin() {
		assertMessage(Validations.validFacility, caze -> {
			caze.setCaseOrigin(null);
			caze.setHealthFacility(null);
			caze.setFacilityType(null);
		});
	}

	@Test
	public void missingPointOfEntryForAPointOfEntryCase() {
		assertMessage(Validations.validPointOfEntry, caze -> {
			caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
			caze.setPointOfEntry(null);
		});
	}

	@Test
	public void missingDisease() {
		assertMessage(Validations.validDisease, caze -> caze.setDisease(null));
	}

	@Test
	public void responsibleDistrictOutsideTheResponsibleRegion() {
		assertMessage(Validations.noResponsibleDistrictInResponsibleRegion, caze -> {
			caze.setResponsibleCommunity(null);
			caze.setResponsibleDistrict(otherRdcf.district);
		});
	}

	@Test
	public void districtOutsideTheRegion() {
		assertMessage(Validations.noDistrictInRegion, caze -> {
			caze.setRegion(rdcf.region);
			caze.setDistrict(otherRdcf.district);
		});
	}

	@Test
	public void communityOutsideTheDistrict() {
		assertMessage(Validations.noCommunityInDistrict, caze -> {
			caze.setDistrict(rdcf.district);
			caze.setCommunity(otherRdcf.community);
		});
	}

	@Test
	public void missingFacilityTypeForAHealthFacility() {
		assertMessage(Validations.noFacilityType, caze -> caze.setFacilityType(null));
	}

	@Test
	public void facilityTypeThatIsNotAnAccommodation() {
		FacilityType notAccommodation = FacilityType.LABORATORY;
		assertEquals(false, notAccommodation.isAccommodation());

		ValidationRuntimeException e = thrownFor(caze -> caze.setFacilityType(notAccommodation));

		assertEquals(I18nProperties.getValidationError(Validations.notAccomodationFacilityType, notAccommodation), e.getMessage());
		assertNull(e.getPropertyErrors());
	}

	@Test
	public void facilityOutsideTheResponsibleDistrict() {
		assertMessage(Validations.noFacilityInResponsibleDistrict, caze -> {
			caze.setResponsibleCommunity(null);
			caze.setHealthFacility(otherRdcf.facility);
		});
	}

	@Test
	public void facilityOutsideTheResponsibleCommunity() {
		assertMessage(Validations.noFacilityInResponsibleCommunity, caze -> caze.setHealthFacility(otherRdcf.facility));
	}

	@Test
	public void facilityOutsideTheResponsibleRegion() {
		var strayFacility = facilityInRegionOnly(otherRdcf);

		assertMessage(Validations.noFacilityInResponsibleRegion, caze -> {
			caze.setResponsibleCommunity(null);
			caze.setHealthFacility(strayFacility);
		});
	}

	@Test
	public void facilityOutsideTheDistrict() {
		assertMessage(Validations.noFacilityInDistrict, caze -> {
			caze.setRegion(rdcf.region);
			caze.setDistrict(rdcf.district);
			caze.setHealthFacility(otherRdcf.facility);
		});
	}

	@Test
	public void facilityOutsideTheCommunity() {
		assertMessage(Validations.noFacilityInCommunity, caze -> {
			caze.setRegion(rdcf.region);
			caze.setDistrict(rdcf.district);
			caze.setCommunity(rdcf.community);
			caze.setHealthFacility(otherRdcf.facility);
		});
	}

	@Test
	public void facilityOutsideTheRegion() {
		var strayFacility = facilityInRegionOnly(otherRdcf);

		assertMessage(Validations.noFacilityInRegion, caze -> {
			caze.setRegion(rdcf.region);
			caze.setDistrict(rdcf.district);
			caze.setHealthFacility(strayFacility);
		});
	}

	@Test
	public void missingFacilityIsReportedBeforeMissingDisease() {
		assertMessage(Validations.validFacility, caze -> {
			caze.setHealthFacility(null);
			caze.setFacilityType(null);
			caze.setDisease(null);
		});
	}

	@Test
	public void missingReportingUserIsReportedBeforeMissingResponsibleRegion() {
		assertMessage(Validations.validReportingUser, caze -> {
			caze.setReportingUser(null);
			caze.setResponsibleRegion(null);
		});
	}

	private FacilityReferenceDto facilityInRegionOnly(RDCF target) {
		var region = getRegionService().getByUuid(target.region.getUuid());
		var facility = creator.createFacility("Stray Facility", FacilityType.HOSPITAL, region, null, null);
		return new FacilityReferenceDto(facility.getUuid(), facility.getName(), null);
	}

	private CaseDataDto validCase() {
		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.EVD);
		caze.setReportingUser(user.toReference());
		caze.setResponsibleRegion(rdcf.region);
		caze.setResponsibleDistrict(rdcf.district);
		caze.setResponsibleCommunity(rdcf.community);
		caze.setHealthFacility(rdcf.facility);
		caze.setFacilityType(FacilityType.HOSPITAL);
		return caze;
	}

	private ValidationRuntimeException thrownFor(Consumer<CaseDataDto> breakIt) {
		CaseDataDto caze = validCase();
		breakIt.accept(caze);
		return assertThrows(ValidationRuntimeException.class, () -> getCaseFacade().validate(caze));
	}

	private void assertMessage(String validationKey, Consumer<CaseDataDto> breakIt) {
		ValidationRuntimeException e = thrownFor(breakIt);

		assertEquals(I18nProperties.getValidationError(validationKey), e.getMessage());
		assertNull(e.getPropertyErrors());
	}
}
