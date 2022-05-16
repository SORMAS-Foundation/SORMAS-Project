package de.symeda.sormas.backend.therapy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.TreatmentRoute;
import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class TreatmentFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testTreatmentDeletion() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(
			rdcf,
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR),
			creator.getUserRoleReference(DefaultUserRole.CASE_SUPERVISOR));
		UserDto admin = getUserFacade().getByUserName("admin");
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
		UserDto user = creator.createUser(
			rdcf,
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR),
			creator.getUserRoleReference(DefaultUserRole.CASE_SUPERVISOR));
		PersonDto casePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), casePerson.toReference(), rdcf);
		creator.createTreatment(caze);

		List<TreatmentIndexDto> results = getTreatmentFacade().getIndexList(null);

		assertEquals(1, results.size());
	}

	@Test
	public void testTreatmentForPrescription() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR);
		PersonDto casePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), casePerson.toReference(), rdcf);
		PrescriptionDto prescription =  creator.createPrescription(caze);

		TreatmentDto standaloneTreatment = creator.createTreatment(caze);
		TreatmentDto prescriptionTreatment1 = creator.createTreatment(caze, t -> {
			t.setPrescription(prescription.toReference());
		});
		TreatmentDto prescriptionTreatment2 = creator.createTreatment(caze, t -> {
			t.setPrescription(prescription.toReference());
		});

		List<String> prescriptionUuids = new ArrayList<>();
		prescriptionUuids.add(prescription.getUuid());

		List<TreatmentIndexDto> results = getTreatmentFacade().getTreatmentForPrescription(prescriptionUuids);
		assertEquals(2, results.size());
		List<String> treatmentUuids = results.stream().map(t->t.getUuid()).collect(Collectors.toList());
		assertTrue(treatmentUuids.contains(prescriptionTreatment1.getUuid()));
		assertTrue(treatmentUuids.contains(prescriptionTreatment2.getUuid()));
		assertFalse(treatmentUuids.contains(standaloneTreatment.getUuid()));

	}

	@Test
	public void testDeleteTreatmentsByUuids() {
		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR);
		PersonDto casePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), casePerson.toReference(), rdcf);

		TreatmentDto treatment1 = creator.createTreatment(caze);
		TreatmentDto treatment2 = creator.createTreatment(caze);
		TreatmentDto treatment3 = creator.createTreatment(caze);

		List<TreatmentIndexDto> results = getTreatmentFacade().getIndexList(null);
		assertEquals(3, results.size());

		List<String> uuidToDelete = new ArrayList<>();
		uuidToDelete.add(treatment2.getUuid());
		uuidToDelete.add(treatment3.getUuid());

		getTreatmentFacade().deleteTreatments(uuidToDelete);

		results = getTreatmentFacade().getIndexList(null);
		assertEquals(1, results.size());
		assertEquals(treatment1.getUuid(), results.get(0).getUuid());
	}

	@Test
	public void testUnbindTreatmentsFromPrescription() {

		RDCFEntities rdcf = creator.createRDCFEntities();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR);
		PersonDto casePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), casePerson.toReference(), rdcf);
		PrescriptionDto prescription =  creator.createPrescription(caze);

		TreatmentDto prescriptionTreatment1 = creator.createTreatment(caze, t -> {
			t.setPrescription(prescription.toReference());
		});
		TreatmentDto prescriptionTreatment2 = creator.createTreatment(caze, t -> {
			t.setPrescription(prescription.toReference());
		});

		List<String> treatmentUuids = new ArrayList<>();
		treatmentUuids.add(prescriptionTreatment1.getUuid());
		treatmentUuids.add(prescriptionTreatment2.getUuid());

		List<TreatmentDto> treatmentDtos = getTreatmentFacade().getByUuids(treatmentUuids);
		for(TreatmentDto treatmentDto:treatmentDtos){
			assertNotNull(treatmentDto.getPrescription());
			assertEquals(prescription.getUuid(), treatmentDto.getPrescription().getUuid());
		}

		getTreatmentFacade().unlinkPrescriptionFromTreatments(treatmentUuids);

		treatmentDtos = getTreatmentFacade().getByUuids(treatmentUuids);
		for(TreatmentDto treatmentDto:treatmentDtos){
			assertNull(treatmentDto.getPrescription());
		}
	}

}
