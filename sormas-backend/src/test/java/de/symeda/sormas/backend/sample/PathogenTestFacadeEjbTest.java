package de.symeda.sormas.backend.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class PathogenTestFacadeEjbTest extends AbstractBeanTest {

	private Date testDateTime = new Date();

	@Test
	public void testGetBySampleUuids() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto sample2 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto sample3 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		PathogenTestDto pathogenTest = creator.createPathogenTest(sample.toReference(), caze);
		PathogenTestDto pathogenTest2 = creator.createPathogenTest(sample.toReference(), caze);
		PathogenTestDto pathogenTest3 = creator.createPathogenTest(sample2.toReference(), caze);
		creator.createPathogenTest(sample3.toReference(), caze);

		List<PathogenTestDto> pathogenTests = getPathogenTestFacade().getBySampleUuids(Arrays.asList(sample.getUuid(), sample2.getUuid()));

		assertThat(pathogenTests, hasSize(3));
		assertThat(pathogenTests, contains(pathogenTest, pathogenTest2, pathogenTest3));
	}

	@Test
	public void testSaveAndUpdatePathogenTestAssociatedToCase() {

		final RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final PersonDto person = creator.createPerson();
		final CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		final SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		final PathogenTestDto newPathogenTest = creator.buildPathogenTestDto(rdcf, user, sample, caze.getDisease(), testDateTime);

		testSaveAndUpdatePathogenTest(newPathogenTest);
	}

	@Test
	public void testQuantitativeResultFieldsRoundTrip() {

		final RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final PersonDto person = creator.createPerson();
		final CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		final SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		// QUANTITATIVE_BUFFY_COAT produces QUALITATIVE + NUMERIC, so those fields must round-trip.
		final PathogenTestDto test = creator.buildPathogenTestDto(rdcf, user, sample, caze.getDisease(), testDateTime);
		test.setTestType(PathogenTestType.QUANTITATIVE_BUFFY_COAT);
		test.setTestResult(PathogenTestResultType.POSITIVE);
		test.setQuantitativeValue(24.0f);
		test.setQuantitativeUnit("copies/mL");

		final PathogenTestDto reloaded = getPathogenTestFacade().savePathogenTest(test);

		assertEquals(24.0f, reloaded.getQuantitativeValue());
		assertEquals("copies/mL", reloaded.getQuantitativeUnit());

		// Partially-populated test (only a smear grade, as for Acid-Fast Stain) round-trips with the rest null.
		final PathogenTestDto smearOnly = creator.buildPathogenTestDto(rdcf, user, sample, caze.getDisease(), testDateTime);
		smearOnly.setTestType(PathogenTestType.ACID_FAST_STAIN);
		smearOnly.setTestResult(PathogenTestResultType.NOT_APPLICABLE);
		smearOnly.setSmearGrade(de.symeda.sormas.api.sample.SmearGrade.THREE_PLUS);

		final PathogenTestDto reloadedSmearOnly = getPathogenTestFacade().savePathogenTest(smearOnly);
		assertEquals(de.symeda.sormas.api.sample.SmearGrade.THREE_PLUS, reloadedSmearOnly.getSmearGrade());
		assertNull(reloadedSmearOnly.getQuantitativeValue());
		assertNull(reloadedSmearOnly.getQuantitativeUnit());
		assertNull(reloadedSmearOnly.getQuantitativeBoolean());
		assertNull(reloadedSmearOnly.getWesternBlotInterpretation());
	}

	@Test
	public void testQuantitativeResultFieldsClearedOnSaveForMismatchedTestType() {

		final RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final PersonDto person = creator.createPerson();
		final CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		final SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		// A method declaring only SMEAR_GRADE (Acid-Fast Stain) must not persist numeric/boolean/Western-Blot
		// values, even when the DTO carries them - e.g. left over after changing the test type.
		final PathogenTestDto smearType = creator.buildPathogenTestDto(rdcf, user, sample, caze.getDisease(), testDateTime);
		smearType.setTestType(PathogenTestType.ACID_FAST_STAIN);
		smearType.setTestResult(PathogenTestResultType.NOT_APPLICABLE);
		smearType.setSmearGrade(de.symeda.sormas.api.sample.SmearGrade.THREE_PLUS);
		smearType.setQuantitativeValue(24.0f);
		smearType.setQuantitativeUnit("copies/mL");
		smearType.setQuantitativeBoolean(de.symeda.sormas.api.utils.YesNoUnknown.YES);
		smearType.setWesternBlotInterpretation(de.symeda.sormas.api.sample.WesternBlotInterpretation.POSITIVE);

		final PathogenTestDto reloaded = getPathogenTestFacade().savePathogenTest(smearType);

		assertEquals(de.symeda.sormas.api.sample.SmearGrade.THREE_PLUS, reloaded.getSmearGrade());
		assertNull(reloaded.getQuantitativeValue());
		assertNull(reloaded.getQuantitativeUnit());
		assertNull(reloaded.getQuantitativeBoolean());
		assertNull(reloaded.getWesternBlotInterpretation());

		// Changing the test type to one that no longer produces a smear grade clears the previously stored value.
		reloaded.setTestType(PathogenTestType.PCR_RT_PCR);
		reloaded.setTestResult(PathogenTestResultType.POSITIVE);
		reloaded.setQuantitativeValue(12.0f);
		reloaded.setQuantitativeUnit("IU/mL");

		final PathogenTestDto retyped = getPathogenTestFacade().savePathogenTest(reloaded);

		assertEquals(12.0f, retyped.getQuantitativeValue());
		assertNull(retyped.getSmearGrade());
		assertNull(retyped.getQuantitativeBoolean());
		assertNull(retyped.getWesternBlotInterpretation());
	}

	@Test
	public void testAntibioticSusceptibilityHasNoResultValueType() {
		// ANTIBIOTIC_SUSCEPTIBILITY's real result is the drug-susceptibility grid, not a value-type-bearing
		// field. Its @ResultValueTypeRel is explicitly empty, so saving it with NOT_APPLICABLE must round-trip
		// cleanly and the empty-set branch in fillOrBuildEntity must null every quantitative field.

		final RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final PersonDto person = creator.createPerson();
		final CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		final SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		final PathogenTestDto ast = creator.buildPathogenTestDto(rdcf, user, sample, caze.getDisease(), testDateTime);
		ast.setTestType(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY);
		ast.setTestResult(PathogenTestResultType.NOT_APPLICABLE);
		// Carry stale values to verify the empty-ResultValueTypeRel set clears every quantitative field on save.
		ast.setQuantitativeValue(99.0f);
		ast.setQuantitativeUnit("copies/mL");
		ast.setQuantitativeBoolean(de.symeda.sormas.api.utils.YesNoUnknown.YES);
		ast.setSmearGrade(de.symeda.sormas.api.sample.SmearGrade.THREE_PLUS);
		ast.setWesternBlotInterpretation(de.symeda.sormas.api.sample.WesternBlotInterpretation.POSITIVE);

		final PathogenTestDto reloaded = getPathogenTestFacade().savePathogenTest(ast);

		assertEquals(PathogenTestResultType.NOT_APPLICABLE, reloaded.getTestResult());
		assertNull(reloaded.getQuantitativeValue());
		assertNull(reloaded.getQuantitativeUnit());
		assertNull(reloaded.getQuantitativeBoolean());
		assertNull(reloaded.getSmearGrade());
		assertNull(reloaded.getWesternBlotInterpretation());
	}

	@Test
	public void testSaveAndUpdatePathogenTestAssociatedToContact() {

		final RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final PersonDto person = creator.createPerson();
		final CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		final ContactDto contact = creator.createContact(user.toReference(), person.toReference(), caze);
		final SampleDto sample =
			creator.createSample(contact.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);

		final PathogenTestDto newPathogenTest = creator.buildPathogenTestDto(rdcf, user, sample, contact.getDisease(), testDateTime);

		testSaveAndUpdatePathogenTest(newPathogenTest);
	}

	@Test
	public void testSaveAndUpdatePathogenTestAssociatedToBothCaseAndContact() {

		final RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final PersonDto person = creator.createPerson();
		final CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		final ContactDto contact = creator.createContact(user.toReference(), person.toReference(), caze);
		final SampleDto sample =
			creator.createSample(contact.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);

		final CaseDataDto caseDataDto = CaseDataDto.buildFromContact(contact);
		caseDataDto.setResponsibleRegion(new RegionReferenceDto(rdcf.region.getUuid(), null, null));
		caseDataDto.setResponsibleDistrict(new DistrictReferenceDto(rdcf.district.getUuid(), null, null));
		caseDataDto.setFacilityType(getFacilityFacade().getByUuid(rdcf.facility.getUuid()).getType());
		caseDataDto.setHealthFacility(new FacilityReferenceDto(rdcf.facility.getUuid(), null, null));
		caseDataDto.setReportingUser(user.toReference());
		final CaseDataDto caseConvertedFromContact = getCaseFacade().save(caseDataDto);

		getCaseFacade().setSampleAssociations(contact.toReference(), caseConvertedFromContact.toReference());

		final PathogenTestDto newPathogenTest = creator.buildPathogenTestDto(rdcf, user, sample, caseConvertedFromContact.getDisease(), testDateTime);

		testSaveAndUpdatePathogenTest(newPathogenTest);
	}

	@Test
	public void testCaseClassificationChangeOnPathogenTestActions() {
		final RDCF rdcf = creator.createRDCF();
		final UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf, c -> {
			c.setDisease(Disease.CORONAVIRUS);
			c.getSymptoms().setDifficultyBreathing(SymptomState.YES);
		});

		final SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setPathogenTestResult(PathogenTestResultType.POSITIVE);
		});
		PathogenTestDto test = creator.createPathogenTest(sample.toReference(), user.toReference(), p -> {
			p.setTestedDisease(Disease.CORONAVIRUS);
			p.setLab(rdcf.facility);
			p.setTestType(PathogenTestType.PCR_RT_PCR);
			p.setTestResult(PathogenTestResultType.POSITIVE);
			p.setTestResultVerified(true);
		});

		assertThat(getCaseFacade().getCaseDataByUuid(caze.getUuid()).getCaseClassification(), is(CaseClassification.CONFIRMED));

		getPathogenTestFacade().deletePathogenTest(test.getUuid(), new DeletionDetails());
		assertThat(getCaseFacade().getCaseDataByUuid(caze.getUuid()).getCaseClassification(), is(CaseClassification.SUSPECT));
	}

	@Test
	public void testCaseClassificationChangeOnPathogenTestActionsWithNoCaseRights() {
		final RDCF rdcf = creator.createRDCF();
		final UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf, c -> {
			c.setDisease(Disease.CORONAVIRUS);
			c.getSymptoms().setDifficultyBreathing(SymptomState.YES);
		});

		final SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setPathogenTestResult(PathogenTestResultType.POSITIVE);
		});

		UserDto noCaseUser = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			null,
			"NoCase",
			"Roghts",
			"nocase",
			JurisdictionLevel.DISTRICT,
			UserRight.PATHOGEN_TEST_CREATE,
			UserRight.PATHOGEN_TEST_DELETE,
			UserRight.SAMPLE_VIEW);
		loginWith(noCaseUser);

		PathogenTestDto test = creator.createPathogenTest(sample.toReference(), user.toReference(), p -> {
			p.setTestedDisease(Disease.CORONAVIRUS);
			p.setLab(rdcf.facility);
			p.setTestType(PathogenTestType.PCR_RT_PCR);
			p.setTestResult(PathogenTestResultType.POSITIVE);
			p.setTestResultVerified(true);
		});

		assertThat(getCaseFacade().getCaseDataByUuid(caze.getUuid()).getCaseClassification(), is(CaseClassification.CONFIRMED));

		getPathogenTestFacade().deletePathogenTest(test.getUuid(), new DeletionDetails());
		assertThat(getCaseFacade().getCaseDataByUuid(caze.getUuid()).getCaseClassification(), is(CaseClassification.SUSPECT));
	}

	private void testSaveAndUpdatePathogenTest(PathogenTestDto newPathogenTest) {

		final PathogenTestDto savedPathogenTest = getPathogenTestFacade().savePathogenTest(newPathogenTest);
		assertNotNull(savedPathogenTest);
		assertEquals(Disease.EVD, savedPathogenTest.getTestedDisease());
		assertEquals(PathogenTestType.ISOLATION, savedPathogenTest.getTestType());
		assertEquals(PathogenTestResultType.PENDING, savedPathogenTest.getTestResult());
		assertEquals("all bad!", savedPathogenTest.getTestResultText());
		assertEquals(testDateTime, savedPathogenTest.getTestDateTime());
		assertFalse(savedPathogenTest.getTestResultVerified());

		savedPathogenTest.setTestResultVerified(true);
		savedPathogenTest.setTestResultText("all good!");
		savedPathogenTest.setTestType(PathogenTestType.OTHER);
		savedPathogenTest.setTestResult(PathogenTestResultType.POSITIVE);

		final PathogenTestDto updatedPathogen = getPathogenTestFacade().savePathogenTest(savedPathogenTest);
		assertNotNull(updatedPathogen);
		assertEquals(Disease.EVD, updatedPathogen.getTestedDisease());
		assertEquals(PathogenTestType.OTHER, updatedPathogen.getTestType());
		assertEquals(PathogenTestResultType.POSITIVE, updatedPathogen.getTestResult());
		assertEquals("all good!", updatedPathogen.getTestResultText());
		assertEquals(testDateTime, updatedPathogen.getTestDateTime());
		assertTrue(updatedPathogen.getTestResultVerified());
	}

	@Test
	public void testEmptyResultDefaultsToPendingWhenResultNotRequired() {
		final RDCF rdcf = creator.createRDCF();
		final UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf);
		final SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		// Result is required by default (#13948 issue #13958): an empty result must be rejected.
		final PathogenTestDto requiredTest = creator.buildPathogenTestDto(rdcf, user, sample, caze.getDisease(), testDateTime);
		requiredTest.setTestResult(null);
		assertThrows(
			de.symeda.sormas.api.utils.ValidationRuntimeException.class,
			() -> getPathogenTestFacade().savePathogenTest(requiredTest));

		// Once the administrator turns the requirement off, an empty result is allowed and defaults to PENDING.
		getFeatureConfigurationFacade().setServerFeatureEnabled(FeatureType.PATHOGEN_TEST_RESULT_REQUIRED, false);

		final PathogenTestDto optionalTest = creator.buildPathogenTestDto(rdcf, user, sample, caze.getDisease(), testDateTime);
		optionalTest.setTestResult(null);
		final PathogenTestDto saved = getPathogenTestFacade().savePathogenTest(optionalTest);
		assertEquals(PathogenTestResultType.PENDING, saved.getTestResult());
	}
}
