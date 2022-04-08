package de.symeda.sormas.backend.deletionconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.spi.QueryImplementor;
import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
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
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.travelentry.TravelEntry;

public class CoreEntityDeletionServiceTest extends AbstractBeanTest {

	@Before
	public void setupConfig() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_URL, "url");
	}

	@Test
	public void testCaseAutomaticDeletion() throws IOException {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getCoreEntityTypeConfig(CoreEntityType.CASE);

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

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		SessionImpl em = (SessionImpl) getEntityManager();
		QueryImplementor query = em.createQuery("select c from cases c where c.uuid=:uuid");
		query.setParameter("uuid", caze.getUuid());
		Case singleResult = (Case) query.getSingleResult();
		singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
		singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
		em.save(singleResult);

		assertEquals(2, getCaseService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getCaseService().count());
		assertEquals(duplicateCase.getUuid(), getCaseService().getAll().get(0).getUuid());
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
	public void testCaseVisitAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getCoreEntityTypeConfig(CoreEntityType.CASE);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.ADMIN, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		VisitDto visit = creator.createVisit(caze.getDisease(), caze.getPerson(), caze.getReportDate());
		visit.getSymptoms().setAnorexiaAppetiteLoss(SymptomState.YES);
		getVisitFacade().saveVisit(visit);

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		SessionImpl em = (SessionImpl) getEntityManager();
		QueryImplementor query = em.createQuery("select c from cases c where c.uuid=:uuid");
		query.setParameter("uuid", caze.getUuid());
		Case singleResult = (Case) query.getSingleResult();
		singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
		singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
		em.save(singleResult);

		assertEquals(1, getCaseService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getCaseService().count());
		assertEquals(0, getVisitService().count());
	}

	@Test
	public void testImmunizationAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getCoreEntityTypeConfig(CoreEntityType.IMMUNIZATION);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.ADMIN, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		ImmunizationDto immunization = creator.createImmunization(Disease.EVD, person.toReference(), user.toReference(), rdcf);
		creator.createVaccination(user.toReference(), immunization.toReference());

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		SessionImpl em = (SessionImpl) getEntityManager();
		QueryImplementor query = em.createQuery("select i from immunization i where i.uuid=:uuid");
		query.setParameter("uuid", immunization.getUuid());
		Immunization singleResult = (Immunization) query.getSingleResult();
		singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
		singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
		em.save(singleResult);

		assertEquals(1, getImmunizationService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getImmunizationService().count());
		assertEquals(0, getVaccinationService().count());
	}

	@Test
	public void testTravelEntryAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getCoreEntityTypeConfig(CoreEntityType.TRAVEL_ENTRY);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.ADMIN, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		TravelEntryDto travelEntry = creator.createTravelEntry(person.toReference(), user.toReference(), rdcf, null);

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		SessionImpl em = (SessionImpl) getEntityManager();
		QueryImplementor query = em.createQuery("select t from travelentry t where t.uuid=:uuid");
		query.setParameter("uuid", travelEntry.getUuid());
		TravelEntry singleResult = (TravelEntry) query.getSingleResult();
		singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
		singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
		em.save(singleResult);

		assertEquals(1, getTravelEntryService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getTravelEntryService().count());
	}

	@Test
	public void testPersonAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration caseCoreEntityTypeConfig = getDeletionConfigurationService().getCoreEntityTypeConfig(CoreEntityType.CASE);
		DeletionConfiguration immunizationCoreEntityTypeConfig =
			getDeletionConfigurationService().getCoreEntityTypeConfig(CoreEntityType.IMMUNIZATION);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.ADMIN, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		ImmunizationDto immunization = creator.createImmunization(Disease.EVD, person.toReference(), user.toReference(), rdcf);

		final Date tenYearsPlusAgoCases = DateUtils.addDays(new Date(), (-1) * caseCoreEntityTypeConfig.deletionPeriod - 1);
		SessionImpl em = (SessionImpl) getEntityManager();
		QueryImplementor query = em.createQuery("select c from cases c where c.uuid=:uuid");
		query.setParameter("uuid", caze.getUuid());
		Case singleResultCase = (Case) query.getSingleResult();
		singleResultCase.setCreationDate(new Timestamp(tenYearsPlusAgoCases.getTime()));
		singleResultCase.setChangeDate(new Timestamp(tenYearsPlusAgoCases.getTime()));
		em.save(singleResultCase);

		assertEquals(1, getPersonService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getPersonService().count());

		final Date tenYearsPlusAgoContacts = DateUtils.addDays(new Date(), (-1) * immunizationCoreEntityTypeConfig.deletionPeriod - 1);
		em = (SessionImpl) getEntityManager();
		query = em.createQuery("select i from immunization i where i.uuid=:uuid");
		query.setParameter("uuid", immunization.getUuid());
		Immunization singleResultImmunization = (Immunization) query.getSingleResult();
		singleResultImmunization.setCreationDate(new Timestamp(tenYearsPlusAgoContacts.getTime()));
		singleResultImmunization.setChangeDate(new Timestamp(tenYearsPlusAgoContacts.getTime()));
		em.save(singleResultImmunization);

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getPersonService().count());
	}

	@Test
	public void testAutomaticManuallyDeletedEntitiesDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration deletionConfig = getDeletionConfigurationService().getCoreEntityTypeManualDeletionConfig(CoreEntityType.IMMUNIZATION);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.ADMIN, UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		ImmunizationDto immunization = creator.createImmunization(Disease.EVD, person.toReference(), user.toReference(), rdcf);

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getImmunizationService().count());

		getImmunizationFacade().delete(immunization.getUuid());

		assertEquals(1, getImmunizationService().count());

		final Date ninetyDaysPlusAgo = DateUtils.addDays(new Date(), (-1) * deletionConfig.deletionPeriod - 1);
		SessionImpl em = (SessionImpl) getEntityManager();
		QueryImplementor query = em.createQuery("update immunization i set changedate=:date where i.uuid=:uuid");
		em.getTransaction().begin();
		query.setParameter("date", new Timestamp(ninetyDaysPlusAgo.getTime()));
		query.setParameter("uuid", immunization.getUuid());
		query.executeUpdate();
		em.getTransaction().commit();

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getImmunizationService().count());
	}
}
