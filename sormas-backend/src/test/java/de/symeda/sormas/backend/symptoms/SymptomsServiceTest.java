package de.symeda.sormas.backend.symptoms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Query;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfiguration;

public class SymptomsServiceTest extends AbstractBeanTest {

	@Test
	public void testCaseVisitAutomaticDeletion() {

		createDeletionConfigurations();
		DeletionConfiguration coreEntityTypeConfig = getDeletionConfigurationService().getEntityTypeConfig(DeletableEntityType.CASE);

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto person1 = creator.createPerson();
		CaseDataDto caze1 = creator.createCase(user.toReference(), person1.toReference(), rdcf);

		creator.createClinicalVisit(caze1, v -> {
			v.setVisitingPerson("John Smith");
			v.setVisitRemarks("Test remarks");

			SymptomsDto symptoms = v.getSymptoms();
			symptoms.setPatientIllLocation("Test ill location");
			symptoms.setOtherHemorrhagicSymptoms(SymptomState.YES);
			symptoms.setOtherHemorrhagicSymptomsText("OtherHemorrhagic");
		});

		VisitDto visit1 = creator.createVisit(caze1.getDisease(), caze1.getPerson(), caze1.getReportDate());
		visit1.getSymptoms().setAnorexiaAppetiteLoss(SymptomState.YES);
		getVisitFacade().save(visit1);

		PersonDto person2 = creator.createPerson();
		CaseDataDto caze2 = creator.createCase(user.toReference(), person2.toReference(), rdcf);

		creator.createClinicalVisit(caze2, v -> {
			v.setVisitingPerson("Thomas");
			v.setVisitRemarks("Test remarks");

			SymptomsDto symptoms = v.getSymptoms();
			symptoms.setPatientIllLocation("Test ill location");
			symptoms.setOtherHemorrhagicSymptoms(SymptomState.YES);
			symptoms.setOtherHemorrhagicSymptomsText("OtherHemorrhagic");
		});

		VisitDto visit2 = creator.createVisit(caze2.getDisease(), caze2.getPerson(), caze2.getReportDate());
		visit2.getSymptoms().setAnorexiaAppetiteLoss(SymptomState.YES);
		getVisitFacade().save(visit2);

		final Date tenYearsPlusAgo = DateUtils.addDays(new Date(), (-1) * coreEntityTypeConfig.getDeletionPeriod() - 1);
		executeInTransaction(em -> {
			Query query = em.createQuery("select c from cases c where c.uuid=:uuid");
			query.setParameter("uuid", caze1.getUuid());
			Case singleResult = (Case) query.getSingleResult();
			singleResult.setCreationDate(new Timestamp(tenYearsPlusAgo.getTime()));
			singleResult.setChangeDate(new Timestamp(tenYearsPlusAgo.getTime()));
			em.persist(singleResult);
		});

		assertEquals(2, getCaseService().count());
		assertEquals(2, getVisitService().count());
		assertEquals(6, getSymptomsService().count());

		useSystemUser();
		getCoreEntityDeletionService().executeAutomaticDeletion();
		loginWith(user);

		assertEquals(1, getCaseService().count());
		assertEquals(1, getVisitService().count());
		assertEquals(3, getSymptomsService().count());
	}

}
