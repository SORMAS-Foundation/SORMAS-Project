package de.symeda.sormas.backend.therapy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class TreatmentFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testTreatmentDeletion() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf, UserRole.ADMIN);
		PersonDto casePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), casePerson.toReference(), rdcf);
		TreatmentDto treatment = creator.createTreatment(caze);

		// Database should contain the created prescription
		assertNotNull(getTreatmentFacade().getTreatmentByUuid(treatment.getUuid()));

		getTreatmentFacade().deleteTreatment(treatment.getUuid());

		// Database should not contain the deleted visit
		assertNull(getTreatmentFacade().getTreatmentByUuid(treatment.getUuid()));
	}

	@Test
	public void testTreatmentIndexListGeneration() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR);
		PersonDto casePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), casePerson.toReference(), rdcf);
		creator.createTreatment(caze);

		List<TreatmentIndexDto> results = getTreatmentFacade().getIndexList(null);

		assertEquals(1, results.size());
	}
}
