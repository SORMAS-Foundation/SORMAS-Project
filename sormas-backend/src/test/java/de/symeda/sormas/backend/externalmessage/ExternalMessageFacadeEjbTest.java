/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.externalmessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValue;

public class ExternalMessageFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetByReportIdWithCornerCaseInput() {
		String reportId = "123456789";
		creator.createExternalMessage((lm) -> lm.setReportId(reportId));

		List<ExternalMessageDto> list = getExternalMessageFacade().getByReportId(null);

		assertNotNull(list);
		assertTrue(list.isEmpty());

		list = getExternalMessageFacade().getByReportId("");

		assertNotNull(list);
		assertTrue(list.isEmpty());
	}

	@Test
	public void testGetByReportIdWithOneMessage() {

		String reportId = "123456789";
		creator.createExternalMessage((lm) -> lm.setReportId(reportId));

		// create noise
		creator.createExternalMessage(null);
		creator.createExternalMessage((lm) -> lm.setReportId("some-other-id"));

		List<ExternalMessageDto> list = getExternalMessageFacade().getByReportId(reportId);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		assertEquals(reportId, list.get(0).getReportId());
	}

	@Test
	public void testGetByUuid() {

		ExternalMessageDto labMessage = creator.createExternalMessage(null);

		ExternalMessageDto result = getExternalMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(result, equalTo(labMessage));
	}

	@Test
	public void testExistsForwardedLabMessageWith() {

		String reportId = "1234";

		// create noise
		creator.createExternalMessage((lm) -> lm.setStatus(ExternalMessageStatus.FORWARDED));

		assertFalse(getExternalMessageFacade().existsForwardedExternalMessageWith(reportId));
		assertFalse(getExternalMessageFacade().existsForwardedExternalMessageWith(null));

		creator.createExternalMessage((lm) -> lm.setReportId(reportId));

		assertFalse(getExternalMessageFacade().existsForwardedExternalMessageWith(reportId));

		ExternalMessageDto forwardedMessage = creator.createExternalMessage((lm) -> {
			lm.setReportId(reportId);
			lm.setStatus(ExternalMessageStatus.FORWARDED);
		});

		assertTrue(getExternalMessageFacade().existsForwardedExternalMessageWith(reportId));
	}

	@Test
	public void testExistsLabMessageForEntityCase() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));

		// create noise
		CaseDataDto noiseCaze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		creator.createSample(noiseCaze.toReference(), user.toReference(), rdcf.facility);

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));

		creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caze.toReference(), sample.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));

		// create additional matches
		creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caze.toReference(), sample.toReference());
		SampleDto sample2 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caze.toReference(), sample2.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));
	}

	@Test
	public void testExistsLabMessageForEntityContact() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());

		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));

		// create noise
		ContactDto noiseContact = creator.createContact(user.toReference(), person.toReference());
		creator.createSample(noiseContact.toReference(), user.toReference(), rdcf.facility, null);

		SampleDto sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));

		creator.createLabMessageWithTestReport(sample.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));

		// create additional matches
		creator.createLabMessageWithTestReport(sample.toReference());
		SampleDto sample2 = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		creator.createLabMessageWithTestReport(sample2.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));
	}

	@Test
	public void testExistsLabMessageForEntityEventParticipant() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();
		EventDto event = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());

		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));

		// create noise
		EventParticipantDto noiseEventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		creator.createSample(noiseEventParticipant.toReference(), user.toReference(), rdcf.facility);

		SampleDto sample = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);
		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));

		creator.createLabMessageWithTestReport(sample.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));

		// create additional matches
		creator.createLabMessageWithTestReport(sample.toReference());
		SampleDto sample2 = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);
		creator.createLabMessageWithTestReport(sample2.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));
	}

	@Test
	public void testCountAndIndexListDoesNotReturnMessagesLinkedToDeletedEntities() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();

		CaseDataDto externalMessageCase = creator.createCase(user.toReference(), person.toReference(), rdcf);
		SampleDto externalMessageSample = creator.createSample(
			creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf).toReference(),
			user.toReference(),
			rdcf.facility);

		CaseDataDto caseWithSample = creator.createCase(user.toReference(), person.toReference(), rdcf);
		SampleDto caseSample = creator.createSample(caseWithSample.toReference(), user.toReference(), rdcf.facility);

		ContactDto contactWithSample = creator.createContact(rdcf, user.toReference(), creator.createPerson().toReference());
		SampleDto contactSample = creator.createSample(contactWithSample.toReference(), user.toReference(), rdcf.facility, null);

		EventParticipantDto eventParticipantWithSample =
			creator.createEventParticipant(creator.createEvent(user.toReference()).toReference(), creator.createPerson(), user.toReference());
		SampleDto eventParticipantSample = creator.createSample(eventParticipantWithSample.toReference(), user.toReference(), rdcf.facility);

		EventDto eventToDelete = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantWithDeletedEvent =
			creator.createEventParticipant(eventToDelete.toReference(), creator.createPerson(), user.toReference());
		SampleDto eventParticipantSampleForDeletedEvent =
			creator.createSample(eventParticipantWithDeletedEvent.toReference(), user.toReference(), rdcf.facility);

		ExternalMessageDto messageWithCaseSample = creator.createLabMessageWithTestReport(externalMessageSample.toReference());
		ExternalMessageDto messageWithSurveillanceReport =
			creator.createLabMessageWithSurveillanceReport(user.toReference(), externalMessageCase.toReference());
		ExternalMessageDto labMessageWithSurveillanceReportAndSample =
			creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caseWithSample.toReference(), caseSample.toReference());
		ExternalMessageDto messageWithContactSample = creator.createLabMessageWithTestReport(contactSample.toReference());
		ExternalMessageDto messageWithEventParticipantSample = creator.createLabMessageWithTestReport(eventParticipantSample.toReference());
		ExternalMessageDto messageWithEvent = creator.createLabMessageWithTestReport(eventParticipantSampleForDeletedEvent.toReference());

		assertThat(getExternalMessageFacade().count(new ExternalMessageCriteria()), is(6L));
		List<ExternalMessageIndexDto> indexList = getExternalMessageFacade().getIndexList(new ExternalMessageCriteria(), null, null, null);
		assertThat(indexList, hasSize(6));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, messageWithCaseSample)).count(), is(1L));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, messageWithSurveillanceReport)).count(), is(1L));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, labMessageWithSurveillanceReportAndSample)).count(), is(1L));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, messageWithContactSample)).count(), is(1L));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, messageWithEventParticipantSample)).count(), is(1L));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, messageWithEvent)).count(), is(1L));

		getCaseFacade().delete(externalMessageCase.getUuid(), new DeletionDetails());
		getSampleFacade().delete(externalMessageSample.getUuid(), new DeletionDetails());
		getCaseFacade().delete(caseWithSample.getUuid(), new DeletionDetails());
		getContactFacade().delete(contactWithSample.getUuid(), new DeletionDetails());
		getEventParticipantFacade().delete(eventParticipantWithSample.getUuid(), new DeletionDetails());
		getEventFacade().delete(eventToDelete.getUuid(), new DeletionDetails());

		assertThat(getExternalMessageFacade().count(new ExternalMessageCriteria()), is(0L));
		indexList = getExternalMessageFacade().getIndexList(new ExternalMessageCriteria(), null, null, null);
		assertThat(indexList, hasSize(0));

	}

	@Test
	public void testDiseaseVariantDeterminationOnSave() {
		CustomizableEnumValue diseaseVariantEnumValue = new CustomizableEnumValue();
		diseaseVariantEnumValue.setDataType(CustomizableEnumType.DISEASE_VARIANT);
		diseaseVariantEnumValue.setValue("BF.1.2");
		diseaseVariantEnumValue.setDiseases(Collections.singletonList(Disease.CORONAVIRUS));
		diseaseVariantEnumValue.setCaption("BF.1.2 variant");
		getCustomizableEnumValueService().ensurePersisted(diseaseVariantEnumValue);

		DiseaseVariant diseaseVariant = new DiseaseVariant();
		diseaseVariant.setValue(diseaseVariantEnumValue.getValue());

		CustomizableEnumValue diseaseVariantEnumValue2 = new CustomizableEnumValue();
		diseaseVariantEnumValue2.setDataType(CustomizableEnumType.DISEASE_VARIANT);
		diseaseVariantEnumValue2.setValue("BF.1.3");
		diseaseVariantEnumValue2.setDiseases(Collections.singletonList(Disease.CORONAVIRUS));
		diseaseVariantEnumValue2.setCaption("BF.1.3 variant");
		getCustomizableEnumValueService().ensurePersisted(diseaseVariantEnumValue2);

		DiseaseVariant diseaseVariant2 = new DiseaseVariant();
		diseaseVariant2.setValue(diseaseVariantEnumValue2.getValue());

		Mockito
			.when(MockProducer.getCustomizableEnumFacadeForConverter().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, diseaseVariant.getValue()))
			.thenReturn(diseaseVariant);
		Mockito
			.when(MockProducer.getCustomizableEnumFacadeForConverter().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, diseaseVariant2.getValue()))
			.thenReturn(diseaseVariant2);

		ExternalMessageDto labMessage =
			getExternalMessageFacade().save(createLabMessageWithDiseaseVariants(diseaseVariant, null, diseaseVariant, null));
		assertEquals(diseaseVariant, labMessage.getDiseaseVariant());

		//Will not update the disease variant if already set
		labMessage.getSampleReports().get(0).getTestReports().get(0).setTestedDiseaseVariant(diseaseVariant2.getValue());
		labMessage.getSampleReports().get(0).getTestReports().get(1).setTestedDiseaseVariant(diseaseVariant2.getValue());
		getExternalMessageFacade().save(labMessage);
		assertEquals(diseaseVariant, labMessage.getDiseaseVariant());

		labMessage = getExternalMessageFacade().save(createLabMessageWithDiseaseVariants(diseaseVariant, null, diseaseVariant2, null));
		assertNull(labMessage.getDiseaseVariant());

		labMessage = getExternalMessageFacade().save(createLabMessageWithDiseaseVariants(diseaseVariant, "Details", diseaseVariant, "Details"));
		assertEquals(diseaseVariant, labMessage.getDiseaseVariant());
		assertEquals("Details", labMessage.getDiseaseVariantDetails());

		labMessage = getExternalMessageFacade().save(createLabMessageWithDiseaseVariants(diseaseVariant, "Details 1", diseaseVariant, "Details 2"));
		assertNull(labMessage.getDiseaseVariant());
		assertNull(labMessage.getDiseaseVariantDetails());
	}

	private ExternalMessageDto createLabMessageWithDiseaseVariants(
		DiseaseVariant diseaseVariant1,
		String diseaseVariantDetails1,
		DiseaseVariant diseaseVariant2,
		String diseaseVariantDetails2) {
		ExternalMessageDto labMessage = ExternalMessageDto.build();
		labMessage.setType(ExternalMessageType.LAB_MESSAGE);
		labMessage.setDisease(Disease.CORONAVIRUS);

		SampleReportDto sampleReport = SampleReportDto.build();
		labMessage.addSampleReport(sampleReport);

		TestReportDto testReport = TestReportDto.build();
		testReport.setTestedDiseaseVariant(diseaseVariant1.getValue());
		testReport.setTestedDiseaseVariantDetails(diseaseVariantDetails1);
		testReport.setTestResult(PathogenTestResultType.POSITIVE);
		sampleReport.addTestReport(testReport);

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestedDiseaseVariant(diseaseVariant2.getValue());
		testReport1.setTestedDiseaseVariantDetails(diseaseVariantDetails2);
		testReport1.setTestResult(PathogenTestResultType.POSITIVE);
		sampleReport.addTestReport(testReport1);

		return labMessage;
	}

	@Test
	public void testSaveAndProcess() {

		ExternalMessageDto labMessage = createLabMessage(m -> m.setAutomaticProcessingPossible(true));
		ExternalMessageDto savedLabMessage = getExternalMessageFacade().saveAndProcessLabmessage(labMessage);

		assertThat(savedLabMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));
		ExternalMessageDto externMessage = getExternalMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(externMessage, is(notNullValue()));
		assertThat(externMessage.getStatus(), is(ExternalMessageStatus.PROCESSED));

		// assert that a case has been created
		List<String> caseUuids = getCaseFacade().getAllUuids();
		assertThat(caseUuids, hasSize(1));
		// a sample has been created for the sample report
		SampleReferenceDto sample = getSampleReportFacade().getByUuid(labMessage.getSampleReports().get(0).getUuid()).getSample();
		assertThat(sample, is(notNullValue()));
		// the associated case of sample is the sample resulted by processing the sample report
		assertThat(getSampleFacade().getByCaseUuids(caseUuids).get(0).toReference(), is(sample));
	}

	@Test
	public void testOnlyLabMessageSavedOnExceptionWhileProcessing() {
		ExternalMessageDto labMessageWithNoLab = createLabMessage(m -> {
			m.setAutomaticProcessingPossible(true);
			m.setReporterExternalIds(null);
		});

		// error when saving sample
		getExternalMessageFacade().saveAndProcessLabmessage(labMessageWithNoLab);
		assertThat(labMessageWithNoLab.getStatus(), is(ExternalMessageStatus.UNPROCESSED));
		assertThat(getPersonFacade().getAllUuids(), hasSize(0));
		assertThat(getCaseFacade().getAllUuids(), hasSize(0));
		assertThat(getSampleFacade().getAllActiveUuids(), hasSize(0));
		// the lab message has been saved as unprocessed
		ExternalMessageDto savedLabMessageWithNoLab = getExternalMessageFacade().getByUuid(labMessageWithNoLab.getUuid());
		assertThat(savedLabMessageWithNoLab, is(notNullValue()));
		assertThat(savedLabMessageWithNoLab.getStatus(), is(ExternalMessageStatus.UNPROCESSED));

		ExternalMessageDto labMessageWithIncompleteTest = createLabMessage(m -> {
			m.setAutomaticProcessingPossible(true);

			TestReportDto testReport = TestReportDto.build();
			testReport.setTestResult(null);
			testReport.setTestDateTime(new Date());

			m.getSampleReports().get(0).getTestReports().add(testReport);
		});
		labMessageWithIncompleteTest.getSampleReports().get(0).getTestReports().get(0).setTestType(null);

		// error when saving last pathogen test
		getExternalMessageFacade().saveAndProcessLabmessage(labMessageWithIncompleteTest);

		assertThat(labMessageWithIncompleteTest.getStatus(), is(ExternalMessageStatus.UNPROCESSED));
		assertThat(getPersonFacade().getAllUuids(), hasSize(0));
		assertThat(getCaseFacade().getAllUuids(), hasSize(0));
		assertThat(getSampleFacade().getAllActiveUuids(), hasSize(0));
		assertThat(getPathogenTestFacade().getAllActiveUuids(), hasSize(0));

		// the lab message has been saved as unprocessed
		ExternalMessageDto savedLabMessageWithIncompleteTest = getExternalMessageFacade().getByUuid(labMessageWithIncompleteTest.getUuid());
		assertThat(savedLabMessageWithNoLab, is(notNullValue()));
		assertThat(savedLabMessageWithIncompleteTest.getStatus(), is(ExternalMessageStatus.UNPROCESSED));
	}

	@Test
	public void testOnlyLabMessageSavedOnCanceledProcessing() {
		ExternalMessageDto labMessage = createLabMessage((m) -> m.setAutomaticProcessingPossible(true));

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_OFFICER);
		PersonDto person = creator.createPerson(labMessage.getPersonFirstName(), labMessage.getPersonLastName(), labMessage.getPersonSex(), p -> {
			p.setNationalHealthId(labMessage.getPersonNationalHealthId());
		});
		creator.createCase(user.toReference(), person.toReference(), rdcf, c -> c.setDisease(labMessage.getDisease()));

		// cancelled processing
		getExternalMessageFacade().saveAndProcessLabmessage(labMessage);
		assertThat(labMessage.getStatus(), is(ExternalMessageStatus.UNPROCESSED));
		assertThat(getPersonFacade().getAllUuids(), hasSize(1));
		assertThat(getCaseFacade().getAllUuids(), hasSize(1));
		assertThat(getSampleFacade().getAllActiveUuids(), hasSize(0));
		// the lab message has been saved as unprocessed
		ExternalMessageDto savedLabMessageWithNoLab = getExternalMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(savedLabMessageWithNoLab, is(notNullValue()));
		assertThat(savedLabMessageWithNoLab.getStatus(), is(ExternalMessageStatus.UNPROCESSED));
	}

	private ExternalMessageDto createLabMessage(Consumer<ExternalMessageDto> customConfig) {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		FacilityDto lab = creator.createFacility("Lab", rdcf.region, rdcf.district, f -> {
			f.setType(FacilityType.LABORATORY);
			f.setExternalID("test-facility-ext-id-1");
		});

		ExternalMessageDto labMessage = ExternalMessageDto.build();
		labMessage.setType(ExternalMessageType.LAB_MESSAGE);
		labMessage.setMessageDateTime(new Date());
		labMessage.setDisease(Disease.CORONAVIRUS);
		labMessage.setPersonFirstName("John");
		labMessage.setPersonLastName("Doe");
		labMessage.setPersonSex(Sex.MALE);
		labMessage.setPersonNationalHealthId("1234567890");
		labMessage.setPersonFacility(rdcf.facility);
		labMessage.setReporterExternalIds(Collections.singletonList(lab.getExternalID()));

		SampleReportDto sampleReport = SampleReportDto.build();
		sampleReport.setSampleDateTime(new Date());
		sampleReport.setSpecimenCondition(SpecimenCondition.ADEQUATE);

		TestReportDto testReport = TestReportDto.build();
		testReport.setTestResult(PathogenTestResultType.PENDING);
		testReport.setTestDateTime(new Date());

		sampleReport.setTestReports(Collections.singletonList(testReport));
		labMessage.setSampleReports(Collections.singletonList(sampleReport));

		if (customConfig != null) {
			customConfig.accept(labMessage);
		}

		return labMessage;
	}
	//	This test currently does not work because the bean tests used don't support @TransactionAttribute tags.
//	This test should be enabled once there is a new test framework in use.
//	@Test
//	public void testSaveWithFallback() {
//
//		// valid message
//		ExternalMessageDto validMessage = ExternalMessageDto.build();
//		validMessage.setReportId("reportId");
//		validMessage.setStatus(ExternalMessageStatus.FORWARDED);
//		validMessage.setTestReports(Collections.singletonList(TestReportDto.build()));
//		validMessage.setPersonFirstName("Dude");
//		validMessage.setExternalMessageDetails("Details");
//		getLabMessageFacade().saveWithFallback(validMessage);
//		ExternalMessageDto savedMessage = getLabMessageFacade().getByUuid(validMessage.getUuid());
//		assertEquals(validMessage, savedMessage);
//
//		// Invalid message
//		ExternalMessageDto invalidMessage = ExternalMessageDto.build();
//		invalidMessage.setExternalMessageDetails("Details");
//		invalidMessage.setPersonFirstName(String.join("", Collections.nCopies(50, "MaliciousDude")));
//		getLabMessageFacade().saveWithFallback(invalidMessage);
//		savedMessage = getLabMessageFacade().getByUuid(invalidMessage.getUuid());
//		assertEquals(invalidMessage.getUuid(), savedMessage.getUuid());
//		assertEquals(invalidMessage.getStatus(), savedMessage.getStatus());
//		assertEquals(invalidMessage.getExternalMessageDetails(), savedMessage.getExternalMessageDetails());
//		assertNull(savedMessage.getPersonFirstName());
//
//		// make sure that valid message still exists
//		savedMessage = getLabMessageFacade().getByUuid(validMessage.getUuid());
//		assertEquals(validMessage, savedMessage);
//
//	}
}
