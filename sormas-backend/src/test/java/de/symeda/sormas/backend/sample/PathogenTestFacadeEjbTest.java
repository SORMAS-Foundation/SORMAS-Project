package de.symeda.sormas.backend.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
