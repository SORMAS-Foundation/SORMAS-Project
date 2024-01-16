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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.dashboard.sample.MapSampleDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
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
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class SampleFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetIndexList() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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

		// Referenced user has to find his samples
		loginWith(user);
		List<SampleIndexDto> result = getSampleFacade().getIndexList(new SampleCriteria(), 0, 100, null);
		assertThat(
			result.stream().map(e -> e.getUuid()).collect(Collectors.toList()),
			containsInAnyOrder(sample.getUuid(), referredSample.getUuid()));
	}

	@Test
	public void testCount() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
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
		UserDto user = useNationalAdminLogin();
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
			rdcf);

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
		assertEquals(cazeSample.getUuid(), sample11.getUuid());
		assertEquals(caze.getUuid(), sample11.getAssociatedCase().getUuid());
		assertTrue(sample11.getAssociatedCase().getCaption().startsWith("Case PERSON1"));

		final SampleIndexDto sample12 = sampleList1.get(1);
		assertEquals(sample.getUuid(), sample12.getUuid());
		assertEquals(contact.getUuid(), sample12.getAssociatedContact().getUuid());
		assertTrue(sample12.getAssociatedContact().getCaption().startsWith("Contact PERSON2"));

		final SampleIndexDto sample13 = sampleList1.get(2);
		assertEquals(referredSample.getUuid(), sample13.getUuid());
		assertEquals(contact.getUuid(), sample13.getAssociatedContact().getUuid());
		assertTrue(sample13.getAssociatedContact().getCaption().startsWith("Contact PERSON2"));

		final SampleIndexDto sample14 = sampleList1.get(3);
		assertEquals(sampleOfEventParticipant.getUuid(), sample14.getUuid());
		assertEquals(eventParticipant.getUuid(), sample14.getAssociatedEventParticipant().getUuid());
		assertEquals(rdcf.district.getCaption(), sample14.getDistrict());

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
	public void testGetIndexListBySampleAssociationTypeAndRestrictedAccessToAssignedEntities() {

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = useNationalAdminLogin();
		PersonDto cazePerson = creator.createPerson("Case", "Person1");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		SampleDto cazeSample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		PersonDto contactPerson = creator.createPerson("Contact", "Person2");
		ContactDto contact = creator.createContact(user.toReference(), contactPerson.toReference(), caze);
		cazeSample.setSampleDateTime(DateHelper.subtractDays(new Date(), 5));
		getSampleFacade().saveSample(cazeSample);
		SampleDto sample = creator.createSample(
			contact.toReference(),
			DateHelper.subtractDays(new Date(), 4),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			rdcf.facility);
		assertEquals(2, getSampleFacade().getIndexList(new SampleCriteria(), 0, 100, null).size());

		UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities =
			creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertTrue(getCurrentUserService().hasRestrictedAccessToAssignedEntities());
		assertEquals(0, getSampleFacade().getIndexList(new SampleCriteria(), 0, 100, null).size());

		loginWith(user);
		caze.setSurveillanceOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getCaseFacade().save(caze);
		contact.setContactOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getContactFacade().save(contact);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(2, getSampleFacade().getIndexList(new SampleCriteria(), 0, 100, null).size());
	}

	@Test
	public void testGetIndexListForCaseConvertedFromContact() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		caseDataDto.setFacilityType(FacilityType.HOSPITAL);
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
		assertEquals(sample.getUuid(), sample11.getUuid());
		assertEquals(caseConvertedFromContact.getUuid(), sample11.getAssociatedCase().getUuid());
		assertEquals(contact.getUuid(), sample11.getAssociatedContact().getUuid());

		final SampleIndexDto sample12 = samplesOfConvertedCase.get(1);
		assertEquals(referredSample.getUuid(), sample12.getUuid());
		assertEquals(contact.getUuid(), sample12.getAssociatedContact().getUuid());
		assertEquals(caseConvertedFromContact.getUuid(), sample11.getAssociatedCase().getUuid());
	}

	@Test
	public void testSampleDeletion() {

		Date since = new Date();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
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
		assertNotNull(getPathogenTestFacade().getByUuid(sampleTest.getUuid()));

		getSampleFacade().delete(sample.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));

		// Sample and pathogen test should be marked as deleted
		assertTrue(getSampleFacade().getDeletedUuidsSince(since).contains(sample.getUuid()));
		assertTrue(getPathogenTestFacade().getDeletedUuidsSince(since).contains(sampleTest.getUuid()));
		assertEquals(DeletionReason.OTHER_REASON, getSampleFacade().getSampleByUuid(sample.getUuid()).getDeletionReason());
		assertEquals("test reason", getSampleFacade().getSampleByUuid(sample.getUuid()).getOtherDeletionReason());
	}

	@Test
	public void testAllSamplesDeletionWithOneAdditionalTest() {

		Date since = new Date();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		assertNotNull(getPathogenTestFacade().getByUuid(firstSamplePathogenTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(firstSampleAdditionalTest.getUuid()));

		getSampleFacade()
			.delete(Arrays.asList(firstSample.getUuid(), secondSample.getUuid()), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));

		// Sample and pathogen test should be marked as deleted, additional test should be deleted
		List<String> sampleUuids = getSampleFacade().getDeletedUuidsSince(since);
		assertTrue(sampleUuids.contains(firstSample.getUuid()));
		assertTrue(sampleUuids.contains(secondSample.getUuid()));
		assertTrue(getPathogenTestFacade().getDeletedUuidsSince(since).contains(firstSamplePathogenTest.getUuid()));
		assertNull(getAdditionalTestFacade().getByUuid(firstSampleAdditionalTest.getUuid()));
	}

	@Test
	public void testAllSamplesDeletionWithMultipleAdditionalTest() {

		Date since = new Date();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		assertNotNull(getPathogenTestFacade().getByUuid(secondSamplePathogenTest.getUuid()));
		assertNotNull(getPathogenTestFacade().getByUuid(thirdSamplePathogenTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(secondSampleAdditionalTest.getUuid()));
		assertNotNull(getAdditionalTestFacade().getByUuid(thirdSampleAdditionalTest.getUuid()));

		getSampleFacade()
			.delete(Arrays.asList(thirdSample.getUuid(), forthSample.getUuid()), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));

		// Sample and pathogen test should be marked as deleted, additional tests should be deleted
		List<String> sampleUuids = getSampleFacade().getDeletedUuidsSince(since);
		assertTrue(sampleUuids.contains(thirdSample.getUuid()));
		assertTrue(sampleUuids.contains(forthSample.getUuid()));
		assertTrue(getPathogenTestFacade().getDeletedUuidsSince(since).contains(secondSamplePathogenTest.getUuid()));
		assertTrue(getPathogenTestFacade().getDeletedUuidsSince(since).contains(thirdSamplePathogenTest.getUuid()));
		assertNull(getAdditionalTestFacade().getByUuid(secondSampleAdditionalTest.getUuid()));
		assertNull(getAdditionalTestFacade().getByUuid(thirdSampleAdditionalTest.getUuid()));
	}

	@Test
	public void testArchivedSampleNotGettingTransfered() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

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
		assertEquals(1, getPathogenTestFacade().getAllActivePathogenTestsAfter(null).size());
		assertEquals(1, getPathogenTestFacade().getAllActiveUuids().size());

		getCaseFacade().archive(caze.getUuid(), null);

		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 0
		assertEquals(0, getSampleFacade().getAllActiveSamplesAfter(null).size());
		assertEquals(0, getSampleFacade().getAllActiveUuids().size());
		assertEquals(0, getPathogenTestFacade().getAllActivePathogenTestsAfter(null).size());
		assertEquals(0, getPathogenTestFacade().getAllActiveUuids().size());

		getCaseFacade().dearchive(Collections.singletonList(caze.getUuid()), null);

		// getAllActiveSamples/getAllActiveSampleTests and getAllUuids should return length 1
		assertEquals(1, getSampleFacade().getAllActiveSamplesAfter(null).size());
		assertEquals(1, getSampleFacade().getAllActiveUuids().size());
		assertEquals(1, getPathogenTestFacade().getAllActivePathogenTestsAfter(null).size());
		assertEquals(1, getPathogenTestFacade().getAllActiveUuids().size());
	}

	@Test
	public void testGetByCaseUuids() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
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
		UserDto officer = creator.createSurveillanceOfficer(rdcf);
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
		criteria.sampleCriteria(new SampleCriteria().caze(caze.toReference()));

		criteria.setLabSampleId("case_sample_id");
		List<SampleDto> similarSamples = getSampleFacade().getSimilarSamples(criteria);
		MatcherAssert.assertThat(similarSamples, hasSize(1));

		// should return no samples for unknown lab sample id and missing date and material
		criteria.setLabSampleId("unknown_id");
		similarSamples = getSampleFacade().getSimilarSamples(criteria);
		MatcherAssert.assertThat(similarSamples, hasSize(0));

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
		SampleSimilarityCriteria contactSampleCriteria = new SampleSimilarityCriteria().sampleCriteria(new SampleCriteria().contact(contact));
		contactSampleCriteria.setSampleDateTime(sampleDateTime1);
		contactSampleCriteria.setSampleMaterial(SampleMaterial.BLOOD);

		List<SampleDto> contactSimilarSamples = getSampleFacade().getSimilarSamples(contactSampleCriteria);
		MatcherAssert.assertThat(contactSimilarSamples, hasSize(1));
		MatcherAssert.assertThat(contactSimilarSamples.get(0).getUuid(), is(contactSample.getUuid()));

		// event participant samples
		SampleSimilarityCriteria eventParticipantSampleCriteria =
			new SampleSimilarityCriteria().sampleCriteria(new SampleCriteria().eventParticipant(eventParticipant.toReference()));
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
		UserDto officer = creator.createSurveillanceOfficer(rdcf);
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

		getSampleFacade().delete(sample2.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));

		result = getSampleFacade().getByLabSampleId(labSampleId);
		MatcherAssert.assertThat(result, hasSize(1));
		MatcherAssert.assertThat(result, contains(equalTo(sample)));
	}

	@Test
	public void testSaveSampleWithUnexistingCase() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto cazePerson = creator.createPerson();

		CaseDataDto unexistingCase = CaseDataDto.build(cazePerson.toReference(), Disease.DENGUE);

		SampleDto cazeSample = SampleDto.build(user.toReference(), unexistingCase.toReference());
		cazeSample.setSampleDateTime(new Date());
		cazeSample.setSampleMaterial(SampleMaterial.BLOOD);
		cazeSample.setSamplePurpose(SamplePurpose.INTERNAL);

		try {
			getSampleFacade().saveSample(cazeSample);
			fail();
		} catch (ValidationRuntimeException e) {
			assertEquals(I18nProperties.getValidationError(Validations.noCaseWithUuid), e.getMessage());
		}
	}

	@Test
	public void testSaveSampleWithUnexistingContact() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto cazePerson = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), rdcf);
		ContactDto unexistingContact = ContactDto.build(caze);

		SampleDto contactSample = SampleDto.build(user.toReference(), unexistingContact.toReference());
		contactSample.setSampleDateTime(new Date());
		contactSample.setSampleMaterial(SampleMaterial.BLOOD);
		contactSample.setSamplePurpose(SamplePurpose.INTERNAL);

		try {
			getSampleFacade().saveSample(contactSample);
			fail();
		} catch (ValidationRuntimeException e) {
			assertEquals(I18nProperties.getValidationError(Validations.noContactWithUuid), e.getMessage());
		}
	}

	@Test
	public void testSaveSampleWithUnexistingEventParticipant() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto cazePerson = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), rdcf);
		EventDto event = creator.createEvent(user.toReference());

		EventParticipantDto unexstingEventParticipant =
			EventParticipantDto.buildFromCase(caze.toReference(), cazePerson, event.toReference(), user.toReference());

		SampleDto eventParticipantSample = SampleDto.build(user.toReference(), unexstingEventParticipant.toReference());
		eventParticipantSample.setSampleDateTime(new Date());
		eventParticipantSample.setSampleMaterial(SampleMaterial.BLOOD);
		eventParticipantSample.setSamplePurpose(SamplePurpose.INTERNAL);

		try {
			getSampleFacade().saveSample(eventParticipantSample);
			fail();
		} catch (ValidationRuntimeException e) {
			assertEquals(I18nProperties.getValidationError(Validations.noEventParticipantWithUuid), e.getMessage());
		}
	}

	@Test
	public void testSaveSampleWithUnexistingLaboratory() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto cazePerson = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), rdcf);

		SampleDto cazeSample = SampleDto.build(user.toReference(), caze.toReference());
		cazeSample.setSampleDateTime(new Date());
		cazeSample.setSampleMaterial(SampleMaterial.BLOOD);
		cazeSample.setSamplePurpose(SamplePurpose.INTERNAL);

		FacilityDto unexistingLaboratory = FacilityDto.build();
		cazeSample.setLab(unexistingLaboratory.toReference());

		try {
			getSampleFacade().saveSample(cazeSample);
			fail();
		} catch (ValidationRuntimeException e) {
			assertEquals(I18nProperties.getValidationError(Validations.noLaboratoryWithUuid), e.getMessage());
		}
	}

	@Test
	public void testSaveSampleWithUnexistingReportingUser() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = useSurveillanceOfficerLogin(rdcf);
		PersonDto cazePerson = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), rdcf);

		SampleDto sample = SampleDto.build(user.toReference(), caze.toReference());
		sample.setSampleDateTime(new Date());
		sample.setSampleMaterial(SampleMaterial.BLOOD);
		sample.setSamplePurpose(SamplePurpose.INTERNAL);

		UserDto unexistingUser = UserDto.build();
		sample.setReportingUser(unexistingUser.toReference());

		try {
			getSampleFacade().saveSample(sample);
			fail();
		} catch (ValidationRuntimeException e) {
			assertEquals(I18nProperties.getValidationError(Validations.noReportingUserWithUuid), e.getMessage());
		}
	}

	@Test
	public void testSamplesForActiveAndArchiveCases() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto person = creator.createPerson("New", "Person");
		CaseDataDto cazeActive = creator.createCase(user.toReference(), person.toReference(), rdcf);
		CaseDataDto cazeArchive = creator.createCase(user.toReference(), person.toReference(), rdcf);

		SampleDto sampleActive = creator.createSample(cazeActive.toReference(), user.toReference(), rdcf.facility);
		SampleDto sampleArchive = creator.createSample(cazeArchive.toReference(), user.toReference(), rdcf.facility);

		getCaseFacade().archive(cazeArchive.getUuid(), null);

		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria = caseCriteria.relevanceStatus(EntityRelevanceStatus.ARCHIVED);
		List<CaseIndexDto> caseIndexDtos = getCaseFacade().getIndexList(caseCriteria, null, null, null);
		assertEquals(1, caseIndexDtos.size());
		assertEquals(cazeArchive.getUuid(), caseIndexDtos.get(0).getUuid());

		SampleCriteria sampleCriteria = new SampleCriteria();
		List<SampleIndexDto> sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(2, sampleIndexDtos.size());

		sampleCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(1, sampleIndexDtos.size());
		assertEquals(sampleActive.getUuid(), sampleIndexDtos.get(0).getUuid());

		sampleCriteria.relevanceStatus(EntityRelevanceStatus.ARCHIVED);
		sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(1, sampleIndexDtos.size());
		assertEquals(sampleArchive.getUuid(), sampleIndexDtos.get(0).getUuid());
	}

	@Test
	public void testSamplesForActiveAndArchiveContacts() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto person = creator.createPerson("New", "Person");

		ContactDto contactActive = creator.createContact(user.toReference(), person.toReference());
		ContactDto contactArchive = creator.createContact(user.toReference(), person.toReference());

		SampleDto sampleActive = creator.createSample(contactActive.toReference(), user.toReference(), rdcf.facility, null);
		SampleDto sampleArchive = creator.createSample(contactArchive.toReference(), user.toReference(), rdcf.facility, null);

		getContactFacade().archive(contactArchive.getUuid(), null);

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria = contactCriteria.relevanceStatus(EntityRelevanceStatus.ARCHIVED);
		List<ContactIndexDto> contactIndexDtos = getContactFacade().getIndexList(contactCriteria, null, null, null);
		assertEquals(1, contactIndexDtos.size());
		assertEquals(contactArchive.getUuid(), contactIndexDtos.get(0).getUuid());

		SampleCriteria sampleCriteria = new SampleCriteria();
		List<SampleIndexDto> sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(2, sampleIndexDtos.size());

		sampleCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(1, sampleIndexDtos.size());
		assertEquals(sampleActive.getUuid(), sampleIndexDtos.get(0).getUuid());

		sampleCriteria.relevanceStatus(EntityRelevanceStatus.ARCHIVED);
		sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(1, sampleIndexDtos.size());
		assertEquals(sampleArchive.getUuid(), sampleIndexDtos.get(0).getUuid());
	}

	@Test
	public void testSamplesForActiveAndArchiveEventParticipant() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto person = creator.createPerson("New", "Person");

		EventDto eventDto = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantDtoActive = creator.createEventParticipant(eventDto.toReference(), person, user.toReference());
		EventParticipantDto eventParticipantDtoArchive = creator.createEventParticipant(eventDto.toReference(), person, user.toReference());

		SampleDto sampleActive = creator.createSample(eventParticipantDtoActive.toReference(), user.toReference(), rdcf.facility);
		SampleDto sampleArchive = creator.createSample(eventParticipantDtoArchive.toReference(), user.toReference(), rdcf.facility);

		getEventParticipantFacade().archive(eventParticipantDtoArchive.getUuid(), null);

		EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
		eventParticipantCriteria.setEvent(eventDto.toReference());
		eventParticipantCriteria = eventParticipantCriteria.relevanceStatus(EntityRelevanceStatus.ARCHIVED);
		List<EventParticipantIndexDto> eventParticipantIndexDtos =
			getEventParticipantFacade().getIndexList(eventParticipantCriteria, null, null, null);
		assertEquals(1, eventParticipantIndexDtos.size());
		assertEquals(eventParticipantDtoArchive.getUuid(), eventParticipantIndexDtos.get(0).getUuid());

		SampleCriteria sampleCriteria = new SampleCriteria();
		List<SampleIndexDto> sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(2, sampleIndexDtos.size());

		sampleCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(1, sampleIndexDtos.size());
		assertEquals(sampleActive.getUuid(), sampleIndexDtos.get(0).getUuid());

		sampleCriteria.relevanceStatus(EntityRelevanceStatus.ARCHIVED);
		sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(1, sampleIndexDtos.size());
		assertEquals(sampleArchive.getUuid(), sampleIndexDtos.get(0).getUuid());
	}

	@Test
	public void testSampleForActiveCaseAndArchiveContact() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto person = creator.createPerson("New", "Person");

		CaseDataDto cazeActive = creator.createCase(user.toReference(), person.toReference(), rdcf);
		ContactDto contactArchive = creator.createContact(user.toReference(), person.toReference());

		SampleDto sampleCaseActive = creator.createSample(cazeActive.toReference(), user.toReference(), rdcf.facility);

		SampleDto sampleContactArchive = creator.createSample(contactArchive.toReference(), user.toReference(), rdcf.facility, null);

		SampleDto sampleCaseAndContact = creator.createSample(contactArchive.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setAssociatedCase(cazeActive.toReference());
		});

		getContactFacade().archive(contactArchive.getUuid(), null);

		ContactCriteria contactCriteria = new ContactCriteria();
		contactCriteria = contactCriteria.relevanceStatus(EntityRelevanceStatus.ARCHIVED);
		List<ContactIndexDto> contactIndexDtos = getContactFacade().getIndexList(contactCriteria, null, null, null);
		assertEquals(1, contactIndexDtos.size());
		assertEquals(contactArchive.getUuid(), contactIndexDtos.get(0).getUuid());

		SampleCriteria sampleCriteria = new SampleCriteria();
		List<SampleIndexDto> sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(3, sampleIndexDtos.size());

		sampleCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(2, sampleIndexDtos.size());
		List<String> uuids = sampleIndexDtos.stream().map(s -> s.getUuid()).collect(Collectors.toList());
		assertTrue(uuids.contains(sampleCaseActive.getUuid()));
		assertTrue(uuids.contains(sampleCaseAndContact.getUuid()));

		sampleCriteria.relevanceStatus(EntityRelevanceStatus.ARCHIVED);
		sampleIndexDtos = getSampleFacade().getIndexList(sampleCriteria, null, null, null);
		assertEquals(1, sampleIndexDtos.size());
		assertEquals(sampleContactArchive.getUuid(), sampleIndexDtos.get(0).getUuid());
	}

	@Test
	public void testIsEditAllowedSampleWithActiveEntity() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto person = creator.createPerson("New", "Person");

		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());
		EventDto eventDto = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantDto = creator.createEventParticipant(eventDto.toReference(), person, user.toReference());

		SampleDto sampleCase = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto sampleContact = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		SampleDto sampleEventParticipant = creator.createSample(eventParticipantDto.toReference(), user.toReference(), rdcf.facility);

		Boolean editable = getSampleFacade().isEditAllowed(sampleCase.getUuid());
		assertTrue(editable);
		editable = getSampleFacade().isEditAllowed(sampleContact.getUuid());
		assertTrue(editable);
		editable = getSampleFacade().isEditAllowed(sampleEventParticipant.getUuid());
		assertTrue(editable);
	}

	@Test
	public void testIsEditAllowedSampleWithInactiveEntity() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto person = creator.createPerson("New", "Person");

		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());
		EventDto eventDto = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantDto = creator.createEventParticipant(eventDto.toReference(), person, user.toReference());

		SampleDto sampleCase = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto sampleContact = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		SampleDto sampleEventParticipant = creator.createSample(eventParticipantDto.toReference(), user.toReference(), rdcf.facility);

		getCaseFacade().archive(caze.getUuid(), null);
		getContactFacade().archive(contact.getUuid(), null);
		getEventParticipantFacade().archive(eventParticipantDto.getUuid(), null);

		FeatureConfigurationIndexDto indexFeatureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, false, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(indexFeatureConfiguration, FeatureType.EDIT_ARCHIVED_ENTITIES);

		assertFalse(getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EDIT_ARCHIVED_ENTITIES));
		Boolean editable = getSampleFacade().isEditAllowed(sampleCase.getUuid());
		assertFalse(editable);
		editable = getSampleFacade().isEditAllowed(sampleContact.getUuid());
		assertFalse(editable);
		editable = getSampleFacade().isEditAllowed(sampleEventParticipant.getUuid());
		assertFalse(editable);
	}

	@Test
	public void testIsEditAllowedSampleWithInactiveEntityAndFeaturePropTrue() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto person = creator.createPerson("New", "Person");

		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());
		EventDto eventDto = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantDto = creator.createEventParticipant(eventDto.toReference(), person, user.toReference());

		SampleDto sampleCase = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto sampleContact = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		SampleDto sampleEventParticipant = creator.createSample(eventParticipantDto.toReference(), user.toReference(), rdcf.facility);

		getCaseFacade().archive(caze.getUuid(), null);
		getContactFacade().archive(contact.getUuid(), null);
		getEventParticipantFacade().archive(eventParticipantDto.getUuid(), null);

		FeatureConfigurationIndexDto indexFeatureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(indexFeatureConfiguration, FeatureType.EDIT_ARCHIVED_ENTITIES);

		assertTrue(getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EDIT_ARCHIVED_ENTITIES));
		Boolean editable = getSampleFacade().isEditAllowed(sampleCase.getUuid());
		assertTrue(editable);
		editable = getSampleFacade().isEditAllowed(sampleContact.getUuid());
		assertTrue(editable);
		editable = getSampleFacade().isEditAllowed(sampleEventParticipant.getUuid());
		assertTrue(editable);
	}

	@Test
	public void testIsEditAllowedSampleWithActiveAndInactiveEntity() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto person = creator.createPerson("New", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());

		SampleDto sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setAssociatedCase(caze.toReference());
		});

		getContactFacade().archive(contact.getUuid(), null);

		Boolean editable = getSampleFacade().isEditAllowed(sample.getUuid());
		assertTrue(editable);
	}

	@Test
	public void testCountAndGetSamplesForMap() {
		UserDto user = creator.createSurveillanceSupervisor(creator.createRDCF());

		PersonDto personWithCoord = creator.createPerson("New", "Person", Sex.UNKNOWN, p -> {
			p.getAddress().setLongitude(45.342163);
			p.getAddress().setLatitude(15.491076);
		});

		CaseDataDto cazeWithPersonCoord = creator.createCase(user.toReference(), personWithCoord.toReference(), creator.createRDCF());
		creator.createSample(cazeWithPersonCoord.toReference(), user.toReference(), creator.createRDCF().facility);

		CaseDataDto caseWithCoord = creator.createCase(user.toReference(), creator.createPerson().toReference(), creator.createRDCF(), c -> {
			c.setReportLon(45.342163);
			c.setReportLat(15.491076);
		});
		creator.createSample(caseWithCoord.toReference(), user.toReference(), creator.createRDCF().facility);

		ContactDto contactWithParsonCoord = creator.createContact(user.toReference(), personWithCoord.toReference(), Disease.CORONAVIRUS);
		creator.createSample(
			contactWithParsonCoord.toReference(),
			new Date(),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			creator.createRDCF().facility);

		ContactDto contactWithCoord = creator.createContact(user.toReference(), creator.createPerson().toReference(), Disease.CORONAVIRUS, c -> {
			c.setReportLon(45.342163);
			c.setReportLat(15.491076);
		});
		creator.createSample(
			contactWithCoord.toReference(),
			new Date(),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			creator.createRDCF().facility);

		EventDto event = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantWithPersonCoord =
			creator.createEventParticipant(event.toReference(), personWithCoord, user.toReference());
		creator.createSample(
			eventParticipantWithPersonCoord.toReference(),
			new Date(),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			creator.createRDCF().facility);

		EventDto eventWithCoord = creator.createEvent(user.toReference(), Disease.CORONAVIRUS, e -> {
			e.setReportLon(45.342163);
			e.setReportLat(15.491076);
		});
		EventParticipantDto eventParticipantWithEventCoord =
			creator.createEventParticipant(eventWithCoord.toReference(), creator.createPerson(), user.toReference());
		creator.createSample(
			eventParticipantWithEventCoord.toReference(),
			new Date(),
			new Date(),
			user.toReference(),
			SampleMaterial.BLOOD,
			creator.createRDCF().facility);

		// sample coordinates not taken
		SampleDto sampleWithCoord = creator.createSample(
			creator.createCase(user.toReference(), creator.createPerson().toReference(), creator.createRDCF()).toReference(),
			user.toReference(),
			creator.createRDCF().facility,
			s -> {
				s.setReportLon(45.342163);
				s.setReportLat(15.491076);
			});

		// sample without GPS coordinates should not be counted
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), creator.createRDCF());
		SampleDto sampleWithoutCoord = creator.createSample(caze.toReference(), user.toReference(), creator.createRDCF().facility);

		Long count = getSampleDashboardFacade().countSamplesForMap(new SampleDashboardCriteria(), Collections.emptySet());
		List<MapSampleDto> samples = getSampleDashboardFacade().getSamplesForMap(new SampleDashboardCriteria(), Collections.emptySet());
		assertEquals(0, count);
		assertEquals(0, samples.size());

		count = getSampleDashboardFacade().countSamplesForMap(new SampleDashboardCriteria(), Collections.singleton(SampleAssociationType.CASE));
		samples = getSampleDashboardFacade().getSamplesForMap(new SampleDashboardCriteria(), Collections.singleton(SampleAssociationType.CASE));
		assertEquals(2, count);
		assertEquals(2, samples.size());

		count = getSampleDashboardFacade().countSamplesForMap(new SampleDashboardCriteria(), Collections.singleton(SampleAssociationType.CONTACT));
		samples = getSampleDashboardFacade().getSamplesForMap(new SampleDashboardCriteria(), Collections.singleton(SampleAssociationType.CONTACT));
		assertEquals(2, count);
		assertEquals(2, samples.size());

		count = getSampleDashboardFacade()
			.countSamplesForMap(new SampleDashboardCriteria(), Collections.singleton(SampleAssociationType.EVENT_PARTICIPANT));
		samples = getSampleDashboardFacade()
			.getSamplesForMap(new SampleDashboardCriteria(), Collections.singleton(SampleAssociationType.EVENT_PARTICIPANT));
		assertEquals(2, count);
		assertEquals(2, samples.size());

		count = getSampleDashboardFacade().countSamplesForMap(
			new SampleDashboardCriteria(),
			new HashSet<>(Arrays.asList(SampleAssociationType.CASE, SampleAssociationType.CONTACT)));
		samples = getSampleDashboardFacade()
			.getSamplesForMap(new SampleDashboardCriteria(), new HashSet<>(Arrays.asList(SampleAssociationType.CASE, SampleAssociationType.CONTACT)));
		assertEquals(4, count);
		assertEquals(4, samples.size());

		count = getSampleDashboardFacade().countSamplesForMap(
			new SampleDashboardCriteria(),
			new HashSet<>(Arrays.asList(SampleAssociationType.CASE, SampleAssociationType.CONTACT, SampleAssociationType.EVENT_PARTICIPANT)));
		samples = getSampleDashboardFacade().getSamplesForMap(
			new SampleDashboardCriteria(),
			new HashSet<>(Arrays.asList(SampleAssociationType.CASE, SampleAssociationType.CONTACT, SampleAssociationType.EVENT_PARTICIPANT)));
		assertEquals(6, count);
		assertEquals(6, samples.size());
	}
}
