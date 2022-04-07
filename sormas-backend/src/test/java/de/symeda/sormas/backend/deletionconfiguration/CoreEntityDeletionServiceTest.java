package de.symeda.sormas.backend.deletionconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.spi.QueryImplementor;
import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTest;

public class CoreEntityDeletionServiceTest extends AbstractBeanTest {

	@Before
	public void setupConfig() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_URL, "url");
	}

	@Test
	public void testCaseAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getCoreEntityTypeConfig(CoreEntityType.CASE);

		final Date today = new Date();
		final Date tenYearsPlusAgo = DateUtils.addDays(today, (-1) * coreEntityTypeConfig.deletionPeriod - 1);

		TestDataCreator.RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person", Sex.MALE, 1980, 1, 1);
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			tenYearsPlusAgo,
			rdcf);

		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, tenYearsPlusAgo, tenYearsPlusAgo, null);

		SessionImpl em = (SessionImpl) getEntityManager();
		QueryImplementor query = em.createQuery("select c from cases c where c.uuid=:uuid");
		query.setParameter("uuid", caze.getUuid());
		Case singleResult = (Case) query.getSingleResult();
		singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
		singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
		em.save(singleResult);

		CaseCriteria caseCriteria = new CaseCriteria();
		caseCriteria.deleted(false);

		assertEquals(1, getCaseFacade().count(caseCriteria));

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();

		assertEquals(0, getCaseFacade().count(caseCriteria));
	}

	@Test
	public void testCasePermanentDeletion() throws IOException {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.ADMIN, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		creator.createClinicalVisit(caze);
		creator.createTreatment(caze);
		creator.createPrescription(caze);
		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		creator.createSurveillanceReport(user.toReference(), caze.toReference());
		creator.createDocument(
			user.toReference(),
			"document.pdf",
			"application/pdf",
			42L,
			DocumentRelatedEntityType.CASE,
			caze.getUuid(),
			"content".getBytes(StandardCharsets.UTF_8));

		CaseDataDto duplicateCase = creator.createCase(user.toReference(), person.toReference(), rdcf);
		getCaseFacade().deleteCaseAsDuplicate(duplicateCase.getUuid(), caze.getUuid());

		ContactDto resultingContact = creator.createContact(user.toReference(), person.toReference(), caze);
		ContactDto sourceContact = creator.createContact(
			user.toReference(),
			person.toReference(),
			caze.getDisease(),
			contactDto -> contactDto.setResultingCase(caze.toReference()));
		EventDto event = creator.createEvent(user.toReference(), caze.getDisease());
		EventParticipantDto eventParticipant = creator.createEventParticipant(
			event.toReference(),
			person,
			"Description",
			user.toReference(),
			eventParticipantDto -> eventParticipantDto.setResultingCase(caze.toReference()),
			rdcf);
		SampleDto multiSample = creator.createSample(
			caze.toReference(),
			user.toReference(),
			rdcf.facility,
			sampleDto -> sampleDto.setAssociatedContact(resultingContact.toReference()));
		TravelEntryDto travelEntry =
			creator.createTravelEntry(person.toReference(), user.toReference(), rdcf, te -> te.setResultingCase(caze.toReference()));
		ImmunizationDto immunization = creator.createImmunization(
			caze.getDisease(),
			person.toReference(),
			user.toReference(),
			rdcf,
			immunizationDto -> immunizationDto.setRelatedCase(caze.toReference()));

		VisitDto visit = creator.createVisit(caze.getDisease(), caze.getPerson(), caze.getReportDate());
		visit.getSymptoms().setAnorexiaAppetiteLoss(SymptomState.YES);
		getVisitFacade().saveVisit(visit);

		assertEquals(2, getCaseService().count());

		getCaseFacade().delete(caze.getUuid());

		useSystemUser();
		getCoreEntityDeletionService().executePermanentDeletion();
		loginWith(user);

		assertEquals(0, getCaseService().count());
		assertEquals(0, getClinicalVisitService().count());
		assertEquals(0, getTreatmentService().count());
		assertEquals(0, getPrescriptionService().count());
		assertEquals(1, getSampleService().count());
		assertEquals(1, getVisitService().count());
		assertNull(getSampleFacade().getSampleByUuid(multiSample.getUuid()).getAssociatedCase());
		assertEquals(0, getSurveillanceReportService().count());
		assertTrue(getDocumentService().getAll().get(0).isDeleted());
		assertNull(getContactFacade().getByUuid(resultingContact.getUuid()).getCaze());
		assertNull(getContactFacade().getByUuid(sourceContact.getUuid()).getResultingCase());
		assertNull(getEventParticipantFacade().getByUuid(eventParticipant.getUuid()).getResultingCase());
		assertNull(getTravelEntryFacade().getByUuid(travelEntry.getUuid()).getResultingCase());
		assertNull(getImmunizationFacade().getByUuid(immunization.getUuid()).getRelatedCase());
	}

	@Test
	public void testSamplePermanentDeletion() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.ADMIN, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto referralSample =
			creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> s.setReferredTo(sample.toReference()));
		creator.createPathogenTest(sample.toReference(), caze);
		creator.createAdditionalTest(sample.toReference());
		LabMessageDto labMessage = creator.createLabMessage(lm -> lm.setSample(sample.toReference()));

		getSampleFacade().deleteSample(sample.toReference());

		List<PathogenTest> pathogenTests = getPathogenTestService().getAll();
		assertEquals(2, getSampleService().count());
		assertTrue(getSampleService().getByUuid(sample.getUuid()).isDeleted());
		assertEquals(1, pathogenTests.size());
		assertTrue(pathogenTests.get(0).isDeleted());
		assertEquals(1, getAdditionalTestService().count());
		assertNull(getSampleService().getByUuid(referralSample.getUuid()).getReferredTo());
		assertNull(getLabMessageService().getByUuid(labMessage.getUuid()).getSample());

		useSystemUser();
		getCoreEntityDeletionService().executePermanentDeletion();
		loginWith(user);

		assertEquals(1, getSampleService().count());
		assertEquals(0, getPathogenTestService().count());
		assertEquals(0, getAdditionalTestService().count());
		assertEquals(1, getLabMessageService().count());
	}

	@Test
	public void testLabMessagePermanentDeletion() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.ADMIN, UserRole.NATIONAL_USER);

		LabMessageDto labMessage = creator.createLabMessage(null);
		TestReportDto testReport = creator.createTestReport(labMessage.toReference());

		getLabMessageFacade().deleteLabMessage(labMessage.getUuid());

		assertEquals(1, getLabMessageService().count());
		assertEquals(labMessage.toReference(), getTestReportFacade().getByUuid(testReport.getUuid()).getLabMessage());

		useSystemUser();
		getCoreEntityDeletionService().executePermanentDeletion();
		loginWith(user);

		assertEquals(0, getLabMessageService().count());
		assertEquals(0, getTestReportService().count());
	}

	@Test
	public void testCaseVisitPermanentDeletion() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.ADMIN, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		VisitDto visit = creator.createVisit(caze.getDisease(), caze.getPerson(), caze.getReportDate());
		visit.getSymptoms().setAnorexiaAppetiteLoss(SymptomState.YES);
		getVisitFacade().saveVisit(visit);

		assertEquals(1, getCaseService().count());

		getCaseFacade().delete(caze.getUuid());

		useSystemUser();
		getCoreEntityDeletionService().executePermanentDeletion();
		loginWith(user);

		assertEquals(0, getCaseService().count());
		assertEquals(0, getVisitService().count());
	}

	private void createDeletionConfigurations() {
		create(CoreEntityType.CASE);
		create(CoreEntityType.CONTACT);
		create(CoreEntityType.EVENT);
		create(CoreEntityType.EVENT_PARTICIPANT);
		create(CoreEntityType.IMMUNIZATION);
		create(CoreEntityType.TRAVEL_ENTRY);
	}

	private DeletionConfiguration create(CoreEntityType coreEntityType) {
		DeletionConfigurationService deletionConfigurationService = getBean(DeletionConfigurationService.class);

		DeletionConfiguration entity = new DeletionConfiguration();
		entity.setEntityType(coreEntityType);
		entity.setDeletionReference(DeletionReference.CREATION);
		entity.setDeletionPeriod(3650);
		deletionConfigurationService.ensurePersisted(entity);
		return entity;
	}
}
