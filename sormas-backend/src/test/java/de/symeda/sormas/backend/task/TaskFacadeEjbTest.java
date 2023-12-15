/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class TaskFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testTaskDirectoryForDeletedLinkedCase() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.userRoleDtoMap.get(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		TaskDto task = creator.createTask(
			TaskContext.CASE,
			TaskType.OTHER,
			TaskStatus.PENDING,
			caze.toReference(),
			null,
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());

		getCaseFacade().delete(caze.getUuid(), new DeletionDetails());

		List<TaskIndexDto> tasks =
			getTaskFacade().getIndexList(new TaskCriteria().relevanceStatus(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED), 0, 100, null);
		assertEquals(0, tasks.size());
	}

	@Test
	public void testSampleDeletion() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		UserDto admin = getUserFacade().getByUserName("admin");
		String adminUuid = admin.getUuid();
		TaskDto task = creator.createTask(
			TaskContext.GENERAL,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		// Database should contain the created task
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));

		getTaskFacade().delete(task.getUuid());

		// Database should not contain the created task
		assertNull(getTaskFacade().getByUuid(task.getUuid()));
	}

	@Test
	public void testGetIndexList() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		// Database should contain the created task
		assertNotNull(getTaskFacade().getIndexList(null, 0, 100, null));
	}

	@Test
	public void testGetIndexListByARestrictedAccessToAssignedEntitiesUser() {
		RDCF rdcf = creator.createRDCF();

		UserDto user = creator.createNationalUser();
		UserDto surveillanceOfficer = creator.createSurveillanceOfficer(rdcf);
		assertNotNull(getTaskFacade().getIndexList(null, 0, 100, null));

		//test tasks related to cases
		loginWith(user);
		String lastName = "Person";
		PersonDto cazePerson = creator.createPerson("Case", lastName);
		final CaseDataDto firstCase = creator.createCase(
			surveillanceOfficer.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		final CaseDataDto secondCase = creator.createCase(
			surveillanceOfficer.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf,
			c -> c.setHealthFacilityDetails("abc"));
		assertEquals(2, getTaskFacade().getIndexList(null, 0, 100, null).size());

		useNationalAdminLogin();
		UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities =
			creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertTrue(getCurrentUserService().hasRestrictedAccessToAssignedEntities());
		assertEquals(0, getTaskFacade().getIndexList(null, 0, 100, null).size());

		loginWith(user);
		firstCase.setSurveillanceOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getCaseFacade().save(firstCase);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(1, getTaskFacade().getIndexList(null, 0, 100, null).size());

		//test tasks related to contacts
		loginWith(user);
		PersonDto contactPerson = creator.createPerson("Contact2", "Person2");
		final ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), null, new Date(), new Date(), Disease.EVD);
		assertEquals(3, getTaskFacade().getIndexList(null, 0, 100, null).size());
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(1, getTaskFacade().getIndexList(null, 0, 100, null).size());
		loginWith(user);
		contact.setContactOfficer(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getContactFacade().save(contact);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(2, getTaskFacade().getIndexList(null, 0, 100, null).size());

		//test tasks related to events
		loginWith(user);
		final EventDto event = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"TitleEv1",
			"DescriptionEv1",
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
		creator.createTask(
			TaskContext.EVENT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			event.toReference(),
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		assertEquals(4, getTaskFacade().getIndexList(null, 0, 100, null).size());

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(2, getTaskFacade().getIndexList(null, 0, 100, null).size());

		loginWith(user);
		event.setResponsibleUser(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getEventFacade().save(event);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(3, getTaskFacade().getIndexList(null, 0, 100, null).size());

		//test tasks related to environments
		loginWith(user);
		EnvironmentDto environment = creator.createEnvironment("Test Environment", EnvironmentMedia.WATER, user.toReference(), rdcf);
		creator.createTask(
			TaskContext.ENVIRONMENT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			null,
			environment.toReference(),
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		assertEquals(5, getTaskFacade().getIndexList(null, 0, 100, null).size());
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(3, getTaskFacade().getIndexList(null, 0, 100, null).size());
		loginWith(user);
		environment.setResponsibleUser(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getEnvironmentFacade().save(environment);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(4, getTaskFacade().getIndexList(null, 0, 100, null).size());
	}

	@Test
	public void testArchivedTaskNotGettingTransfered() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		EventDto event = creator.createEvent(
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

		creator.createTask(
			TaskContext.GENERAL,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		creator.createTask(
			TaskContext.CASE,
			TaskType.OTHER,
			TaskStatus.PENDING,
			caze.toReference(),
			null,
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		creator.createTask(
			TaskContext.CONTACT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			contact.toReference(),
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		creator.createTask(
			TaskContext.EVENT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			event.toReference(),
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		// getAllActiveTasks and getAllUuids should return length 4+1+1 (case investigation & contact investigation)
		assertEquals(6, getTaskFacade().getAllActiveTasksAfter(null).size());
		assertEquals(6, getTaskFacade().getAllActiveUuids().size());

		getCaseFacade().archive(caze.getUuid(), null);
		getEventFacade().archive(event.getUuid(), null);

		// getAllActiveTasks and getAllUuids should return length 3
		assertEquals(3, getTaskFacade().getAllActiveTasksAfter(null).size());
		assertEquals(3, getTaskFacade().getAllActiveUuids().size());

		getContactFacade().archive(contact.getUuid(), null);

		// getAllActiveTasks and getAllUuids should return length 1
		assertEquals(1, getTaskFacade().getAllActiveTasksAfter(null).size());
		assertEquals(1, getTaskFacade().getAllActiveUuids().size());

		getCaseFacade().dearchive(Collections.singletonList(caze.getUuid()), null);
		getEventFacade().dearchive(Collections.singletonList(event.getUuid()), null);
		getContactFacade().dearchive(Collections.singletonList(contact.getUuid()), null);

		// getAllActiveTasks and getAllUuids should return length 5 + 1 (contact investigation)
		assertEquals(6, getTaskFacade().getAllActiveTasksAfter(null).size());
		assertEquals(6, getTaskFacade().getAllActiveUuids().size());
	}

	@Test
	public void testGetAllActiveTasksBatched() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact =
			creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze, new Date(), new Date(), null);
		EventDto event = creator.createEvent(
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

		creator.createTask(
			TaskContext.GENERAL,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		creator.createTask(
			TaskContext.CASE,
			TaskType.OTHER,
			TaskStatus.PENDING,
			caze.toReference(),
			null,
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		creator.createTask(
			TaskContext.CONTACT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			contact.toReference(),
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		creator.createTask(
			TaskContext.EVENT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			event.toReference(),
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());

		// getAllActiveTasks batched
		List<TaskDto> allActiveTasksAfterBatched = getTaskFacade().getAllActiveTasksAfter(null, 3, null);
		assertEquals(3, allActiveTasksAfterBatched.size());
		assertTrue(allActiveTasksAfterBatched.get(0).getChangeDate().getTime() <= allActiveTasksAfterBatched.get(1).getChangeDate().getTime());
		assertTrue(allActiveTasksAfterBatched.get(1).getChangeDate().getTime() <= allActiveTasksAfterBatched.get(2).getChangeDate().getTime());

		List<TaskDto> allActiveTasksAfterLargeBatch = getTaskFacade().getAllActiveTasksAfter(null, 10, null);
		assertEquals(6, allActiveTasksAfterLargeBatch.size());

		TaskDto taskRead = allActiveTasksAfterBatched.get(2);
		List<TaskDto> allActiveTasksAfterBatchedOneMillisecondBefore =
			getTaskFacade().getAllActiveTasksAfter(new Date(taskRead.getChangeDate().getTime() - 1L), 10, EntityDto.NO_LAST_SYNCED_UUID);
		assertEquals(4, allActiveTasksAfterBatchedOneMillisecondBefore.size());

		// getAllActiveTasks batched with Uuid
		List<TaskDto> allActiveTasksAfterBatchedSameTime =
			getTaskFacade().getAllActiveTasksAfter(taskRead.getChangeDate(), 10, "AAAAAA-AAAAAA-AAAAAA-AAAAAA");
		assertEquals(4, allActiveTasksAfterBatchedSameTime.size());

		List<TaskDto> allActiveTasksAfterBatchedSameTimeSameUuid =
			getTaskFacade().getAllActiveTasksAfter(taskRead.getChangeDate(), 10, taskRead.getUuid());
		assertEquals(3, allActiveTasksAfterBatchedSameTimeSameUuid.size());

		TaskService taskService = getBean(TaskService.class);
		Task byUuid = taskService.getByUuid(taskRead.getUuid());

		Task taskWithNanoseconds = new Task();
		Timestamp changeDate = new Timestamp(taskRead.getChangeDate().getTime());
		changeDate.setNanos(changeDate.getNanos() + 150000);
		taskWithNanoseconds.setChangeDate(changeDate);
		taskWithNanoseconds.setUuid("ZZZZZZ-ZZZZZZ-ZZZZZZ-ZZZZZZ");
		taskWithNanoseconds.setId(null);
		taskWithNanoseconds.setTaskContext(byUuid.getTaskContext());
		taskWithNanoseconds.setTaskType(byUuid.getTaskType());
		taskWithNanoseconds.setCaze(byUuid.getCaze());
		taskWithNanoseconds.setContact(byUuid.getContact());
		taskWithNanoseconds.setTaskStatus(byUuid.getTaskStatus());
		taskWithNanoseconds.setCreatorUser(byUuid.getCreatorUser());
		taskWithNanoseconds.setAssigneeUser(byUuid.getAssigneeUser());

		taskService.ensurePersisted(taskWithNanoseconds);

		assertEquals(changeDate.getTime(), taskService.getByUuid("ZZZZZZ-ZZZZZZ-ZZZZZZ-ZZZZZZ").getChangeDate().getTime());

		List<TaskDto> allAfterSeveralResultsSameTime = getTaskFacade().getAllActiveTasksAfter(taskRead.getChangeDate(), 10, taskRead.getUuid());
		assertEquals(4, allAfterSeveralResultsSameTime.size());

		List<TaskDto> allAfterSeveralResultsSameTimeWithUuid =
			getTaskFacade().getAllActiveTasksAfter(taskRead.getChangeDate(), 10, "AAAAAA-AAAAAA-AAAAAA-AAAAAA");
		assertEquals(5, allAfterSeveralResultsSameTimeWithUuid.size());

		assertEquals(taskRead.getUuid(), allAfterSeveralResultsSameTimeWithUuid.get(0).getUuid());
		assertEquals("ZZZZZZ-ZZZZZZ-ZZZZZZ-ZZZZZZ", allAfterSeveralResultsSameTimeWithUuid.get(1).getUuid());
	}

	@Test
	public void testFilterTasksByUserJurisdiction() {

		RDCF rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		CommunityDto c2 = creator.createCommunity("Community 2", rdcf1.district);

		// Create users
		UserDto survSup = creator
			.createUser(rdcf1.region.getUuid(), null, null, "Surv", "Sup", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		UserDto survOff = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			null,
			"Surv",
			"Off",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
		UserDto commInf = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			c2.getUuid(),
			null,
			"Comm",
			"Inf",
			creator.getUserRoleReference(DefaultUserRole.COMMUNITY_INFORMANT));

		// 1. Region level user without a task
		loginWith(survSup);
		assertThat(getTaskFacade().getIndexList(null, 0, 100, null), is(empty()));

		// 2a. District level user with task
		loginWith(survOff);
		assertThat(getTaskFacade().getIndexList(null, 0, 100, null), is(empty()));

		CaseDataDto caze = creator.createCase(survOff.toReference(), creator.createPerson("First", "Last").toReference(), rdcf1);
		creator.createTask(
			TaskContext.CASE,
			TaskType.CASE_INVESTIGATION,
			TaskStatus.PENDING,
			caze.toReference(),
			null,
			null,
			null,
			new Date(),
			survOff.toReference());
		assertThat(getTaskFacade().getIndexList(null, 0, 100, null), is(not(empty())));

		// 2b. Region user now sees tasks from district level
		loginWith(survSup);
		assertThat(getTaskFacade().getIndexList(null, 0, 100, null), is(not(empty())));

		// 3. Community level user does not see task of district level user
		loginWith(commInf);
		assertThat(getTaskFacade().getIndexList(null, 0, 100, null), is(empty()));

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2019);
		List<TaskDto> activeTasks = getTaskFacade().getAllActiveTasksAfter(calendar.getTime());
		assertThat(activeTasks, is(empty()));

		List<TaskDto> tasksByCase = getTaskFacade().getAllByCase(caze.toReference());
		assertThat(tasksByCase, is(empty()));
	}

	@Test
	public void testGetAllTasksForUsersWithoutRights() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator
			.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.ADMIN), creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		user.setDistrict(null);
		user.setCommunity(null);
		user.setHealthFacility(null);
		getUserFacade().saveUser(user, false);
		UserDto userCaseOfficer = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.CASE_OFFICER));
		loginWith(user);

		PersonDto person = creator.createPerson("case person", "ln");
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		ContactDto contact = creator.createContact(user.toReference(), person.toReference(), caze.getDisease());

		creator.createTask(TaskContext.CONTACT, contact.toReference(), t -> {
			t.setTaskStatus(TaskStatus.PENDING);
			t.setAssigneeUser(user.toReference());
		});

		creator.createTask(TaskContext.CONTACT, contact.toReference(), t -> {
			t.setTaskStatus(TaskStatus.PENDING);
			t.setAssigneeUser(userCaseOfficer.toReference());
		});

		List<TaskIndexDto> tasks = getTaskFacade().getIndexList(null, 0, 100, null);
		List<TaskIndexDto> contactTasks = tasks.stream().filter(t -> t.getTaskContext().equals(TaskContext.CONTACT)).collect(Collectors.toList());
		assertEquals(2, contactTasks.size());

		loginWith(userCaseOfficer);

		tasks = getTaskFacade().getIndexList(null, 0, 100, null);
		contactTasks = tasks.stream().filter(t -> t.getTaskContext().equals(TaskContext.CONTACT)).collect(Collectors.toList());
		assertEquals(1, contactTasks.size());
	}

	@Test
	public void testGetPendingTaskCountPerUser() {

		List<String> userUuids;

		// 0. empty or not present uuid
		userUuids = Collections.emptyList();
		assertThat(getTaskFacade().getPendingTaskCountPerUser(userUuids).entrySet(), is(empty()));
		userUuids = Collections.singletonList(DataHelper.createUuid());
		assertThat(getTaskFacade().getPendingTaskCountPerUser(userUuids).entrySet(), is(empty()));

		// 1. one user with tasks, one without
		RDCF rdcf = new RDCF(creator.createRDCFEntities());
		UserDto user1 = creator.createSurveillanceSupervisor(rdcf);
		UserDto user2 = creator.createUser(rdcf, "Second", "User", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		creator.createTask(user1.toReference());

		userUuids = Arrays.asList(user1.getUuid(), user2.getUuid());
		Map<String, Long> taskCounts = getTaskFacade().getPendingTaskCountPerUser(userUuids);
		assertThat(taskCounts.size(), is(1));
		assertThat(taskCounts.get(user1.getUuid()), is(1L));

		// 2. both users have tasks
		creator.createTask(user1.toReference());
		creator.createTask(user2.toReference());

		taskCounts = getTaskFacade().getPendingTaskCountPerUser(userUuids);
		assertThat(taskCounts.size(), is(2));
		assertThat(taskCounts.get(user1.getUuid()), is(2L));
		assertThat(taskCounts.get(user2.getUuid()), is(1L));
	}

	@Test
	public void testAllTaskAndDisplayCaseResponsibleRegion() {
		RDCF rdcf1 = new RDCF(creator.createRDCFEntities("Region1", "District1", "Community1", "Facility1"));
		RDCF rdcf2 = new RDCF(creator.createRDCFEntities("Region2", "District2", "Community2", "Facility2"));

		UserDto user = creator.createUser(rdcf1, creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		PersonDto person = creator.createPerson();
		creator.createCase(user.toReference(), rdcf1, (c) -> {
			c.setPerson(person.toReference());
			c.setDisease(Disease.CORONAVIRUS);
			c.setRegion(rdcf1.region);
			c.setDistrict(rdcf1.district);
			c.setCommunity(rdcf1.community);
			c.setResponsibleRegion(rdcf2.region);
			c.setResponsibleDistrict(rdcf2.district);
			c.setResponsibleCommunity(rdcf2.community);
			c.setReportDate(new Date());
		});

		List<TaskIndexDto> taskIndexDtos = getTaskFacade().getIndexList(null, 0, 100, null);
		assertEquals(1, taskIndexDtos.size());
		assertEquals(rdcf2.region, taskIndexDtos.get(0).getRegion());
		assertEquals(rdcf2.district, taskIndexDtos.get(0).getDistrict());
		assertEquals(rdcf2.community, taskIndexDtos.get(0).getCommunity());
	}

	@Test
	public void testGetTaskListForUserWithoutEventViewRight() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto personDto = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), personDto.toReference(), rdcf);

		creator.createTask(
			TaskContext.GENERAL,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());

		creator.createTask(
			TaskContext.CASE,
			TaskType.OTHER,
			TaskStatus.PENDING,
			caze.toReference(),
			null,
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());

		ContactDto contactDto = creator.createContact(rdcf, user.toReference(), personDto.toReference());
		creator.createTask(
			TaskContext.CONTACT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			contactDto.toReference(),
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());

		EventDto eventDto = creator.createEvent(user.toReference());
		creator.createTask(
			TaskContext.EVENT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			eventDto.toReference(),
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());

		TravelEntryDto travelEntryDto = creator
			.createTravelEntry(personDto.toReference(), user.toReference(), Disease.CORONAVIRUS, rdcf.region, rdcf.district, rdcf.pointOfEntry);
		creator.createTask(TaskContext.TRAVEL_ENTRY, travelEntryDto.toReference(), t -> {
			t.setTaskStatus(TaskStatus.PENDING);
			t.setAssigneeUser(user.toReference());
		});

		TaskCriteria taskCriteria = new TaskCriteria();
		taskCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);

		List<TaskIndexDto> taskIndexDtos = getTaskFacade().getIndexList(taskCriteria, 0, 100, null);
		Set<TaskContext> taskContexts = taskIndexDtos.stream().map(t -> t.getTaskContext()).collect(Collectors.toSet());
		assertEquals(5, taskContexts.size());
		assertTrue(
			taskContexts
				.containsAll(Arrays.asList(TaskContext.GENERAL, TaskContext.CASE, TaskContext.CONTACT, TaskContext.EVENT, TaskContext.TRAVEL_ENTRY)));

		UserDto noEventNoCaseViewUser = creator.createUser(
			"",
			"",
			"",
			"NoEve",
			"NoCase",
			creator.createUserRole(
				"NoEventNoCaseView",
				JurisdictionLevel.NATION,
				UserRight.CASE_VIEW,
				UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS,
				UserRight.TRAVEL_ENTRY_VIEW,
				UserRight.PERSON_VIEW));
		loginWith(noEventNoCaseViewUser);
		assertFalse(getUserService().hasRight(UserRight.EVENT_VIEW));
		assertFalse(getUserService().hasRight(UserRight.CONTACT_VIEW));
		assertTrue(getUserService().hasRight(UserRight.CASE_VIEW));
		assertTrue(getUserService().hasRight(UserRight.TRAVEL_ENTRY_VIEW));

		taskIndexDtos = getTaskFacade().getIndexList(taskCriteria, 0, 100, null);
		assertNotNull(taskIndexDtos);
		taskContexts = taskIndexDtos.stream().map(t -> t.getTaskContext()).collect(Collectors.toSet());
		assertEquals(3, taskContexts.size());
		assertTrue(taskContexts.containsAll(Arrays.asList(TaskContext.GENERAL, TaskContext.CASE, TaskContext.TRAVEL_ENTRY)));
		assertFalse(taskContexts.contains(TaskContext.CONTACT));
		assertFalse(taskContexts.contains(TaskContext.EVENT));
	}

	@Test
	public void testUserWithoutEventViewRightSeeHisAssignTask() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		UserDto noEventNoCaseViewUser = creator.createUser(
			rdcf,
			creator.createUserRole(
				"NoEventNoCaseView",
				JurisdictionLevel.NATION,
				UserRight.CASE_VIEW,
				UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS,
				UserRight.TRAVEL_ENTRY_VIEW,
				UserRight.PERSON_VIEW));

		EventDto eventDto = creator.createEvent(user.toReference());
		TaskDto taskEvent1 = creator.createTask(
			TaskContext.EVENT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			eventDto.toReference(),
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		TaskDto taskEvent2 = creator.createTask(
			TaskContext.EVENT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			eventDto.toReference(),
			null,
			DateHelper.addDays(new Date(), 1),
			noEventNoCaseViewUser.toReference());

		TaskCriteria taskCriteria = new TaskCriteria();
		taskCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);

		List<TaskIndexDto> taskIndexDtos = getTaskFacade().getIndexList(taskCriteria, 0, 100, null);
		assertEquals(2, taskIndexDtos.size());
		List<String> tasksIds = taskIndexDtos.stream().map(t -> t.getUuid()).collect(Collectors.toList());
		assertTrue(tasksIds.contains(taskEvent1.getUuid()));
		assertTrue(tasksIds.contains(taskEvent2.getUuid()));

		loginWith(noEventNoCaseViewUser);
		assertFalse(getUserService().hasRight(UserRight.EVENT_VIEW));
		assertFalse(getUserService().hasRight(UserRight.CONTACT_VIEW));
		assertTrue(getUserService().hasRight(UserRight.CASE_VIEW));
		assertTrue(getUserService().hasRight(UserRight.TRAVEL_ENTRY_VIEW));

		taskIndexDtos = getTaskFacade().getIndexList(taskCriteria, 0, 100, null);
		assertNotNull(taskIndexDtos);
		tasksIds = taskIndexDtos.stream().map(t -> t.getUuid()).collect(Collectors.toList());
		assertFalse(tasksIds.contains(taskEvent1.getUuid()));
		assertTrue(tasksIds.contains(taskEvent2.getUuid()));
	}

	@Test
	public void testUserWithoutEventViewRightSeeHisObservedTask() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		UserDto noEventNoCaseViewUser = creator.createUser(
			rdcf,
			creator.createUserRole(
				"NoEventNoCaseView",
				JurisdictionLevel.NATION,
				UserRight.CASE_VIEW,
				UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS,
				UserRight.TRAVEL_ENTRY_VIEW,
				UserRight.PERSON_VIEW));

		EventDto eventDto = creator.createEvent(user.toReference());

		TaskDto taskEvent1 = creator.createTask(TaskContext.EVENT, eventDto.toReference(), t -> {
			t.setObserverUsers(Stream.of(user.toReference()).collect(Collectors.toSet()));
		});
		TaskDto taskEvent2 = creator.createTask(TaskContext.EVENT, eventDto.toReference(), t -> {
			t.setObserverUsers(Stream.of(noEventNoCaseViewUser.toReference()).collect(Collectors.toSet()));
		});

		TaskCriteria taskCriteria = new TaskCriteria();
		taskCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);

		List<TaskIndexDto> taskIndexDtos = getTaskFacade().getIndexList(taskCriteria, 0, 100, null);
		assertEquals(2, taskIndexDtos.size());
		List<String> tasksIds = taskIndexDtos.stream().map(t -> t.getUuid()).collect(Collectors.toList());
		assertTrue(tasksIds.contains(taskEvent1.getUuid()));
		assertTrue(tasksIds.contains(taskEvent2.getUuid()));

		loginWith(noEventNoCaseViewUser);
		assertFalse(getUserService().hasRight(UserRight.EVENT_VIEW));
		assertFalse(getUserService().hasRight(UserRight.CONTACT_VIEW));
		assertTrue(getUserService().hasRight(UserRight.CASE_VIEW));
		assertTrue(getUserService().hasRight(UserRight.TRAVEL_ENTRY_VIEW));

		taskIndexDtos = getTaskFacade().getIndexList(taskCriteria, 0, 100, null);
		assertNotNull(taskIndexDtos);
		tasksIds = taskIndexDtos.stream().map(t -> t.getUuid()).collect(Collectors.toList());
		assertFalse(tasksIds.contains(taskEvent1.getUuid()));
		assertTrue(tasksIds.contains(taskEvent2.getUuid()));
	}

	@Test
	public void testGetIndexListArchived() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceOfficer(rdcf);

		TaskDto task = creator
			.createTask(TaskContext.GENERAL, TaskType.ANIMAL_TESTING, TaskStatus.PENDING, null, null, null, null, new Date(), user.toReference());
		getTaskFacade().archive(Collections.singletonList(task.getUuid()));

		List<TaskIndexDto> archivedTasks =
			getTaskFacade().getIndexList(new TaskCriteria().relevanceStatus(EntityRelevanceStatus.ARCHIVED), null, null, null);

		assertThat(archivedTasks, hasSize(1));
		assertThat(archivedTasks.stream().filter(t -> t.getUuid().equals(task.getUuid())).count(), is(1L));

		CaseDataDto caze = creator.createCase(user.toReference(), rdcf, null);
		TaskDto caseTask = creator.createTask(TaskContext.CASE, caze.toReference(), null);
		getCaseFacade().archive(caze.getUuid(), new Date());

		archivedTasks = getTaskFacade().getIndexList(new TaskCriteria().relevanceStatus(EntityRelevanceStatus.ARCHIVED), null, null, null);
		assertThat(archivedTasks.stream().filter(t -> t.getUuid().equals(caseTask.getUuid())).count(), is(1L));

		ContactDto contact = creator.createContact(rdcf, user.toReference(), creator.createPerson().toReference());
		TaskDto contactTask = creator.createTask(TaskContext.CONTACT, contact.toReference(), null);
		getContactFacade().archive(contact.getUuid(), new Date());

		archivedTasks = getTaskFacade().getIndexList(new TaskCriteria().relevanceStatus(EntityRelevanceStatus.ARCHIVED), null, null, null);
		assertThat(archivedTasks.stream().filter(t -> t.getUuid().equals(contactTask.getUuid())).count(), is(1L));

		EventDto event = creator.createEvent(user.toReference());
		TaskDto eventTask = creator.createTask(TaskContext.EVENT, event.toReference(), null);
		getEventFacade().archive(event.getUuid(), new Date());

		archivedTasks = getTaskFacade().getIndexList(new TaskCriteria().relevanceStatus(EntityRelevanceStatus.ARCHIVED), null, null, null);
		assertThat(archivedTasks.stream().filter(t -> t.getUuid().equals(eventTask.getUuid())).count(), is(1L));

		TravelEntryDto travelEntry = creator.createTravelEntry(creator.createPerson().toReference(), user.toReference(), rdcf, null);
		TaskDto travelEntryTask = creator.createTask(TaskContext.TRAVEL_ENTRY, travelEntry.toReference(), null);
		getTravelEntryFacade().archive(travelEntry.getUuid(), new Date());

		archivedTasks = getTaskFacade().getIndexList(new TaskCriteria().relevanceStatus(EntityRelevanceStatus.ARCHIVED), null, null, null);
		assertThat(archivedTasks.stream().filter(t -> t.getUuid().equals(travelEntryTask.getUuid())).count(), is(1L));
	}

	@Test
	public void testGetEditPermissionTypeOnGenericTask() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceOfficer(rdcf);

		TaskDto task = creator
			.createTask(TaskContext.GENERAL, TaskType.ANIMAL_TESTING, TaskStatus.PENDING, null, null, null, null, new Date(), user.toReference());
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		getTaskFacade().archive(Collections.singletonList(task.getUuid()));
		setEditArchiveFeature(true);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));
		setEditArchiveFeature(false);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ARCHIVING_STATUS_ONLY));
	}

	@Test
	public void testGetEditPermissionTypeOnCaseTask() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceOfficer(rdcf);

		CaseDataDto caseToArchive = creator.createCase(user.toReference(), rdcf, null);
		TaskDto task = creator.createTask(TaskContext.CASE, caseToArchive.toReference(), null);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		getCaseFacade().archive(caseToArchive.getUuid(), new Date());

		setEditArchiveFeature(true);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		setEditArchiveFeature(false);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ARCHIVING_STATUS_ONLY));

		// shared case
		CaseDataDto caseToShare = creator.createCase(user.toReference(), rdcf, null);
		task = creator.createTask(TaskContext.CASE, caseToShare.toReference(), null);

		creator.createShareRequestInfo(
			ShareRequestDataType.CASE,
			getUserService().getByUuid(user.getUuid()),
			"test-server",
			true,
			ShareRequestStatus.PENDING,
			(s) -> s.setCaze(getCaseService().getByUuid(caseToShare.getUuid())));

		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		creator.createShareRequestInfo(
			ShareRequestDataType.CASE,
			getUserService().getByUuid(user.getUuid()),
			"test-server",
			true,
			ShareRequestStatus.ACCEPTED,
			(s) -> s.setCaze(getCaseService().getByUuid(caseToShare.getUuid())));

		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));
	}

	@Test
	public void testGetEditPermissionTypeOnContactTask() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceOfficer(rdcf);

		ContactDto contactToArchive = creator.createContact(rdcf, user.toReference(), creator.createPerson().toReference());
		TaskDto task = creator.createTask(TaskContext.CONTACT, contactToArchive.toReference(), null);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		getContactFacade().archive(contactToArchive.getUuid(), new Date());

		setEditArchiveFeature(true);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		setEditArchiveFeature(false);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ARCHIVING_STATUS_ONLY));

		// shared contact
		ContactDto contactToShare = creator.createContact(rdcf, user.toReference(), creator.createPerson().toReference());
		task = creator.createTask(TaskContext.CONTACT, contactToShare.toReference(), null);

		creator.createShareRequestInfo(
			ShareRequestDataType.CONTACT,
			getUserService().getByUuid(user.getUuid()),
			"test-server",
			true,
			ShareRequestStatus.PENDING,
			(s) -> s.setContact(getContactService().getByUuid(contactToShare.getUuid())));

		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		creator.createShareRequestInfo(
			ShareRequestDataType.CONTACT,
			getUserService().getByUuid(user.getUuid()),
			"test-server",
			true,
			ShareRequestStatus.ACCEPTED,
			(s) -> s.setContact(getContactService().getByUuid(contactToShare.getUuid())));

		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));
	}

	@Test
	public void testGetEditPermissionTypeOnEventTask() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceOfficer(rdcf);

		EventDto eventToArchive = creator.createEvent(user.toReference());
		TaskDto task = creator.createTask(TaskContext.EVENT, eventToArchive.toReference(), null);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		getEventFacade().archive(eventToArchive.getUuid(), new Date());

		setEditArchiveFeature(true);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		setEditArchiveFeature(false);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ARCHIVING_STATUS_ONLY));

		// shared event
		EventDto eventToShare = creator.createEvent(user.toReference());
		task = creator.createTask(TaskContext.EVENT, eventToShare.toReference(), null);

		creator.createShareRequestInfo(
			ShareRequestDataType.EVENT,
			getUserService().getByUuid(user.getUuid()),
			"test-server",
			true,
			ShareRequestStatus.PENDING,
			(s) -> s.setEvent(getEventService().getByUuid(eventToShare.getUuid())));

		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		creator.createShareRequestInfo(
			ShareRequestDataType.EVENT,
			getUserService().getByUuid(user.getUuid()),
			"test-server",
			true,
			ShareRequestStatus.ACCEPTED,
			(s) -> s.setEvent(getEventService().getByUuid(eventToShare.getUuid())));

		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));
	}

	@Test
	public void testGetEditPermissionTypeOnTravelEntryTask() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceOfficer(rdcf);

		TravelEntryDto travelEntryToArchive = creator.createTravelEntry(creator.createPerson().toReference(), user.toReference(), rdcf, null);
		TaskDto task = creator.createTask(TaskContext.TRAVEL_ENTRY, travelEntryToArchive.toReference(), null);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		getTravelEntryFacade().archive(travelEntryToArchive.getUuid(), new Date());

		setEditArchiveFeature(true);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ALLOWED));

		setEditArchiveFeature(false);
		assertThat(getTaskFacade().getEditPermissionType(task.getUuid()), is(EditPermissionType.ARCHIVING_STATUS_ONLY));
	}

	private void setEditArchiveFeature(boolean enabled) {
		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, enabled, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.EDIT_ARCHIVED_ENTITIES);

	}

}
