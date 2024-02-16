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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskExportDto;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class TaskFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

	@Override
	public void init() {
		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));

		loginWith(user2);
	}

	@Test
	public void testGetTaskWithCaseInJurisdiction() {

		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		TaskDto task = createCaseTask(caze);

		assertNotPseudonymized(getTaskFacade().getByUuid(task.getUuid()));
	}

	@Test
	public void testGetTaskWithCaseOutsideJurisdiction() {

		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		TaskDto task = createCaseTask(caze);

		assertPseudonymized(getTaskFacade().getByUuid(task.getUuid()));
	}

	@Test
	public void testGetTaskWithContactInJurisdiction() {

		CaseDataDto caze = creator.createCase(user2.toReference(), creator.createPerson("John", "Doe").toReference(), rdcf2);
		ContactDto contact = creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("James", "Smith").toReference(),
			caze,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		TaskDto task = createContactTask(contact);

		TaskDto savedTask = getTaskFacade().getByUuid(task.getUuid());
		assertThat(savedTask.getContact().getCaption(), containsString("James SMITH to case John Doe"));
	}

	@Test
	public void testGetTaskWithContactOutsideJurisdiction() {

		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Doe").toReference(), rdcf1);
		ContactDto contact = creator.createContact(
			user1.toReference(),
			null,
			creator.createPerson("James", "Smith").toReference(),
			caze,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);
		TaskDto task = createContactTask(contact);

		TaskDto savedTask = getTaskFacade().getByUuid(task.getUuid());
		assertThat(savedTask.getContact().getCaption(), is(DataHelper.getShortUuid(savedTask.getContact().getUuid())));
	}

	@Test
	public void testPseudonymizeIndexList() {

		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		TaskDto task1 = createCaseTask(caze1);

		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		ContactDto contact1 = creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		TaskDto task2 = createCaseTask(caze2);
		TaskDto task3 = createContactTask(contact1);

		ContactDto contact2 = creator.createContact(
			user1.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);
		TaskDto task4 = createContactTask(contact2);

		List<TaskIndexDto> indexList = getTaskFacade().getIndexList(new TaskCriteria(), null, null, Collections.emptyList());
		TaskIndexDto index1 = indexList.stream().filter(t -> t.getUuid().equals(task1.getUuid())).findFirst().get();
		assertThat(index1.getCaze().getFirstName(), is("John"));
		assertThat(index1.getCaze().getLastName(), is("Smith"));

		TaskIndexDto index2 = indexList.stream().filter(t -> t.getUuid().equals(task2.getUuid())).findFirst().get();
		assertThat(index2.getCaze().getFirstName(), isEmptyString());
		assertThat(index2.getCaze().getLastName(), isEmptyString());

		TaskIndexDto index3 = indexList.stream().filter(t -> t.getUuid().equals(task3.getUuid())).findFirst().get();
		assertThat(index3.getContact().getCaption(), containsString("John SMITH"));

		TaskIndexDto index4 = indexList.stream().filter(t -> t.getUuid().equals(task4.getUuid())).findFirst().get();
		assertThat(index4.getContact().getCaption(), is(DataHelper.getShortUuid(index4.getContact().getUuid())));
	}

	@Test
	public void testPseudonymizeExportList() {

		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		TaskDto task1 = createCaseTask(caze1);

		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		ContactDto contact1 = creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		TaskDto task2 = createCaseTask(caze2);
		TaskDto task3 = createContactTask(contact1);

		ContactDto contact2 = creator.createContact(
			user1.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);
		TaskDto task4 = createContactTask(contact2);

		List<TaskExportDto> exportList = getTaskFacade().getExportList(new TaskCriteria(), null, 1, 100);
		TaskExportDto export1 = exportList.stream().filter(t -> t.getUuid().equals(task1.getUuid())).findFirst().get();
		assertThat(export1.getPersonFirstName(), is("John"));
		assertThat(export1.getPersonLastName(), is("Smith"));

		TaskExportDto export2 = exportList.stream().filter(t -> t.getUuid().equals(task2.getUuid())).findFirst().get();
		assertThat(export2.getPersonFirstName(), is("Confidential"));
		assertThat(export2.getPersonLastName(), is("Confidential"));

		TaskExportDto export3 = exportList.stream().filter(t -> t.getUuid().equals(task3.getUuid())).findFirst().get();
		assertThat(export3.getPersonFirstName(), is("John"));
		assertThat(export3.getPersonLastName(), is("Smith"));

		TaskExportDto export4 = exportList.stream().filter(t -> t.getUuid().equals(task4.getUuid())).findFirst().get();
		assertThat(export4.getPersonFirstName(), is("Confidential"));
		assertThat(export4.getPersonLastName(), is("Confidential"));

	}

	@Test
	public void testPseudonymizeGetAllAfter() {

		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		TaskDto task1 = createCaseTask(caze1);

		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		ContactDto contact1 = creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		TaskDto task2 = createCaseTask(caze2);
		TaskDto task3 = createContactTask(contact1);

		ContactDto contact2 = creator.createContact(
			user1.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);
		TaskDto task4 = createContactTask(contact2);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2019);
		List<TaskDto> activeTasks = getTaskFacade().getAllActiveTasksAfter(calendar.getTime());
		TaskDto active1 = activeTasks.stream().filter(t -> t.getUuid().equals(task1.getUuid())).findFirst().get();
		assertThat(active1.getCaze().getFirstName(), is("John"));
		assertThat(active1.getCaze().getLastName(), is("Smith"));

		TaskDto active2 = activeTasks.stream().filter(t -> t.getUuid().equals(task2.getUuid())).findFirst().get();
		assertThat(active2.getCaze().getFirstName(), isEmptyString());
		assertThat(active2.getCaze().getLastName(), isEmptyString());

		TaskDto active3 = activeTasks.stream().filter(t -> t.getUuid().equals(task3.getUuid())).findFirst().get();
		assertThat(active3.getContact().getCaption(), containsString("John SMITH"));

		TaskDto active4 = activeTasks.stream().filter(t -> t.getUuid().equals(task4.getUuid())).findFirst().get();
		assertThat(active4.getContact().getCaption(), is(DataHelper.getShortUuid(task4.getContact().getUuid())));
	}

	@Test
	public void testPseudonymizeGetAllByUuid() {

		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		TaskDto task1 = createCaseTask(caze1);

		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		ContactDto contact1 = creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		TaskDto task2 = createCaseTask(caze2);
		TaskDto task3 = createContactTask(contact1);

		ContactDto contact2 = creator.createContact(
			user1.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);
		TaskDto task4 = createContactTask(contact2);

		List<TaskDto> activeTasks = getTaskFacade().getByUuids(Arrays.asList(task1.getUuid(), task2.getUuid(), task3.getUuid(), task4.getUuid()));

		TaskDto active1 = activeTasks.stream().filter(t -> t.getUuid().equals(task1.getUuid())).findFirst().get();
		assertThat(active1.getCaze().getFirstName(), is("John"));
		assertThat(active1.getCaze().getLastName(), is("Smith"));

		TaskDto active2 = activeTasks.stream().filter(t -> t.getUuid().equals(task2.getUuid())).findFirst().get();
		assertThat(active2.getCaze().getFirstName(), isEmptyString());
		assertThat(active2.getCaze().getLastName(), isEmptyString());

		TaskDto active3 = activeTasks.stream().filter(t -> t.getUuid().equals(task3.getUuid())).findFirst().get();
		assertThat(active3.getContact().getCaption(), containsString("John SMITH"));

		TaskDto active4 = activeTasks.stream().filter(t -> t.getUuid().equals(task4.getUuid())).findFirst().get();
		assertThat(active4.getContact().getCaption(), is(DataHelper.getShortUuid(task4.getContact().getUuid())));
	}

	@Test
	public void testPseudonymizeGetByCase() {

		CaseDataDto caze1 = creator.createCase(user2.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf2);
		TaskDto task1 = createCaseTask(caze1);

		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		TaskDto task2 = createCaseTask(caze2);

		List<TaskDto> case1Tasks = getTaskFacade().getAllByCase(caze1.toReference());
		TaskDto active1 = case1Tasks.stream().filter(t -> t.getUuid().equals(task1.getUuid())).findFirst().get();
		assertThat(active1.getCaze().getFirstName(), is("John"));
		assertThat(active1.getCaze().getLastName(), is("Smith"));

		List<TaskDto> case2Task = getTaskFacade().getAllByCase(caze2.toReference());
		TaskDto active2 = case2Task.stream().filter(t -> t.getUuid().equals(task2.getUuid())).findFirst().get();
		assertThat(active2.getCaze().getFirstName(), isEmptyString());
		assertThat(active2.getCaze().getLastName(), isEmptyString());
	}

	@Test
	public void testPseudonymizeGetByContact() {
		CaseDataDto caze2 = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		ContactDto contact1 = creator.createContact(
			user2.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf2);
		TaskDto task3 = createContactTask(contact1);

		ContactDto contact2 = creator.createContact(
			user1.toReference(),
			null,
			creator.createPerson("John", "Smith").toReference(),
			caze2,
			new Date(),
			new Date(),
			Disease.CORONAVIRUS,
			rdcf1);
		TaskDto task4 = createContactTask(contact2);

		List<TaskDto> contact1Tasks = getTaskFacade().getAllByContact(contact1.toReference());
		TaskDto active1 = contact1Tasks.stream().filter(t -> t.getUuid().equals(task3.getUuid())).findFirst().get();
		assertThat(active1.getContact().getCaption(), containsString("John SMITH"));

		List<TaskDto> contact2Tasks = getTaskFacade().getAllByContact(contact2.toReference());
		TaskDto active2 = contact2Tasks.stream().filter(t -> t.getUuid().equals(task4.getUuid())).findFirst().get();
		assertThat(active2.getContact().getCaption(), is(DataHelper.getShortUuid(task4.getContact().getUuid())));
	}

	@Test
	public void testGetTaskOfCaseWithSpecialAccess() {

		CaseDataDto caze = creator.createCase(user1.toReference(), creator.createPerson("John", "Smith").toReference(), rdcf1);
		creator.createSpecialCaseAccess(caze.toReference(), user1.toReference(), user2.toReference(), DateHelper.addDays(new Date(), 1));
		TaskDto task = createCaseTask(caze);

		assertNotPseudonymized(getTaskFacade().getByUuid(task.getUuid()));
		assertNotPseudonymized(getTaskFacade().getByUuids(Collections.singletonList(task.getUuid())).get(0));
		assertNotPseudonymized(getTaskFacade().getAllActiveTasksAfter(new Date(0)).get(0));
		assertThat(getTaskFacade().getIndexList(new TaskCriteria(), null, null, null).get(0).isPseudonymized(), is(false));
		assertThat(getTaskFacade().getExportList(new TaskCriteria(), null, 0, Integer.MAX_VALUE).get(0).isPseudonymized(), is(false));
	}

	private TaskDto createCaseTask(CaseDataDto caze) {
		return creator
			.createTask(TaskContext.CASE, TaskType.CASE_INVESTIGATION, TaskStatus.PENDING, caze.toReference(), null, null, null, new Date(), null);
	}

	private TaskDto createContactTask(ContactDto contact) {
		return creator.createTask(
			TaskContext.CONTACT,
			TaskType.CONTACT_FOLLOW_UP,
			TaskStatus.PENDING,
			null,
			contact.toReference(),
			null,
			null,
			new Date(),
			null);
	}

	private void assertNotPseudonymized(TaskDto task) {
		assertThat(task.getCaze().getFirstName(), is("John"));
		assertThat(task.getCaze().getLastName(), is("Smith"));
	}

	private void assertPseudonymized(TaskDto task) {
		assertThat(task.getCaze().getFirstName(), isEmptyString());
		assertThat(task.getCaze().getLastName(), isEmptyString());
	}
}
