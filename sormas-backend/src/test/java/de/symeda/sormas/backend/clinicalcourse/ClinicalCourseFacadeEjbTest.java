package de.symeda.sormas.backend.clinicalcourse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitIndexDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class ClinicalCourseFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testClinicalVisitDeletion() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(
			rdcf,
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR),
			creator.getUserRoleReference(DefaultUserRole.CASE_SUPERVISOR));
		UserDto admin = getUserFacade().getByUserName("admin");
		PersonDto casePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), casePerson.toReference(), rdcf);
		ClinicalVisitDto visit = creator.createClinicalVisit(caze);

		// Database should contain the created clinical visit
		assertNotNull(getClinicalVisitFacade().getClinicalVisitByUuid(visit.getUuid()));

		getClinicalVisitFacade().deleteClinicalVisit(visit.getUuid());

		// Database should not contain the deleted visit
		assertNull(getClinicalVisitFacade().getClinicalVisitByUuid(visit.getUuid()));
	}

	@Test
	public void testClinicalVisitIndexListGeneration() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(
			rdcf,
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR),
			creator.getUserRoleReference(DefaultUserRole.CASE_SUPERVISOR));
		PersonDto casePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), casePerson.toReference(), rdcf);
		creator.createClinicalVisit(caze);

		List<ClinicalVisitIndexDto> results = getClinicalVisitFacade().getIndexList(null, null, null, null);

		assertEquals(1, results.size());
	}
}
