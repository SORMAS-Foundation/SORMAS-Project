package de.symeda.sormas.backend.deletionconfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasTest;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.user.User;

public class CoreEntityDeletionServiceTest extends SormasToSormasTest {

	@BeforeEach
	public void setupConfig() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_URL, "url");
	}

	@Test
	public void testCaseAutomaticDeletion() throws IOException {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CASE);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		creator.createClinicalVisit(caze);
		creator.createTreatment(caze);
		creator.createPrescription(caze);
		creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		creator.createSurveillanceReport(user.toReference(), caze.toReference());

		byte[] contentAsBytes =
			("%PDF-1.0\n1 0 obj<</Type/Catalog/Pages " + "2 0 R>>endobj 2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj 3 0 obj<</Ty"
				+ "pe/Page/MediaBox[0 0 3 3]>>endobj\nxref\n0 4\n0000000000 65535 f\n000000001"
				+ "0 00000 n\n0000000053 00000 n\n0000000102 00000 n\ntrailer<</Size 4/Root 1 " + "0 R>>\nstartxref\n149\n%EOF").getBytes();

		creator.createDocument(
			user.toReference(),
			"document.pdf",
			"application/pdf",
			42L,
			DocumentRelatedEntityType.CASE,
			caze.getUuid(),
			contentAsBytes);

		CaseDataDto duplicateCase = creator.createCase(user.toReference(), person.toReference(), rdcf);
		getCaseFacade().deleteAsDuplicate(duplicateCase.getUuid(), caze.getUuid());

		final ContactDto resultingContact = creator.createContact(user.toReference(), person.toReference(), caze);
		assertNull(resultingContact.getRegion());
		ContactDto sourceContact = creator.createContact(
			user.toReference(),
			person.toReference(),
			caze.getDisease(),
			contactDto -> contactDto.setResultingCase(caze.toReference()));
		ContactDto deletedSourceContact = creator.createContact(
			user.toReference(),
			person.toReference(),
			caze.getDisease(),
			contactDto -> contactDto.setResultingCase(caze.toReference()));
		getContactFacade().delete(deletedSourceContact.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));
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
		creator.createTravelEntry(person.toReference(), user.toReference(), rdcf, te -> {
			te.setResultingCase(caze.toReference());
			te.setDeleted(true);
		});
		ImmunizationDto immunization = creator.createImmunization(
			caze.getDisease(),
			person.toReference(),
			user.toReference(),
			rdcf,
			immunizationDto -> immunizationDto.setRelatedCase(caze.toReference()));

		VisitDto visit = creator.createVisit(caze.getDisease(), caze.getPerson(), caze.getReportDate());
		visit.getSymptoms().setAnorexiaAppetiteLoss(SymptomState.YES);
		getVisitFacade().save(visit);

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select c from cases c where c.uuid=:uuid");
			query.setParameter("uuid", caze.getUuid());
			Case singleResult = (Case) query.getSingleResult();
			singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(singleResult);
		});

		assertEquals(2, getCaseService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		ContactDto resultingContactUpdated = getContactFacade().getByUuid(resultingContact.getUuid());

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
		assertNull(resultingContactUpdated.getCaze());
		assertEquals(rdcf.region, resultingContactUpdated.getRegion());
		assertEquals(rdcf.district, resultingContactUpdated.getDistrict());
		assertEquals(rdcf.community, resultingContactUpdated.getCommunity());
		assertNull(getContactFacade().getByUuid(sourceContact.getUuid()).getResultingCase());
		assertNull(getEventParticipantFacade().getByUuid(eventParticipant.getUuid()).getResultingCase());
		assertNull(getTravelEntryFacade().getByUuid(travelEntry.getUuid()).getResultingCase());
		assertNull(getImmunizationFacade().getByUuid(immunization.getUuid()).getRelatedCase());
	}

	@Test
	public void testCaseVisitAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CASE);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		creator.createClinicalVisit(caze, v -> {
			v.setVisitingPerson("John Smith");
			v.setVisitRemarks("Test remarks");

			SymptomsDto symptoms = v.getSymptoms();
			symptoms.setPatientIllLocation("Test ill location");
			symptoms.setOtherHemorrhagicSymptoms(SymptomState.YES);
			symptoms.setOtherHemorrhagicSymptomsText("OtherHemorrhagic");
		});

		VisitDto visit = creator.createVisit(caze.getDisease(), caze.getPerson(), caze.getReportDate());
		visit.getSymptoms().setAnorexiaAppetiteLoss(SymptomState.YES);
		getVisitFacade().save(visit);

		CaseDataDto caze2 = creator.createCase(user.toReference(), person.toReference(), rdcf);
		creator.createClinicalVisit(caze2, v -> {
			v.setVisitingPerson("John Wayne");
			v.setVisitRemarks("Visit remarks");

			SymptomsDto symptoms = v.getSymptoms();
			symptoms.setPatientIllLocation("Test sick location");
			symptoms.setOtherHemorrhagicSymptoms(SymptomState.YES);
			symptoms.setOtherHemorrhagicSymptomsText("OtherHemorrhagic for case 2");
		});

		VisitDto visit2 = creator.createVisit(caze2.getDisease(), caze2.getPerson(), caze2.getReportDate());
		visit2.getSymptoms().setCough(SymptomState.YES);
		getVisitFacade().save(visit2);

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select c from cases c where c.uuid=:uuid");
			query.setParameter("uuid", caze.getUuid());
			Case singleResult = (Case) query.getSingleResult();
			singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(singleResult);
		});

		assertEquals(2, getCaseService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getCaseService().count());
		assertEquals(1, getVisitService().count());
		assertEquals(3, getSymptomsService().count());
	}

	@Test
	public void testImmunizationAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.IMMUNIZATION);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		ImmunizationDto immunization = creator.createImmunization(Disease.EVD, person.toReference(), user.toReference(), rdcf);
		creator.createVaccination(user.toReference(), immunization.toReference());

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select i from immunization i where i.uuid=:uuid");
			query.setParameter("uuid", immunization.getUuid());
			Immunization singleResult = (Immunization) query.getSingleResult();
			singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(singleResult);
		});

		assertEquals(1, getImmunizationService().count());
		assertEquals(1, getVaccinationService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getImmunizationService().count());
		assertEquals(0, getVaccinationService().count());
		assertEquals(0, getPersonService().count());
	}

	@Test
	public void testOrphanReducedImmunizationAutomaticDeletion() {

		createDeletionConfigurations();

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		ImmunizationDto immunization = creator.createImmunization(Disease.EVD, person.toReference(), user.toReference(), rdcf);
		creator.createVaccination(user.toReference(), immunization.toReference());

		assertEquals(1, getImmunizationService().count());
		assertEquals(1, getVaccinationService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertFalse(getFeatureConfigurationFacade().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED));
		assertEquals(1, getImmunizationService().count());
		assertEquals(1, getVaccinationService().count());
		assertEquals(1, getPersonService().count());

		// change feature configuration to immunization reduced
		createFeatureConfiguration(FeatureType.IMMUNIZATION_MANAGEMENT, true, Map.of(FeatureTypeProperty.REDUCED, true));

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertTrue(getFeatureConfigurationFacade().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED));
		assertEquals(0, getImmunizationService().count());
		assertEquals(0, getVaccinationService().count());
		assertEquals(0, getPersonService().count());
	}

	@Test
	public void testEventAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.EVENT);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		EventDto event = creator.createEvent(user.toReference(), Disease.EVD);
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select e from events e where e.uuid=:uuid");
			query.setParameter("uuid", event.getUuid());
			Event singleResult = (Event) query.getSingleResult();
			singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(singleResult);

			Query evPQuery = em.createQuery("select ep from EventParticipant ep where ep.uuid=:uuid");
			evPQuery.setParameter("uuid", eventParticipant.getUuid());
			EventParticipant evPResult = (EventParticipant) evPQuery.getSingleResult();
			evPResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			evPResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(evPResult);
		});

		assertEquals(1, getEventService().count());
		assertEquals(1, getEventParticipantService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getEventService().count());
		assertEquals(0, getEventParticipantService().count());
	}

	@Test
	public void testEventParticipantWithEventSampleAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.EVENT);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		EventDto event = creator.createEvent(user.toReference(), Disease.EVD);
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		SampleDto sampleDto = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query evPQuery = em.createQuery("select ep from EventParticipant ep where ep.uuid=:uuid");
			evPQuery.setParameter("uuid", eventParticipant.getUuid());
			EventParticipant evPResult = (EventParticipant) evPQuery.getSingleResult();
			evPResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			evPResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(evPResult);
		});

		Sample sample = getSampleService().getByUuid(sampleDto.getUuid());

		assertEquals(1, getEventParticipantService().count());
		assertNotNull(sample.getAssociatedEventParticipant());
		assertEquals(eventParticipant.getUuid(), sample.getAssociatedEventParticipant().getUuid());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getEventParticipantService().count());
		sample = getSampleService().getByUuid(sampleDto.getUuid());
		assertNull(sample);
	}

	@Test
	public void testEventParticipantWithCaseSampleAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.EVENT);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caseDataDto = creator.createCase(user.toReference(), person.toReference(), rdcf);
		EventDto event = creator.createEvent(user.toReference(), Disease.EVD);
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		SampleDto sampleDto = creator.createSample(caseDataDto.toReference(), user.toReference(), rdcf.facility, s -> {
			s.setAssociatedEventParticipant(eventParticipant.toReference());
		});

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query evPQuery = em.createQuery("select ep from EventParticipant ep where ep.uuid=:uuid");
			evPQuery.setParameter("uuid", eventParticipant.getUuid());
			EventParticipant evPResult = (EventParticipant) evPQuery.getSingleResult();
			evPResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			evPResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(evPResult);
		});

		Sample sample = getSampleService().getByUuid(sampleDto.getUuid());

		assertEquals(1, getEventParticipantService().count());
		assertNotNull(sample.getAssociatedEventParticipant());
		assertEquals(eventParticipant.getUuid(), sample.getAssociatedEventParticipant().getUuid());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getEventParticipantService().count());
		sample = getSampleService().getByUuid(sampleDto.getUuid());
		assertNotNull(sample);
		assertEquals(null, sample.getAssociatedEventParticipant());
	}

	@Test
	public void testTravelEntryAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.TRAVEL_ENTRY);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		TravelEntryDto travelEntry = creator.createTravelEntry(person.toReference(), user.toReference(), rdcf, null);

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select t from travelentry t where t.uuid=:uuid");
			query.setParameter("uuid", travelEntry.getUuid());
			TravelEntry singleResult = (TravelEntry) query.getSingleResult();
			singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(singleResult);
		});

		assertEquals(1, getTravelEntryService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getTravelEntryService().count());
	}

	@Test
	public void testPersonAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration caseCoreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CASE);
		DeletionConfiguration immunizationCoreEntityTypeConfig =
			getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.IMMUNIZATION);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf, c -> c.setDisease(Disease.EVD));
		creator.createVisit(Disease.EVD, person.toReference());
		// Change the case disease in order to remove the association with the visit to ensure that the visit is properly deleted
		// alongside the person
		caze = getCaseFacade().getByUuid(caze.getUuid());
		caze.setDisease(Disease.CHOLERA);
		CaseDataDto finalCaze = getCaseFacade().save(caze);
		ImmunizationDto immunization = creator.createImmunization(Disease.EVD, person.toReference(), user.toReference(), rdcf);

		final Date tenYearsPlusAgoCases = DateUtils.addDays(new Date(), (-1) * caseCoreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select c from cases c where c.uuid=:uuid");
			query.setParameter("uuid", finalCaze.getUuid());
			Case singleResultCase = (Case) query.getSingleResult();
			singleResultCase.setCreationDate(new Timestamp(tenYearsPlusAgoCases.getTime()));
			singleResultCase.setChangeDate(new Timestamp(tenYearsPlusAgoCases.getTime()));
			em.persist(singleResultCase);
		});

		assertEquals(1, getPersonService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getPersonService().count());

		final Date tenYearsPlusAgoImmunizations = DateUtils.addDays(new Date(), (-1) * immunizationCoreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select i from immunization i where i.uuid=:uuid");
			query.setParameter("uuid", immunization.getUuid());
			Immunization singleResultImmunization = (Immunization) query.getSingleResult();
			singleResultImmunization.setCreationDate(new Timestamp(tenYearsPlusAgoImmunizations.getTime()));
			singleResultImmunization.setChangeDate(new Timestamp(tenYearsPlusAgoImmunizations.getTime()));
			em.persist(singleResultImmunization);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getPersonService().count());
	}

	@Test
	public void testAutomaticManuallyDeletedEntitiesDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration deletionConfig = getDeletionConfigurationService().getEntityTypeManualDeletionConfig(DeletableEntityType.IMMUNIZATION);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		ImmunizationDto immunization = creator.createImmunization(Disease.EVD, person.toReference(), user.toReference(), rdcf);

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getImmunizationService().count());

		getImmunizationFacade().delete(immunization.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));

		assertEquals(1, getImmunizationService().count());

		final Date ninetyDaysPlusAgo = DateUtils.addDays(new Date(), (-1) * deletionConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("update immunization i set changedate=:date where i.uuid=:uuid");
			query.setParameter("date", new Timestamp(ninetyDaysPlusAgo.getTime()));
			query.setParameter("uuid", immunization.getUuid());
			query.executeUpdate();
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getImmunizationService().count());
	}

	@Test
	public void testRestore() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		ImmunizationDto immunization = creator.createImmunization(Disease.EVD, person.toReference(), user.toReference(), rdcf);

		getImmunizationFacade().delete(immunization.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));
		assertEquals(0, getImmunizationFacade().getIndexList(new ImmunizationCriteria(), 0, 100, null).size());

		getImmunizationFacade().restore(immunization.getUuid());
		List<ImmunizationIndexDto> indexList = getImmunizationFacade().getIndexList(new ImmunizationCriteria(), 0, 100, null);
		assertEquals(1, indexList.size());
		Immunization restoredImmunization = getImmunizationService().getByUuid(indexList.get(0).getUuid());
		assertNull(restoredImmunization.getDeletionReason());
		assertNull(restoredImmunization.getOtherDeletionReason());
	}

	@Test
	public void testContactPermanentDeletion() {

		// the Visit will be added only to contacts that have REPORT_DATE_TIME, LAST_CONTACT_DATE or FOLLOW_UP_UNTIL within less than ALLOWED_DATE_OFFSET days from Visit report date
		// the test is checking the permanent deletion for contacts which are more than ALLOWED_DATE_OFFSET days apart from each other so there will be no shared Visit between them
		// the second part tests the permanent deletion of contacts with the same person which are less than ALLOWED_DATE_OFFSET days apart
		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CONTACT);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();

		ContactDto contactDto = creator.createContact(user.toReference(), person.toReference(), Disease.CORONAVIRUS);

		final Date beyondRelevanceDate = DateUtils.addDays(new Date(), (-1) * (FollowUpLogic.ALLOWED_DATE_OFFSET + 1));
		contactDto.setReportDateTime(beyondRelevanceDate);
		getContactFacade().save(contactDto);

		TaskDto taskDto = creator.createTask(
			TaskContext.CONTACT,
			TaskType.CONTACT_FOLLOW_UP,
			TaskStatus.PENDING,
			null,
			contactDto.toReference(),
			null,
			null,
			new Date(),
			null);

		SampleDto sample = creator.createSample(
			contactDto.toReference(),
			user.toReference(),
			rdcf.facility,
			sampleDto -> sampleDto.setAssociatedContact(contactDto.toReference()));

		VisitDto visitDto = creator.createVisit(contactDto.getDisease(), contactDto.getPerson(), contactDto.getReportDateTime());

		ContactDto contactDto2 = creator.createContact(user.toReference(), person.toReference(), Disease.CORONAVIRUS);

		TaskDto taskDto2 = creator.createTask(
			TaskContext.CONTACT,
			TaskType.CONTACT_FOLLOW_UP,
			TaskStatus.PENDING,
			null,
			contactDto2.toReference(),
			null,
			null,
			new Date(),
			null);

		SampleDto sample2 = creator.createSample(
			contactDto2.toReference(),
			user.toReference(),
			rdcf.facility,
			sampleDto -> sampleDto.setAssociatedContact(contactDto2.toReference()));

		VisitDto visitDto2 = creator.createVisit(contactDto2.getDisease(), contactDto2.getPerson(), contactDto2.getReportDateTime());

		assertEquals(2, getContactService().count());
		assertEquals(1, getTaskFacade().getAllByContact(contactDto.toReference()).size());
		assertEquals(1, getTaskFacade().getAllByContact(contactDto2.toReference()).size());
		assertEquals(2, getSampleService().count());
		assertEquals(2, getVisitService().count());

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select i from contact i where i.uuid=:uuid");
			query.setParameter("uuid", contactDto.getUuid());
			Contact singleResult = (Contact) query.getSingleResult();
			singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(singleResult);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getContactService().count());
		assertEquals(1, getTaskFacade().getAllByContact(contactDto2.toReference()).size());
		assertEquals(1, getSampleService().count());
		assertEquals(1, getVisitService().count());

		ContactDto contactDto3 = creator.createContact(user.toReference(), person.toReference(), Disease.CORONAVIRUS);

		TaskDto taskDto3 = creator.createTask(
			TaskContext.CONTACT,
			TaskType.CONTACT_FOLLOW_UP,
			TaskStatus.PENDING,
			null,
			contactDto3.toReference(),
			null,
			null,
			new Date(),
			null);

		SampleDto sample3 = creator.createSample(
			contactDto3.toReference(),
			user.toReference(),
			rdcf.facility,
			sampleDto -> sampleDto.setAssociatedContact(contactDto3.toReference()));

		VisitDto visitDto3 = creator.createVisit(contactDto3.getDisease(), contactDto3.getPerson(), contactDto3.getReportDateTime());

		final Date tenYearsPlusAgoSecondContact = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query2 = em.createQuery("select i from contact i where i.uuid=:uuid");
			query2.setParameter("uuid", contactDto2.getUuid());
			Contact singleResult2 = (Contact) query2.getSingleResult();
			singleResult2.setCreationDate(new Timestamp(tenYearsPlusAgoSecondContact.getTime()));
			singleResult2.setChangeDate(new Timestamp(tenYearsPlusAgoSecondContact.getTime()));
			em.persist(singleResult2);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getContactService().count());
		assertEquals(0, getTaskFacade().getAllByContact(contactDto2.toReference()).size());
		assertEquals(1, getTaskFacade().getAllByContact(contactDto3.toReference()).size());
		assertEquals(1, getSampleService().count());
		assertEquals(2, getVisitService().count());

		final Date tenYearsPlusAgoThirdContact = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query3 = em.createQuery("select i from contact i where i.uuid=:uuid");
			query3.setParameter("uuid", contactDto3.getUuid());
			Contact singleResult3 = (Contact) query3.getSingleResult();
			singleResult3.setCreationDate(new Timestamp(tenYearsPlusAgoThirdContact.getTime()));
			singleResult3.setChangeDate(new Timestamp(tenYearsPlusAgoThirdContact.getTime()));
			em.persist(singleResult3);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getContactService().count());
		assertEquals(0, getTaskFacade().getAllByContact(contactDto3.toReference()).size());
		assertEquals(0, getSampleService().count());
		assertEquals(0, getVisitService().count());
	}

	@Test
	public void testPermanentDeletionOfVisitLinkedToMultipleContacts() throws IOException {
		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CONTACT);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();

		ContactDto contactDto = creator.createContact(user.toReference(), person.toReference(), Disease.CORONAVIRUS);

		TaskDto taskDto = creator.createTask(
			TaskContext.CONTACT,
			TaskType.CONTACT_FOLLOW_UP,
			TaskStatus.PENDING,
			null,
			contactDto.toReference(),
			null,
			null,
			new Date(),
			null);

		SampleDto sample = creator.createSample(
			contactDto.toReference(),
			user.toReference(),
			rdcf.facility,
			sampleDto -> sampleDto.setAssociatedContact(contactDto.toReference()));

		VisitDto visitDto = creator.createVisit(contactDto.getDisease(), contactDto.getPerson(), contactDto.getReportDateTime());

		assertEquals(1, getContactService().count());
		assertEquals(1, getTaskFacade().getAllActiveUuids().size());
		assertEquals(1, getTaskFacade().getAllByContact(contactDto.toReference()).size());
		assertEquals(1, getSampleService().count());
		assertEquals(1, getVisitService().count());

		//create second contact with the same person
		ContactDto contactDto2 = creator.createContact(user.toReference(), person.toReference(), Disease.CORONAVIRUS);

		TaskDto taskDto2 = creator.createTask(
			TaskContext.CONTACT,
			TaskType.CONTACT_FOLLOW_UP,
			TaskStatus.PENDING,
			null,
			contactDto2.toReference(),
			null,
			null,
			new Date(),
			null);
		SampleDto sample2 = creator.createSample(
			contactDto2.toReference(),
			user.toReference(),
			rdcf.facility,
			sampleDto -> sampleDto.setAssociatedContact(contactDto2.toReference()));

		VisitDto visitDto2 = creator.createVisit(contactDto2.getDisease(), contactDto2.getPerson(), contactDto2.getReportDateTime());

		assertEquals(2, getContactService().count());
		assertEquals(2, getTaskFacade().getAllActiveUuids().size());
		assertEquals(1, getTaskFacade().getAllByContact(contactDto.toReference()).size());
		assertEquals(1, getTaskFacade().getAllByContact(contactDto2.toReference()).size());
		assertEquals(2, getSampleService().count());
		assertEquals(2, getVisitService().count());

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select i from contact i where i.uuid=:uuid");
			query.setParameter("uuid", contactDto.getUuid());
			Contact singleResult = (Contact) query.getSingleResult();
			singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(singleResult);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getContactService().count());
		assertEquals(1, getTaskFacade().getAllActiveUuids().size());
		assertEquals(1, getTaskFacade().getAllByContact(contactDto2.toReference()).size());
		assertEquals(1, getSampleService().count());
		assertEquals(2, getVisitService().count());

		executeInTransaction(em -> {
			Query query2 = em.createQuery("select i from contact i where i.uuid=:uuid");
			query2.setParameter("uuid", contactDto2.getUuid());
			Contact singleResult2 = (Contact) query2.getSingleResult();
			singleResult2.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			singleResult2.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(singleResult2);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getContactService().count());
		assertEquals(0, getTaskFacade().getAllActiveUuids().size());
		assertEquals(0, getSampleService().count());
		assertEquals(0, getVisitService().count());
	}

	@Test
	public void testMinimumDeletionPeriod7Days() {
		getDeletionConfigurationService().ensurePersisted(DeletionConfiguration.build(DeletableEntityType.CONTACT, DeletionReference.CREATION, 7));
		assertThrows(
			ConstraintViolationException.class,
			() -> getDeletionConfigurationService()
				.ensurePersisted(DeletionConfiguration.build(DeletableEntityType.CASE, DeletionReference.CREATION, 6)));
	}

	@Test
	public void testSormasToSormasShareRequestPermanentDeletion() {
		createDeletionConfigurations();

		DeletionConfiguration caseDeletionConfiguration = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CASE);
		DeletionConfiguration contactDeletionConfiguration = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CONTACT);
		DeletionConfiguration eventDeletionConfiguration = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.EVENT);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto officer = creator.createSurveillanceOfficer(rdcf).toReference();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));

		SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
		originInfo.setUuid(DataHelper.createUuid());
		originInfo.setSenderName("Test Name");
		originInfo.setSenderEmail("test@email.com");
		originInfo.setOrganizationId(DEFAULT_SERVER_ID);
		originInfo.setOwnershipHandedOver(true);
		SormasToSormasOriginInfoDto savedOriginInfo = getSormasToSormasOriginInfoFacade().saveOriginInfo(originInfo);

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(savedOriginInfo);
		shareRequest.setStatus(ShareRequestStatus.PENDING);
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getSormasToSormasShareRequestService().count());

		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setSurveillanceOfficer(officer);
			dto.setClassificationUser(officer);
			dto.setSormasToSormasOriginInfo(savedOriginInfo);
		});

		ContactDto contact =
			creator.createContact(officer, officer, person.toReference(), caze, new Date(), new Date(), Disease.CORONAVIRUS, rdcf, c -> {
				c.setSormasToSormasOriginInfo(savedOriginInfo);
			});

		EventDto event = creator
			.createEvent(EventStatus.SCREENING, EventInvestigationStatus.ONGOING, "Test event title", "Test description", officer, null, (e) -> {
				e.getEventLocation().setRegion(rdcf.region);
				e.getEventLocation().setDistrict(rdcf.district);
				e.setSormasToSormasOriginInfo(savedOriginInfo);
			});

		SormasToSormasShareRequestDto sormasToSormasShareRequestDto =
			getSormasToSormasShareRequestFacade().getShareRequestByUuid(shareRequest.getUuid());
		sormasToSormasShareRequestDto.setStatus(ShareRequestStatus.ACCEPTED);
		getSormasToSormasShareRequestFacade().saveShareRequest(sormasToSormasShareRequestDto);

		assertEquals(1, getCaseService().count());
		assertEquals(1, getContactService().count());
		assertEquals(1, getEventService().count());
		assertEquals(1, getSormasToSormasShareRequestService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getCaseService().count());
		assertEquals(1, getContactService().count());
		assertEquals(1, getEventService().count());
		assertEquals(1, getSormasToSormasShareRequestService().count());

		final Date tenYearsPlusAgoForCase = DateUtils.addDays(new Date(), (-1) * caseDeletionConfiguration.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query1 = em.createQuery("select i from cases i where i.uuid=:uuid");
			query1.setParameter("uuid", caze.getUuid());
			Case singleResult1 = (Case) query1.getSingleResult();
			singleResult1.setCreationDate(new Timestamp(tenYearsPlusAgoForCase.getTime()));
			singleResult1.setChangeDate(new Timestamp(tenYearsPlusAgoForCase.getTime()));
			em.persist(singleResult1);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);
		assertEquals(0, getCaseService().count());
		assertEquals(1, getSormasToSormasShareRequestService().count());

		final Date tenYearsPlusAgoForContact = DateUtils.addDays(new Date(), (-1) * contactDeletionConfiguration.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query2 = em.createQuery("select i from contact i where i.uuid=:uuid");
			query2.setParameter("uuid", contact.getUuid());
			Contact singleResult2 = (Contact) query2.getSingleResult();
			singleResult2.setCreationDate(new Timestamp(tenYearsPlusAgoForContact.getTime()));
			singleResult2.setChangeDate(new Timestamp(tenYearsPlusAgoForContact.getTime()));
			em.persist(singleResult2);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);
		assertEquals(0, getCaseService().count());
		assertEquals(0, getContactService().count());
		assertEquals(1, getSormasToSormasShareRequestService().count());

		final Date tenYearsPlusAgoForEvent = DateUtils.addDays(new Date(), (-1) * eventDeletionConfiguration.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query3 = em.createQuery("select i from events i where i.uuid=:uuid");
			query3.setParameter("uuid", event.getUuid());
			Event singleResult3 = (Event) query3.getSingleResult();
			singleResult3.setCreationDate(new Timestamp(tenYearsPlusAgoForEvent.getTime()));
			singleResult3.setChangeDate(new Timestamp(tenYearsPlusAgoForEvent.getTime()));
			em.persist(singleResult3);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);
		assertEquals(0, getCaseService().count());
		assertEquals(0, getContactService().count());
		assertEquals(0, getEventService().count());
		assertEquals(0, getSormasToSormasShareRequestService().count());
	}

	@Test
	public void testSormasToSormasShareInfoPermanentDeletion() {
		createDeletionConfigurations();

		DeletionConfiguration caseDeletionConfiguration = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CASE);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto officer = creator.createSurveillanceOfficer(rdcf).toReference();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();

		CaseDataDto caze = creator.createCase(officer, person.toReference(), rdcf);

		User officerUser = getUserService().getByReferenceDto(officer);
		ShareRequestInfo pendingRequestInfo = createShareRequestInfo(
			ShareRequestDataType.CASE,
			officerUser,
			DEFAULT_SERVER_ID,
			true,
			i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference())));
		pendingRequestInfo.setRequestStatus(ShareRequestStatus.PENDING);
		getShareRequestInfoService().persist(pendingRequestInfo);

		ShareRequestInfo acceptedRequestInfo = createShareRequestInfo(
			ShareRequestDataType.CASE,
			officerUser,
			SECOND_SERVER_ID,
			true,
			i -> i.setCaze(getCaseService().getByReferenceDto(caze.toReference())));
		acceptedRequestInfo.setRequestStatus(ShareRequestStatus.ACCEPTED);
		getShareRequestInfoService().persist(acceptedRequestInfo);

		assertEquals(1, getCaseService().count());
		assertEquals(2, getShareRequestInfoService().count());
		assertEquals(2, getSormasToSormasShareInfoService().count());

		final Date tenYearsPlusAgoForCase = DateUtils.addDays(new Date(), (-1) * caseDeletionConfiguration.deletionPeriod - 1);
		executeInTransaction(em -> {
			Query query1 = em.createQuery("select i from cases i where i.uuid=:uuid");
			query1.setParameter("uuid", caze.getUuid());
			Case singleResult1 = (Case) query1.getSingleResult();
			singleResult1.setCreationDate(new Timestamp(tenYearsPlusAgoForCase.getTime()));
			singleResult1.setChangeDate(new Timestamp(tenYearsPlusAgoForCase.getTime()));
			em.persist(singleResult1);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(0, getCaseService().count());
		assertEquals(0, getShareRequestInfoService().count());
		assertEquals(0, getSormasToSormasShareInfoService().count());
	}

	@Test
	public void testReportDeletionReference() {
		createDeletionConfigurations(DeletableEntityType.CASE, DeletionReference.REPORT);
		DeletionConfiguration caseDeletionConfiguration = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CASE);

		TestDataCreator.RDCF redcf = creator.createRDCF();
		UserDto user = creator.createUser(redcf, creator.getUserRoleReference(DefaultUserRole.ADMIN));

		creator.createCase(user.toReference(), creator.createPerson().toReference(), redcf, c -> {
			c.setReportDate(new Date());
		});

		final Date exceededDeletePereiod = DateUtils.addDays(new Date(), (-1) * caseDeletionConfiguration.deletionPeriod - 1);
		creator.createCase(user.toReference(), creator.createPerson().toReference(), redcf, c -> {
			c.setReportDate(exceededDeletePereiod);
		});

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getCaseService().count());
	}
}
