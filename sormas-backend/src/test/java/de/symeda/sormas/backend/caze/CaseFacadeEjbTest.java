package de.symeda.sormas.backend.caze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.util.DtoHelper;

public class CaseFacadeEjbTest extends AbstractBeanTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testDiseaseChangeUpdatesContacts() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(),
				caze.toReference(), new Date(), new Date());

		// Follow-up status and duration should be set to the requirements for EVD
		assertEquals(FollowUpStatus.FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(LocalDate.now().plusDays(21), DateHelper8.toLocalDate(contact.getFollowUpUntil()));

		caze.setDisease(Disease.MEASLES);
		caze = getCaseFacade().saveCase(caze);

		// Follow-up status and duration should be set to no follow-up and null
		// respectively because
		// Measles does not require a follow-up
		contact = getContactFacade().getContactByUuid(contact.getUuid());
		assertEquals(FollowUpStatus.NO_FOLLOW_UP, contact.getFollowUpStatus());
		assertEquals(null, contact.getFollowUpUntil());
	}

	@Test
	public void testMovingCaseUpdatesTaskAssigneeAndCreatesPreviousHospitalization() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		UserDto caseOfficer = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(),
				rdcf.facility.getUuid(), "Case", "Officer", UserRole.CASE_OFFICER);
		TaskDto pendingTask = creator.createTask(TaskContext.CASE, TaskType.CASE_INVESTIGATION, TaskStatus.PENDING,
				caze.toReference(), null, new Date(), user.toReference());
		TaskDto doneTask = creator.createTask(TaskContext.CASE, TaskType.CASE_INVESTIGATION, TaskStatus.DONE,
				caze.toReference(), null, new Date(), user.toReference());

		RDCF newRDCF = creator.createRDCF("New Region", "New District", "New Community", "New Facility");
		getCaseFacade().transferCase(caze.toReference(),
				getCommunityFacade().getCommunityReferenceByUuid(newRDCF.community.getUuid()),
				getFacilityFacade().getFacilityReferenceByUuid(newRDCF.facility.getUuid()),
				caze.getHealthFacilityDetails(), caseOfficer.toReference());

		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());
		pendingTask = getTaskFacade().getByUuid(pendingTask.getUuid());
		doneTask = getTaskFacade().getByUuid(doneTask.getUuid());

		// Case should have the new region, district, community and facility set
		assertEquals(caze.getRegion().getUuid(), newRDCF.region.getUuid());
		assertEquals(caze.getDistrict().getUuid(), newRDCF.district.getUuid());
		assertEquals(caze.getCommunity().getUuid(), newRDCF.community.getUuid());
		assertEquals(caze.getHealthFacility().getUuid(), newRDCF.facility.getUuid());

		// Pending task should've been reassigned to the case officer, done task should
		// still be assigned to the surveillance supervisor
		assertEquals(pendingTask.getAssigneeUser().getUuid(), caseOfficer.getUuid());
		assertEquals(doneTask.getAssigneeUser().getUuid(), user.getUuid());

		// A previous hospitalization with the former facility should have been created
		List<PreviousHospitalizationDto> previousHospitalizations = caze.getHospitalization()
				.getPreviousHospitalizations();
		assertEquals(previousHospitalizations.size(), 1);
	}

	@Test
	public void testDashboardCaseListCreation() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);

		List<DashboardCaseDto> dashboardCaseDtos = getCaseFacade().getNewCasesForDashboard(caze.getRegion(),
				caze.getDistrict(), caze.getDisease(), DateHelper.subtractDays(new Date(), 1),
				DateHelper.addDays(new Date(), 1), user.getUuid());

		// List should have one entry
		assertEquals(1, dashboardCaseDtos.size());
	}

	@Test
	public void testMapCaseListCreation() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);

		List<MapCaseDto> mapCaseDtos = getCaseFacade().getCasesForMap(caze.getRegion(), caze.getDistrict(),
				caze.getDisease(), DateHelper.subtractDays(new Date(), 1), DateHelper.addDays(new Date(), 1),
				user.getUuid());

		// List should have one entry
		assertEquals(1, mapCaseDtos.size());
	}

	@Test
	public void testGetIndexList() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD, CaseClassification.PROBABLE,
				InvestigationStatus.PENDING, new Date(), rdcf);

		List<CaseIndexDto> results = getCaseFacade().getIndexList(user.getUuid(), null);

		// List should have one entry
		assertEquals(1, results.size());
	}

	@Test
	public void testCaseDeletion() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		String userUuid = user.getUuid();
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(),
				new Date(), new Date());
		TaskDto task = creator.createTask(TaskContext.CASE, TaskType.CASE_INVESTIGATION, TaskStatus.PENDING,
				caze.toReference(), null, new Date(), user.toReference());
		creator.createSample(caze.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD,
				rdcf.facility);

		// Database should contain one case, associated contact, task and sample
		assertEquals(1, getCaseFacade().getAllCasesAfter(null, userUuid).size());
		assertEquals(1, getContactFacade().getAllContactsAfter(null, userUuid).size());
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));
		assertEquals(1, getSampleFacade().getAllAfter(null, userUuid).size());

		getCaseFacade().deleteCase(caze.toReference(), adminUuid);

		// Database should contain no case and associated contact, task or sample
		assertEquals(0, getCaseFacade().getAllCasesAfter(null, userUuid).size());
		assertEquals(0, getContactFacade().getAllContactsAfter(null, userUuid).size());
		assertNull(getTaskFacade().getByUuid(task.getUuid()));
		assertEquals(0, getSampleFacade().getAllAfter(null, userUuid).size());
	}

	@Test
	public void testOutcomePersonConditionUpdate() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto firstCase = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);

		// case deceased -> person has to set to dead
		firstCase.setOutcome(CaseOutcome.DECEASED);
		firstCase = getCaseFacade().saveCase(firstCase);
		assertNotNull(firstCase.getOutcomeDate());
		cazePerson = getPersonFacade().getPersonByUuid(cazePerson.getUuid());
		assertEquals(PresentCondition.DEAD, cazePerson.getPresentCondition());
		assertEquals(firstCase.getDisease(), cazePerson.getCauseOfDeathDisease());

		// case has no outcome again -> person should be alive
		firstCase.setOutcome(CaseOutcome.NO_OUTCOME);
		firstCase = getCaseFacade().saveCase(firstCase);
		assertNull(firstCase.getOutcomeDate());
		cazePerson = getPersonFacade().getPersonByUuid(cazePerson.getUuid());
		assertEquals(PresentCondition.ALIVE, cazePerson.getPresentCondition());

		// case deceased -> person has to set to dead
		firstCase.setOutcome(CaseOutcome.DECEASED);
		firstCase = getCaseFacade().saveCase(firstCase);
		cazePerson = getPersonFacade().getPersonByUuid(cazePerson.getUuid());
		assertEquals(PresentCondition.DEAD, cazePerson.getPresentCondition());

		// person alive again -> case has to be reset to no outcome
		cazePerson.setPresentCondition(PresentCondition.ALIVE);
		cazePerson = getPersonFacade().savePerson(cazePerson);

		firstCase = getCaseFacade().getCaseDataByUuid(firstCase.getUuid());
		assertEquals(CaseOutcome.NO_OUTCOME, firstCase.getOutcome());
		assertNull(firstCase.getOutcomeDate());

		// additional case for the the person. set to deceased -> person has to be dead
		// and other no outcome cases have to be set to deceased
		CaseDataDto secondCase = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		secondCase.setOutcome(CaseOutcome.RECOVERED);
		secondCase = getCaseFacade().saveCase(secondCase);
		CaseDataDto thirdCase = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		thirdCase.setOutcome(CaseOutcome.DECEASED);
		thirdCase = getCaseFacade().saveCase(thirdCase);

		cazePerson = getPersonFacade().getPersonByUuid(cazePerson.getUuid());
		assertEquals(PresentCondition.DEAD, cazePerson.getPresentCondition());
		firstCase = getCaseFacade().getCaseDataByUuid(firstCase.getUuid());
		assertEquals(CaseOutcome.DECEASED, firstCase.getOutcome());
		assertNotNull(firstCase.getOutcomeDate());
		secondCase = getCaseFacade().getCaseDataByUuid(secondCase.getUuid());
		assertEquals(CaseOutcome.RECOVERED, secondCase.getOutcome());

		// person alive again -> deceased cases have to be set to no outcome
		cazePerson.setPresentCondition(PresentCondition.ALIVE);
		cazePerson = getPersonFacade().savePerson(cazePerson);
		firstCase = getCaseFacade().getCaseDataByUuid(firstCase.getUuid());
		assertEquals(CaseOutcome.NO_OUTCOME, firstCase.getOutcome());
		secondCase = getCaseFacade().getCaseDataByUuid(secondCase.getUuid());
		assertEquals(CaseOutcome.RECOVERED, secondCase.getOutcome());
		thirdCase = getCaseFacade().getCaseDataByUuid(thirdCase.getUuid());
		assertEquals(CaseOutcome.NO_OUTCOME, thirdCase.getOutcome());
	}

	@Test
	public void testOutcomePersonConditionUpdateForAppSync() throws InterruptedException {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto firstCase = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);

		// simulate short delay between transmissions
		Thread.sleep(DtoHelper.CHANGE_DATE_TOLERANCE_MS + 1);

		// case deceased -> person has to set to dead
		firstCase.setOutcome(CaseOutcome.DECEASED);
		cazePerson.setPresentCondition(PresentCondition.DEAD);
		cazePerson = getPersonFacade().savePerson(cazePerson);

		// this should throw an exception
		exception.expect(UnsupportedOperationException.class);
		firstCase = getCaseFacade().saveCase(firstCase);
	}

	@Test
	public void testExportHadContactWithConfirmedCase() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(),
				caze.toReference(), new Date(), new Date());
		CaseDataDto resultingCase = creator.createCase(user.toReference(), contactPerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		contact.setContactClassification(ContactClassification.CONFIRMED);
		contact = getContactFacade().saveContact(contact);
		assertNotNull(contact.getResultingCase());

		List<CaseExportDto> exportList = getCaseFacade().getExportList(user.getUuid(), new CaseCriteria());
		assertEquals(YesNoUnknown.NO, exportList.get(1).getContactWithConfirmedCase());

		caze.setCaseClassification(CaseClassification.CONFIRMED);
		getCaseFacade().saveCase(caze);

		exportList = getCaseFacade().getExportList(user.getUuid(), new CaseCriteria());
		assertEquals(YesNoUnknown.YES, exportList.get(1).getContactWithConfirmedCase());
	}

}
