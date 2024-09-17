package de.symeda.sormas.backend.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.utils.DateHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @see SampleService
 */
public class SampleServiceTest extends AbstractBeanTest {
	@Inject
	private EntityManager em;

	@Test
	public void testSamplePermanentDeletion() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		SampleDto referralSample =
			creator.createSample(caze.toReference(), user.toReference(), rdcf.facility, s -> s.setReferredTo(sample.toReference()));
		creator.createPathogenTest(sample.toReference(), caze);
		creator.createAdditionalTest(sample.toReference());
		ExternalMessageDto labMessage = creator.createLabMessageWithTestReport(sample.toReference());

		getSampleFacade().delete(sample.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test reason"));

		Sample sampleEntity = getSampleService().getByUuid(sample.getUuid());
		List<PathogenTest> pathogenTests = getPathogenTestService().getAll();
		assertEquals(2, getSampleService().count());
		assertTrue(sampleEntity.isDeleted());
		assertEquals(1, pathogenTests.size());
		assertTrue(pathogenTests.get(0).isDeleted());
		assertEquals(1, getAdditionalTestService().count());
		assertNotNull(getSampleService().getByUuid(referralSample.getUuid()).getReferredTo());
		assertNotNull(getSampleReportService().getByUuid(labMessage.getSampleReports().get(0).getUuid()).getSample());

		sampleEntity = getSampleService().getByUuid(sample.getUuid());
		getSampleService().deletePermanent(mergeToEntityManager(sampleEntity));

		assertEquals(1, getSampleService().count());
		assertEquals(0, getPathogenTestService().count());
		assertEquals(0, getAdditionalTestService().count());
		assertEquals(1, getExternalMessageService().count());
		assertEquals(1, getSampleReportService().count());
		assertEquals(1, getTestReportService().count());
		assertNull(getSampleService().getByUuid(referralSample.getUuid()).getReferredTo());
		assertNull(getSampleReportService().getByUuid(labMessage.getSampleReports().get(0).getUuid()).getSample());
	}

	@Test
	public void testBuildSampleListCriteriaFilterForCase() throws Exception {
		// Prepare data
		SampleCriteria criteria = new SampleCriteria();
		criteria.sampleAssociationType(SampleAssociationType.CASE);
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
				.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		criteria.caze(caze.toReference());

		// Create a CriteriaBuilder, CriteriaQuery, Root, and SampleJoins
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(Sample.class);
		Root<Sample> root = cq.from(Sample.class);
		SampleJoins joins = new SampleJoins(root);
		// Access private method using reflection
		Method method = SampleService.class.getDeclaredMethod("buildSampleListCriteriaFilter", SampleCriteria.class, CriteriaBuilder.class, SampleJoins.class, From.class);
		method.setAccessible(true);

		Predicate predicate = (Predicate) method.invoke(getSampleService(), criteria, cb, joins, root);

		assertNotNull(predicate);
	}

	@Test
	public void testBuildSampleListCriteriaFilterForContact() throws Exception {
		// Prepare data
		SampleCriteria criteria = new SampleCriteria();
		criteria.sampleAssociationType(SampleAssociationType.CONTACT);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
				.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		ContactDto contactDto = creator.createContact(user.toReference(), creator.createPerson("Contact", "Person").toReference(), caze, rdcf);
		criteria.contact(contactDto.toReference());

		// Create a CriteriaBuilder, CriteriaQuery, Root, and SampleJoins
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(Sample.class);
		Root<Sample> root = cq.from(Sample.class);
		SampleJoins joins = new SampleJoins(root);

		// Access private method using reflection
		Method method = SampleService.class.getDeclaredMethod("buildSampleListCriteriaFilter", SampleCriteria.class, CriteriaBuilder.class, SampleJoins.class, From.class);
		method.setAccessible(true);

		Predicate predicate = (Predicate) method.invoke(getSampleService(), criteria, cb, joins, root);

		assertNotNull(predicate);
	}

	@Test
	public void testBuildSampleListCriteriaFilterForEventParticipant() throws Exception {
		// Prepare data
		SampleCriteria criteria = new SampleCriteria();
		criteria.sampleAssociationType(SampleAssociationType.EVENT_PARTICIPANT);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
				.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		EventDto eventDto = creator.createEvent(
				EventStatus.SIGNAL,
				EventInvestigationStatus.PENDING,
				"Title",
				"Description",
				"First",
				"Name",
				"12345",
				TypeOfPlace.PUBLIC_PLACE,
				DateHelper.subtractDays(new Date(), 1),
				new Date(),
				user.toReference(),
				user.toReference(),
				Disease.EVD,
				rdcf);
		PersonDto eventPerson = creator.createPerson("Event", "Person");
		EventParticipantDto eventParticipantDto = creator.createEventParticipant(eventDto.toReference(), eventPerson, "Description", user.toReference());

		criteria.eventParticipant(eventParticipantDto.toReference());

		// Create a CriteriaBuilder, CriteriaQuery, Root, and SampleJoins
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(Sample.class);
		Root<Sample> root = cq.from(Sample.class);
		SampleJoins joins = new SampleJoins(root);

		// Access private method using reflection
		Method method = SampleService.class.getDeclaredMethod("buildSampleListCriteriaFilter", SampleCriteria.class, CriteriaBuilder.class, SampleJoins.class, From.class);
		method.setAccessible(true);

		Predicate predicate = (Predicate) method.invoke(getSampleService(), criteria, cb, joins, root);

		assertNotNull(predicate);
	}

	@Test
	public void testIsEditAllowed() throws InvocationTargetException, IllegalAccessException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();

		UserDto user = creator
				.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);

		Sample sampleEntity = getSampleService().getByUuid(sample.getUuid());

		getFeatureConfigurationFacade().isFeatureDisabled(FeatureType.EDIT_ARCHIVED_ENTITIES);

		boolean sormasToSormasOriginInfoAndNotOwnershipHandoverStatus = (sample.getSormasToSormasOriginInfo() != null && !sample.getSormasToSormasOriginInfo().isOwnershipHandedOver());
		assertFalse(sormasToSormasOriginInfoAndNotOwnershipHandoverStatus);

		boolean isEditAllowed = getSampleService().isEditAllowed(sampleEntity) && !getSampleService().sampleAssignedToActiveEntity(sample.getUuid());
		assertFalse(isEditAllowed);

		boolean notOwnerShipAndJurisdictionFlagStatus = getSampleService().getJurisdictionFlags(sampleEntity).getInJurisdiction() && !getSormasToSormasShareInfoService().isSamlpeOwnershipHandedOver(sampleEntity);
		assertTrue(notOwnerShipAndJurisdictionFlagStatus);
	}
}
