package de.symeda.sormas.backend.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.util.ValidationProblem;

public class CaseValidationProblemsTest extends AbstractBeanTest {

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
	public void validCaseHasNoProblems() {
		assertEquals(List.of(), collect(validCase()));
	}

	@Test
	public void reportsMissingResponsibleJurisdictionWithoutFailing() {
		List<ValidationProblem> problems = collect(caze -> {
			caze.setResponsibleRegion(null);
			caze.setResponsibleDistrict(null);
		});

		assertEquals(List.of(Validations.validResponsibleRegion, Validations.validResponsibleDistrict), keys(problems));
		assertEquals(List.of("responsibleRegion"), problems.get(0).getPropertyPaths());
		assertEquals(List.of("responsibleDistrict"), problems.get(1).getPropertyPaths());
	}

	@Test
	public void reportsMissingFacilityAndMissingDiseaseTogether() {
		List<ValidationProblem> problems = collect(caze -> {
			caze.setHealthFacility(null);
			caze.setFacilityType(null);
			caze.setDisease(null);
		});

		assertEquals(List.of(Validations.validFacility, Validations.validDisease), keys(problems));
		assertEquals(List.of("healthFacility"), problems.get(0).getPropertyPaths());
		assertEquals(List.of("disease"), problems.get(1).getPropertyPaths());
	}

	@Test
	public void reportsMissingReportingUserAndPointOfEntry() {
		List<ValidationProblem> problems = collect(caze -> {
			caze.setReportingUser(null);
			caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
			caze.setPointOfEntry(null);
		});

		assertEquals(List.of(Validations.validReportingUser, Validations.validPointOfEntry), keys(problems));
		assertEquals(List.of("reportingUser"), problems.get(0).getPropertyPaths());
		assertEquals(List.of("pointOfEntry"), problems.get(1).getPropertyPaths());
	}

	@Test
	public void namesBothColumnsWhenTheFacilityIsOutsideTheResponsibleDistrict() {
		List<ValidationProblem> problems = collect(caze -> {
			caze.setResponsibleCommunity(null);
			caze.setHealthFacility(otherRdcf.facility);
		});

		assertEquals(List.of(Validations.noFacilityInResponsibleDistrict), keys(problems));
		assertEquals(List.of("healthFacility", "responsibleDistrict"), problems.get(0).getPropertyPaths());
	}

	@Test
	public void namesBothColumnsWhenTheFacilityIsOutsideTheDistrict() {
		List<ValidationProblem> problems = collect(caze -> {
			caze.setRegion(rdcf.region);
			caze.setDistrict(rdcf.district);
			caze.setHealthFacility(otherRdcf.facility);
		});

		assertEquals(List.of(Validations.noFacilityInDistrict), keys(problems));
		assertEquals(List.of("healthFacility", "district"), problems.get(0).getPropertyPaths());
	}

	@Test
	public void reportsTheResponsibleCommunityMismatchOnce() {
		List<ValidationProblem> problems = collect(caze -> caze.setResponsibleCommunity(otherRdcf.community));

		assertEquals(List.of(Validations.noResponsibleCommunityInResponsibleDistrict), keys(problems));
		assertEquals(List.of("responsibleCommunity", "responsibleDistrict"), problems.get(0).getPropertyPaths());
	}

	@Test
	public void reportsTheResponsibleDistrictMismatchWithBothColumns() {
		List<ValidationProblem> problems = collect(caze -> {
			caze.setResponsibleCommunity(null);
			caze.setHealthFacility(rdcf.facility);
			caze.setResponsibleDistrict(otherRdcf.district);
		});

		assertEquals(Validations.noResponsibleDistrictInResponsibleRegion, problems.get(0).getValidationKey());
		assertEquals(List.of("responsibleDistrict", "responsibleRegion"), problems.get(0).getPropertyPaths());
	}

	@Test
	public void skipsDependentChecksWhenTheirInputsAreMissing() {
		List<ValidationProblem> problems = collect(caze -> {
			caze.setResponsibleRegion(null);
			caze.setResponsibleDistrict(null);
			caze.setResponsibleCommunity(null);
			caze.setHealthFacility(otherRdcf.facility);
		});

		assertEquals(List.of(Validations.validResponsibleRegion, Validations.validResponsibleDistrict), keys(problems));
	}

	@Test
	public void reportsDistrictAndCommunityMismatches() {
		List<ValidationProblem> problems = collect(caze -> {
			caze.setRegion(rdcf.region);
			caze.setDistrict(otherRdcf.district);
			caze.setCommunity(rdcf.community);
			caze.setHealthFacility(otherRdcf.facility);
		});

		assertEquals(List.of(Validations.noDistrictInRegion, Validations.noCommunityInDistrict), keys(problems));
		assertEquals(List.of("district", "region"), problems.get(0).getPropertyPaths());
		assertEquals(List.of("community", "district"), problems.get(1).getPropertyPaths());
	}

	@Test
	public void reportsFacilityTypeProblemsOnTheFacilityTypeColumn() {
		List<ValidationProblem> missing = collect(caze -> caze.setFacilityType(null));
		List<ValidationProblem> notAccommodation = collect(caze -> caze.setFacilityType(FacilityType.LABORATORY));

		assertEquals(List.of(Validations.noFacilityType), keys(missing));
		assertEquals(List.of("facilityType"), missing.get(0).getPropertyPaths());
		assertEquals(List.of(Validations.notAccomodationFacilityType), keys(notAccommodation));
		assertEquals(
			I18nProperties.getValidationError(Validations.notAccomodationFacilityType, FacilityType.LABORATORY),
			notAccommodation.get(0).getMessage());
		assertEquals(List.of("facilityType"), notAccommodation.get(0).getPropertyPaths());
	}

	@Test
	public void validateThrowsTheFirstCollectedMessage() {
		CaseDataDto caze = validCase();
		caze.setResponsibleRegion(null);
		caze.setDisease(null);

		ValidationRuntimeException e = assertThrows(ValidationRuntimeException.class, () -> getCaseFacade().validate(caze));

		assertEquals(I18nProperties.getValidationError(Validations.validResponsibleRegion), e.getMessage());
	}

	@Test
	public void validateStopsAtTheFirstProblemBeforeLookingUpUnknownReferences() {
		CaseDataDto caze = validCase();
		caze.setDisease(null);
		caze.setRegion(rdcf.region);
		caze.setDistrict(new DistrictReferenceDto(DataHelper.createUuid(), "Unknown", null));
		caze.setHealthFacility(new FacilityReferenceDto(DataHelper.createUuid(), "Unknown", null));

		ValidationRuntimeException e = assertThrows(ValidationRuntimeException.class, () -> getCaseFacade().validate(caze));

		assertEquals(I18nProperties.getValidationError(Validations.validDisease), e.getMessage());
	}

	private List<ValidationProblem> collect(Consumer<CaseDataDto> breakIt) {
		CaseDataDto caze = validCase();
		breakIt.accept(caze);
		return collect(caze);
	}

	private List<ValidationProblem> collect(CaseDataDto caze) {
		return getCaseFacade().collectValidationProblems(caze);
	}

	private List<String> keys(List<ValidationProblem> problems) {
		return problems.stream().map(ValidationProblem::getValidationKey).collect(Collectors.toList());
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
}
