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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskIndexDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaskFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testSampleDeletion() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		TaskDto task = creator.createTask(
			TaskContext.GENERAL,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			null,
			null,
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		// Database should contain the created task
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));

		getTaskFacade().deleteTask(task);

		// Database should not contain the created task
		assertNull(getTaskFacade().getByUuid(task.getUuid()));
	}

	@Test
	public void testGetIndexList() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		// Database should contain the created task
		assertNotNull(getTaskFacade().getIndexList(null, 0, 100, null));
	}

	@Test
	public void testArchivedTaskNotGettingTransfered() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
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
			rdcf.district);

		creator.createTask(
			TaskContext.GENERAL,
			TaskType.OTHER,
			TaskStatus.PENDING,
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
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		creator.createTask(
			TaskContext.CONTACT,
			TaskType.OTHER,
			TaskStatus.PENDING,
			null,
			contact.toReference(),
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
			DateHelper.addDays(new Date(), 1),
			user.toReference());
		// getAllActiveTasks and getAllUuids should return length 4+1+1 (case investigation & contact investigation)
		assertEquals(6, getTaskFacade().getAllActiveTasksAfter(null).size());
		assertEquals(6, getTaskFacade().getAllActiveUuids().size());

		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), true);
		getEventFacade().archiveOrDearchiveEvent(event.getUuid(), true);

		// getAllActiveTasks and getAllUuids should return length 1
		assertEquals(1, getTaskFacade().getAllActiveTasksAfter(null).size());
		assertEquals(1, getTaskFacade().getAllActiveUuids().size());

		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), false);
		getEventFacade().archiveOrDearchiveEvent(event.getUuid(), false);

		// getAllActiveTasks and getAllUuids should return length 5 + 1 (contact investigation)
		assertEquals(6, getTaskFacade().getAllActiveTasksAfter(null).size());
		assertEquals(6, getTaskFacade().getAllActiveUuids().size());
	}

	@Test
	public void testFilterTasksByUserJurisdiction() {
		RDCF rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		UserDto survOff = creator.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), null,
				"Surv", "Off", UserRole.SURVEILLANCE_OFFICER);


		creator.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.community.getUuid(), null,
				"Comm", "Inf", UserRole.COMMUNITY_INFORMANT);

		when(MockProducer.getPrincipal().getName()).thenReturn("CommInf");

		CaseDataDto caze = creator.createCase(survOff.toReference(), creator.createPerson("First", "Last").toReference(), rdcf1);
		creator.createTask(TaskContext.CASE, TaskType.CASE_INVESTIGATION, TaskStatus.PENDING,
				caze.toReference(),
				null, null, new Date(), survOff.toReference());

		List<TaskIndexDto> indexTasks = getTaskFacade().getIndexList(null, 0, 100, null);
		assertThat(indexTasks.size(), is(0));

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2019);
		List<TaskDto> activeTasks = getTaskFacade().getAllActiveTasksAfter(calendar.getTime());
		assertThat(activeTasks.size(), is(0));

		List<TaskDto> tasksByCase = getTaskFacade().getAllByCase(caze.toReference());
		assertThat(tasksByCase.size(), is(0));
	}
}
