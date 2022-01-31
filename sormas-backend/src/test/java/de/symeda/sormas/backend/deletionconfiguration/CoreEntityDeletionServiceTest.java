package de.symeda.sormas.backend.deletionconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;
import java.util.Date;

import de.symeda.sormas.backend.caze.Case;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.spi.QueryImplementor;
import org.joda.time.LocalDate;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class CoreEntityDeletionServiceTest extends AbstractBeanTest {

	@Test
	public void testCaseAutomaticDeletion() {

		createDeletionConfiguration();
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

		getCoreEntityDeletionService().executeAutomaticDeletion();

		assertEquals(0, getCaseFacade().count(caseCriteria));

	}

	private void createDeletionConfiguration() {
		build(CoreEntityType.CASE);
		build(CoreEntityType.CONTACT);
		build(CoreEntityType.EVENT);
		build(CoreEntityType.EVENT_PARTICIPANT);
		build(CoreEntityType.IMMUNIZATION);
		build(CoreEntityType.TRAVEL_ENTRY);
	}

	private DeletionConfiguration build(CoreEntityType coreEntityType) {
		DeletionConfigurationService deletionConfigurationService = getBean(DeletionConfigurationService.class);

		DeletionConfiguration entity = new DeletionConfiguration();
		entity.setEntityType(coreEntityType);
		entity.setDeletionReference(coreEntityType.getDeletionReference());
		entity.setDeletionPeriod(3650);
		deletionConfigurationService.ensurePersisted(entity);
		return entity;
	}
}
