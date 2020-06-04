package de.symeda.sormas.backend.clinicalcourse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitIndexDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class ClinicalCourseFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testClinicalVisitDeletion() {
		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf, UserRole.ADMIN);
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
		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR);
		PersonDto casePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), casePerson.toReference(), rdcf);
		creator.createClinicalVisit(caze);
		
		List<ClinicalVisitIndexDto> results = getClinicalVisitFacade().getIndexList(null);
		
		assertEquals(1, results.size());
	}
	
}
