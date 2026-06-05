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

		// QUANTITATIVE_BUFFY_COAT produces QUALITATIVE + NUMERIC + TEXT, so those fields must round-trip.
		final PathogenTestDto test = creator.buildPathogenTestDto(rdcf, user, sample, caze.getDisease(), testDateTime);
		test.setTestType(PathogenTestType.QUANTITATIVE_BUFFY_COAT);
		test.setTestResult(PathogenTestResultType.POSITIVE);
		test.setQuantitativeValue(24.0f);
		test.setQuantitativeUnit("copies/mL");
		test.setQuantitativeText("Trophozoites seen");

		final PathogenTestDto reloaded = getPathogenTestFacade().savePathogenTest(test);

		assertEquals(24.0f, reloaded.getQuantitativeValue());
		assertEquals("copies/mL", reloaded.getQuantitativeUnit());
		assertEquals("Trophozoites seen", reloaded.getQuantitativeText());

		// Partially-populated test (only a text result, as for Sanger/WGS) round-trips with the rest null.
		final PathogenTestDto textOnly = creator.buildPathogenTestDto(rdcf, user, sample, caze.getDisease(), testDateTime);
		textOnly.setTestType(PathogenTestType.SANGER_SEQUENCING);
		textOnly.setTestResult(PathogenTestResultType.NOT_APPLICABLE);
		textOnly.setQuantitativeText("Measles genotype B3");

		final PathogenTestDto reloadedTextOnly = getPathogenTestFacade().savePathogenTest(textOnly);
		assertEquals("Measles genotype B3", reloadedTextOnly.getQuantitativeText());
		assertNull(reloadedTextOnly.getQuantitativeValue());
		assertNull(reloadedTextOnly.getQuantitativeUnit());
		assertNull(reloadedTextOnly.getQuantitativeBoolean());
		assertNull(reloadedTextOnly.getSmearGrade());
		assertNull(reloadedTextOnly.getWesternBlotInterpretation());
	}

	@Test
	public void testQuantitativeResultFieldsClearedOnSaveForMismatchedTestType() {

		final RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createSurveillanceSupervisor(rdcf);
		final PersonDto person = creator.createPerson();
		final CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		final SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		// A method declaring only TEXT (Genotyping) must not persist numeric/boolean/smear/Western-Blot
		// values, even when the DTO carries them - e.g. left over after changing the test type.
		final PathogenTestDto textType = creator.buildPathogenTestDto(rdcf, user, sample, caze.getDisease(), testDateTime);
		textType.setTestType(PathogenTestType.GENOTYPING);
		textType.setTestResult(PathogenTestResultType.NOT_APPLICABLE);
		textType.setQuantitativeText("M. tuberculosis L4 Euro-American");
		textType.setQuantitativeValue(24.0f);
		textType.setQuantitativeUnit("copies/mL");
		textType.setQuantitativeBoolean(de.symeda.sormas.api.utils.YesNoUnknown.YES);
		textType.setSmearGrade(de.symeda.sormas.api.sample.SmearGrade.THREE_PLUS);
		textType.setWesternBlotInterpretation(de.symeda.sormas.api.sample.WesternBlotInterpretation.POSITIVE);

		final PathogenTestDto reloaded = getPathogenTestFacade().savePathogenTest(textType);

		assertEquals("M. tuberculosis L4 Euro-American", reloaded.getQuantitativeText());
		assertNull(reloaded.getQuantitativeValue());
		assertNull(reloaded.getQuantitativeUnit());
		assertNull(reloaded.getQuantitativeBoolean());
		assertNull(reloaded.getSmearGrade());
		assertNull(reloaded.getWesternBlotInterpretation());

		// Changing the test type to one that no longer produces TEXT clears the previously stored text too.
		reloaded.setTestType(PathogenTestType.ACID_FAST_STAIN);
		reloaded.setTestResult(PathogenTestResultType.NOT_APPLICABLE);
		reloaded.setSmearGrade(de.symeda.sormas.api.sample.SmearGrade.TWO_PLUS);

		final PathogenTestDto retyped = getPathogenTestFacade().savePathogenTest(reloaded);

		assertEquals(de.symeda.sormas.api.sample.SmearGrade.TWO_PLUS, retyped.getSmearGrade());
		assertNull(retyped.getQuantitativeText());
		assertNull(retyped.getQuantitativeValue());
		assertNull(retyped.getQuantitativeUnit());
		assertNull(retyped.getQuantitativeBoolean());
		assertNull(retyped.getWesternBlotInterpretation());
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
