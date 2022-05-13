/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.sample;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.common.DeleteDetails;
import de.symeda.sormas.api.common.DeleteReason;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.AdditionalTestingStatus;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class SampleFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetIndexList() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto referredSample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		sample.setReferredTo(referredSample.toReference());
		creator.createAdditionalTest(sample.toReference());
		creator.createAdditionalTest(sample.toReference());
		creator.createPathogenTest(sample.toReference(), caze);
		PathogenTestDto test = creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.CQ_VALUE_DETECTION,
			caze.getDisease(),
			new Date(),
			rdcf.facility,
			caze.getReportingUser(),
			PathogenTestResultType.PENDING,
			"",
			false);
		test.setCqValue(1.5F);
		getPathogenTestFacade().savePathogenTest(test);

		List<SampleIndexDto> sampleIndexDtos = getSampleFacade().getIndexList(new SampleCriteria(), 0, 100, null);

		assertEquals(2, sampleIndexDtos.size());

		// First sample should have an additional test
		assertEquals(AdditionalTestingStatus.PERFORMED, sampleIndexDtos.get(1).getAdditionalTestingStatus());

		assertEquals(PathogenTestType.CQ_VALUE_DETECTION, sampleIndexDtos.get(1).getTypeOfLastTest());
		assertTrue(sampleIndexDtos.get(1).getLastTestCqValue().equals(1.5F));
	}

	@Test
	public void testCount() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto referredSample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		sample.setReferredTo(referredSample.toReference());
		creator.createAdditionalTest(sample.toReference());
		creator.createAdditionalTest(sample.toReference());

		long count = getSampleFacade().count(new SampleCriteria());
		assertEquals(2, count);
	}

	@Test
	public void testGetIndexListBySampleAssociationType() {

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto cazePerson = creator.createPerson("Case", "Person1");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		PersonDto contactPerson = creator.createPerson("Contact", "Person2");
		ContactDto contact = creator.createContact(user.toReference(), contactPerson.toReference(), caze);
		SampleDto cazeSample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		cazeSample.setSampleDateTime(DateHelper.subtractDays(new Date(), 5));
		getSampleFacade().saveSample(cazeSample);
		SampleDto sample = creator.createSample(
			contact.toReference(),
			DateHelper.subtractDays(new Date(), 4),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			rdcf.facility);
		SampleDto referredSample = creator.createSample(
			contact.toReference(),
			DateHelper.subtractDays(new Date(), 3),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			rdcf.facility);
		sample.setReferredTo(referredSample.toReference());
		creator.createAdditionalTest(sample.toReference());

		EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Title",
			"Description",
			"First",
			"Name",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			user.toReference(),
			Disease.EVD,
			rdcf.district);

		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), cazePerson, user.toReference());
		SampleDto sampleOfEventParticipant = creator.createSample(
			eventParticipant.toReference(),
			DateHelper.subtractDays(new Date(), 2),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			rdcf.facility);

		long count = getSampleFacade().count(new SampleCriteria());
		assertEquals(4, count);

		final ArrayList<SortProperty> sortProperties = new ArrayList<>();
		sortProperties.add(new SortProperty(SampleIndexDto.SAMPLE_DATE_TIME));
		sortProperties.add(new SortProperty(SampleIndexDto.ASSOCIATED_EVENT_PARTICIPANT, false));
		final List<SampleIndexDto> sampleList1 = getSampleFacade().getIndexList(new SampleCriteria(), 0, 100, sortProperties);
		assertEquals(4, sampleList1.size());

		final SampleIndexDto sample11 = sampleList1.get(0);
		Assert.assertEquals(cazeSample.getUuid(), sample11.getUuid());
		Assert.assertEquals(caze.getUuid(), sample11.getAssociatedCase().getUuid());
		Assert.assertTrue(sample11.getAssociatedCase().getCaption().startsWith("Case PERSON1"));

		final SampleIndexDto sample12 = sampleList1.get(1);
		Assert.assertEquals(sample.getUuid(), sample12.getUuid());
		Assert.assertEquals(contact.getUuid(), sample12.getAssociatedContact().getUuid());
		Assert.assertTrue(sample12.getAssociatedContact().getCaption().startsWith("Contact PERSON2"));

		final SampleIndexDto sample13 = sampleList1.get(2);
		Assert.assertEquals(referredSample.getUuid(), sample13.getUuid());
		Assert.assertEquals(contact.getUuid(), sample13.getAssociatedContact().getUuid());
		Assert.assertTrue(sample13.getAssociatedContact().getCaption().startsWith("Contact PERSON2"));

		final SampleIndexDto sample14 = sampleList1.get(3);
		Assert.assertEquals(sampleOfEventParticipant.getUuid(), sample14.getUuid());
		Assert.assertEquals(eventParticipant.getUuid(), sample14.getAssociatedEventParticipant().getUuid());
		Assert.assertEquals(rdcf.district.getCaption(), sample14.getDistrict());

		assertEquals(2, getSampleFacade().count(new SampleCriteria().sampleAssociationType(SampleAssociationType.CONTACT)));
		assertEquals(
			2,
			getSampleFacade().getIndexList(new SampleCriteria().sampleAssociationType(SampleAssociationType.CONTACT), 0, 100, null).size());
		assertEquals(1, getSampleFacade().getIndexList(new SampleCriteria().sampleAssociationType(SampleAssociationType.CASE), 0, 100, null).size());
		assertEquals(
			1,
			getSampleFacade().getIndexList(new SampleCriteria().sampleAssociationType(SampleAssociationType.EVENT_PARTICIPANT), 0, 100, null).size());
	}

	@Test
	public void testGetIndexListForCaseConvertedFromContact() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person1");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		PersonDto contactPerson = creator.createPerson("Contact", "Person2");
		ContactDto contact = creator.createContact(user.toReference(), contactPerson.toReference(), caze);
		SampleDto cazeSample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		cazeSample.setSampleDateTime(DateHelper.subtractDays(new Date(), 5));
		getSampleFacade().saveSample(cazeSample);
		SampleDto sample = creator.createSample(
			contact.toReference(),
			DateHelper.subtractDays(new Date(), 4),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			rdcf.facility);
		SampleDto referredSample = creator.createSample(
			contact.toReference(),
			DateHelper.subtractDays(new Date(), 3),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			rdcf.facility);
		sample.setReferredTo(referredSample.toReference());
		creator.createAdditionalTest(sample.toReference());

		CaseDataDto caseDataDto = CaseDataDto.buildFromContact(contact);
		caseDataDto.setResponsibleRegion(new RegionReferenceDto(rdcf.region.getUuid(), null, null));
		caseDataDto.setResponsibleDistrict(new DistrictReferenceDto(rdcf.district.getUuid(), null, null));
		caseDataDto.setFacilityType(rdcf.facility.getType());
		caseDataDto.setHealthFacility(new FacilityReferenceDto(rdcf.facility.getUuid(), null, null));
		caseDataDto.setReportingUser(user.toReference());
		CaseDataDto caseConvertedFromContact = getCaseFacade().save(caseDataDto);

		getCaseFacade().setSampleAssociations(contact.toReference(), caseConvertedFromContact.toReference());

		final SampleCriteria samplesConnectedToConvertedCaseCriteria = new SampleCriteria().caze(caseConvertedFromContact.toReference());
		assertEquals(2, getSampleFacade().count(samplesConnectedToConvertedCaseCriteria));

		final ArrayList<SortProperty> sortProperties = new ArrayList<>();
		sortProperties.add(new SortProperty(SampleIndexDto.SAMPLE_DATE_TIME));
		sortProperties.add(new SortProperty(SampleIndexDto.ASSOCIATED_CONTACT));
		final List<SampleIndexDto> samplesOfConvertedCase =
			getSampleFacade().getIndexList(samplesConnectedToConvertedCaseCriteria, 0, 100, sortProperties);
		assertEquals(2, samplesOfConvertedCase.size());

		final SampleIndexDto sample11 = samplesOfConvertedCase.get(0);
		Assert.assertEquals(sample.getUuid(), sample11.getUuid());
		Assert.assertEquals(caseConvertedFromContact.getUuid(), sample11.getAssociatedCase().getUuid());
		Assert.assertEquals(contact.getUuid(), sample11.getAssociatedContact().getUuid());

		final SampleIndexDto sample12 = samplesOfConvertedCase.get(1);
		Assert.assertEquals(referredSample.getUuid(), sample12.getUuid());
		Assert.assertEquals(contact.getUuid(), sample12.getAssociatedContact().getUuid());
		Assert.assertEquals(caseConvertedFromContact.getUuid(), sample11.getAssociatedCase().getUuid());
	}

	@Test
	public void testSampleDeletion() {

		Date since = new Date();

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = getUserFacade().getByUserName("admin");
		String adminUuid = admin.getUuid();
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		PathogenTestDto sampleTest = creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.MICROSCOPY,
			caze.getDisease(),
			new Date(),
			rdcf.facility,
			user.toReference(),
			PathogenTestResultType.POSITIVE,
			"Positive",
			true);

		// Database should contain the created sample and sample test
		assertNotNull(getSampleFacade().getSampleByUuid(sample.getUuid()));
		assertNotNull(getSampleTestFacade().getByUuid(sampleTest.getUuid()));

		getSampleFacade().deleteSample(sample.toReference(), new DeleteDetails(DeleteReason.OTHER_REASON, null));

		// Sample and pathogen test should be marked as deleted
		assertTrue(getSampleFacade().getDeletedUuidsSince(since).contains(sample.getUuid()));
		assertTrue(getSampleTestFacade().getDeletedUuidsSince(since).contains(sampleTest.getUuid()));
	}

	@Test
	public void testAllSamplesDeletionWithOneAdditionalTest() {

		Date since = new Date();

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		//1st Case person
		PersonDto firstCazePerson = creator.createPerson("FirstCase", "FirstPerson");
		CaseDataDto firstCaze = creator.createCase(
			user.toReference(),
			firstCazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		SampleDto firstSample =
			creator.createSample(firstCaze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		SampleDto secondSample =
			creator.createSample(firstCaze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		PathogenTestDto firstSamplePathogenTest = creator.createPathogenTest(
			firstSample.toReference(),
			PathogenTestType.MICROSCOPY,
			firstCaze.getDisease(),
			new Date(),
			rdcf.facility,
			user.toReference(),
			PathogenTestResultType.POSITIVE,
			"Positive",
			true);
		AdditionalTestDto firstSampleAdditionalTest = creator.createAdditionalTest(firstSample.toReference());

		// Database should contain the created sample and sample test
		assertNotNull(getSampleTestFacade().getByUuid(firstSamplePathogenTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(firstSampleAdditionalTest.getUuid()));

		getSampleFacade().deleteAllSamples(Arrays.asList(firstSample.getUuid(), secondSample.getUuid()));

		// Sample and pathogen test should be marked as deleted, additional test should be deleted
		List<String> sampleUuids = getSampleFacade().getDeletedUuidsSince(since);
		assertTrue(sampleUuids.contains(firstSample.getUuid()));
		assertTrue(sampleUuids.contains(secondSample.getUuid()));
		assertTrue(getSampleTestFacade().getDeletedUuidsSince(since).contains(firstSamplePathogenTest.getUuid()));
		assertNull(getAdditionalTestFacade().getByUuid(firstSampleAdditionalTest.getUuid()));
	}

	@Test
	public void testAllSamplesDeletionWithMultipleAdditionalTest() {

		Date since = new Date();

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		PersonDto secondCazePerson = creator.createPerson("SecondCase", "SecondPerson");
		CaseDataDto secondCaze = creator.createCase(
			user.toReference(),
			secondCazePerson.toReference(),
			Disease.ACUTE_VIRAL_HEPATITIS,
			CaseClassification.NOT_CLASSIFIED,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		SampleDto thirdSample =
			creator.createSample(secondCaze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		SampleDto forthSample =
			creator.createSample(secondCaze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		PathogenTestDto secondSamplePathogenTest = creator.createPathogenTest(
			thirdSample.toReference(),
			PathogenTestType.RAPID_TEST,
			secondCaze.getDisease(),
			new Date(),
			rdcf.facility,
			user.toReference(),
			PathogenTestResultType.INDETERMINATE,
			"Indeterminate",
			true);
		PathogenTestDto thirdSamplePathogenTest = creator.createPathogenTest(
			forthSample.toReference(),
			PathogenTestType.CQ_VALUE_DETECTION,
			secondCaze.getDisease(),
			new Date(),
			rdcf.facility,
			user.toReference(),
			PathogenTestResultType.NOT_DONE,
			"Not done",
			true);
		AdditionalTestDto secondSampleAdditionalTest = creator.createAdditionalTest(thirdSample.toReference());
		AdditionalTestDto thirdSampleAdditionalTest = creator.createAdditionalTest(forthSample.toReference());

		// Database should contain the created sample, sample test and additional tests
		assertNotNull(getSampleTestFacade().getByUuid(secondSamplePathogenTest.getUuid()));
		assertNotNull(getSampleTestFacade().getByUuid(thirdSamplePathogenTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(secondSampleAdditionalTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(thirdSampleAdditionalTest.getUuid()));

		getSampleFacade().deleteAllSamples(Arrays.asList(thirdSample.getUuid(), forthSample.getUuid()));

		// Sample and pathogen test should be marked as deleted, additional tests should be deleted
		List<String> sampleUuids = getSampleFacade().getDeletedUuidsSince(since);
		assertTrue(sampleUuids.contains(thirdSample.getUuid()));
		assertTrue(sampleUuids.contains(forthSample.getUuid()));
		assertTrue(getSampleTestFacade().getDeletedUuidsSince(since).contains(secondSamplePathogenTest.getUuid()));
		assertTrue(getSampleTestFacade().getDeletedUuidsSince(since).contains(thirdSamplePathogenTest.getUuid()));
		assertNull(getAdditionalTestFacade().getByUuid(secondSampleAdditionalTest.getUuid()));
		assertNull(getAdditionalTestFacade().getByUuid(thirdSampleAdditionalTest.getUuid()));
	}

	@Test
	public void testArchivedSampleNotGettingTransfered() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.MICROSCOPY,
			caze.getDisease(),
			new Date(),
			rdcf.facility,
			user.toReference(),
			PathogenTestResultType.POSITIVE,
			"Positive",
			true);

		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 1
		assertEquals(1, getSampleFacade().getAllActiveSamplesAfter(null).size());
		assertEquals(1, getSampleFacade().getAllActiveUuids().size());
		assertEquals(1, getSampleTestFacade().getAllActivePathogenTestsAfter(null).size());
		assertEquals(1, getSampleTestFacade().getAllActiveUuids().size());

		getCaseFacade().archive(caze.getUuid(), null);

		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 0
		assertEquals(0, getSampleFacade().getAllActiveSamplesAfter(null).size());
		assertEquals(0, getSampleFacade().getAllActiveUuids().size());
		assertEquals(0, getSampleTestFacade().getAllActivePathogenTestsAfter(null).size());
		assertEquals(0, getSampleTestFacade().getAllActiveUuids().size());

		getCaseFacade().dearchive(Collections.singletonList(caze.getUuid()), null);

		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 1
		assertEquals(1, getSampleFacade().getAllActiveSamplesAfter(null).size());
		assertEquals(1, getSampleFacade().getAllActiveUuids().size());
		assertEquals(1, getSampleTestFacade().getAllActivePathogenTestsAfter(null).size());
		assertEquals(1, getSampleTestFacade().getAllActiveUuids().size());
	}

	@Test
	public void testGetByCaseUuids() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		CaseDataDto caze2 = creator.createCase(user.toReference(), person.toReference(), rdcf);
		CaseDataDto caze3 = creator.createCase(user.toReference(), person.toReference(), rdcf);

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto sample2 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto sample3 = creator.createSample(caze2.toReference(), user.toReference(), rdcf.facility);
		creator.createSample(caze3.toReference(), user.toReference(), rdcf.facility);

		List<SampleDto> samples = getSampleFacade().getByCaseUuids(Arrays.asList(caze.getUuid(), caze2.getUuid()));

		assertThat(samples, hasSize(3));
		assertThat(samples, contains(sample, sample2, sample3));
	}

	@Test
	public void testGetSimilarSamples() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER);
		CaseDataDto caze = creator.createCase(officer.toReference(), creator.createPerson().toReference(), rdcf);

		Date sampleDateTime1 = DateHelper.parseDate("11.02.2021", new SimpleDateFormat("dd.MM.yyyy"));
		creator.createSample(caze.toReference(), officer.toReference(), rdcf.facility, (s) -> {
			s.setLabSampleID("case_sample_id");
			s.setSampleDateTime(sampleDateTime1);
			s.setSampleMaterial(SampleMaterial.BLOOD);
		});

		Date sampleDateTime2 = DateHelper.parseDate("08.02.2021", new SimpleDateFormat("dd.MM.yyyy"));
		creator.createSample(caze.toReference(), officer.toReference(), rdcf.facility, (s) -> {
			s.setLabSampleID("case_sample_id_2");
			s.setSampleDateTime(sampleDateTime2);
			s.setSampleMaterial(SampleMaterial.BLOOD);
		});

		ContactReferenceDto contact = creator.createContact(officer.toReference(), creator.createPerson().toReference()).toReference();
		SampleDto contactSample =
			creator.createSample(contact, sampleDateTime1, new Date(), officer.toReference(), SampleMaterial.BLOOD, rdcf.facility);

		EventParticipantDto eventParticipant =
			creator.createEventParticipant(creator.createEvent(officer.toReference()).toReference(), creator.createPerson(), officer.toReference());
		SampleDto eventParticipantSample = creator
			.createSample(eventParticipant.toReference(), sampleDateTime1, new Date(), officer.toReference(), SampleMaterial.BLOOD, rdcf.facility);

		SampleSimilarityCriteria criteria = new SampleSimilarityCriteria();
		criteria.caze(caze.toReference());

		criteria.setLabSampleId("case_sample_id");
		List<SampleDto> similarSamples = getSampleFacade().getSimilarSamples(criteria);
		MatcherAssert.assertThat(similarSamples, hasSize(1));

		// should return all samples for unknown lab sample id and missing date and material
		criteria.setLabSampleId("unknown_id");
		similarSamples = getSampleFacade().getSimilarSamples(criteria);
		MatcherAssert.assertThat(similarSamples, hasSize(2));

		criteria.setSampleMaterial(SampleMaterial.BLOOD);

		criteria.setSampleDateTime(DateHelper.addDays(sampleDateTime2, 1));
		similarSamples = getSampleFacade().getSimilarSamples(criteria);
		MatcherAssert.assertThat(similarSamples, hasSize(2));

		criteria.setSampleDateTime(DateHelper.subtractDays(sampleDateTime2, 1));
		similarSamples = getSampleFacade().getSimilarSamples(criteria);
		MatcherAssert.assertThat(similarSamples, hasSize(1));

		criteria.setSampleDateTime(DateHelper.addDays(sampleDateTime1, 3));
		similarSamples = getSampleFacade().getSimilarSamples(criteria);
		MatcherAssert.assertThat(similarSamples, hasSize(0));

		criteria.setSampleDateTime(DateHelper.subtractDays(sampleDateTime2, 3));
		similarSamples = getSampleFacade().getSimilarSamples(criteria);
		MatcherAssert.assertThat(similarSamples, hasSize(0));

		// contact samples
		SampleSimilarityCriteria contactSampleCriteria = new SampleSimilarityCriteria().contact(contact);
		contactSampleCriteria.setSampleDateTime(sampleDateTime1);
		contactSampleCriteria.setSampleMaterial(SampleMaterial.BLOOD);

		List<SampleDto> contactSimilarSamples = getSampleFacade().getSimilarSamples(contactSampleCriteria);
		MatcherAssert.assertThat(contactSimilarSamples, hasSize(1));
		MatcherAssert.assertThat(contactSimilarSamples.get(0).getUuid(), is(contactSample.getUuid()));

		// event participant samples
		SampleSimilarityCriteria eventParticipantSampleCriteria = new SampleSimilarityCriteria().eventParticipant(eventParticipant.toReference());
		eventParticipantSampleCriteria.setSampleDateTime(sampleDateTime1);
		eventParticipantSampleCriteria.setSampleMaterial(SampleMaterial.BLOOD);

		List<SampleDto> eventParticipantSimilarSamples = getSampleFacade().getSimilarSamples(eventParticipantSampleCriteria);
		MatcherAssert.assertThat(eventParticipantSimilarSamples, hasSize(1));
		MatcherAssert.assertThat(eventParticipantSimilarSamples.get(0).getUuid(), is(eventParticipantSample.getUuid()));
	}

	@Test
	public void testGetByLabSampleId() {

		String labSampleId = "1234";
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto officer = creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER);
		CaseDataDto caze = creator.createCase(officer.toReference(), creator.createPerson().toReference(), rdcf);

		SampleDto sample = creator.createSample(caze.toReference(), officer.toReference(), rdcf.facility, (s) -> {
			s.setLabSampleID(labSampleId);
		});

		//create noise
		creator.createSample(caze.toReference(), officer.toReference(), rdcf.facility, (s) -> {
			s.setLabSampleID("some-other-id");
		});

		List<SampleDto> result = getSampleFacade().getByLabSampleId(null);
		assertTrue(result.isEmpty());

		result = getSampleFacade().getByLabSampleId(labSampleId);
		MatcherAssert.assertThat(result, hasSize(1));
		MatcherAssert.assertThat(result, contains(equalTo(sample)));

		SampleDto sample2 = creator.createSample(caze.toReference(), officer.toReference(), rdcf.facility, (s) -> {
			s.setLabSampleID(labSampleId);
		});

		result = getSampleFacade().getByLabSampleId(labSampleId);
		MatcherAssert.assertThat(result, hasSize(2));
		MatcherAssert.assertThat(result, containsInAnyOrder(equalTo(sample), equalTo(sample2)));

		getSampleFacade().deleteSample(sample2.toReference(), new DeleteDetails(DeleteReason.OTHER_REASON, null));

		result = getSampleFacade().getByLabSampleId(labSampleId);
		MatcherAssert.assertThat(result, hasSize(1));
		MatcherAssert.assertThat(result, contains(equalTo(sample)));
	}
}
