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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.task.DashboardTaskDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class TaskFacadeEjbTest extends AbstractBeanTest {
	
	@Test
	public void testDashboardTaskListCreation() {
		
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		creator.createTask(TaskContext.GENERAL, TaskType.OTHER, TaskStatus.PENDING, null, null, null, DateHelper.addDays(new Date(), 1), user.toReference());
		
		List<DashboardTaskDto> dashboardTaskDtos = getTaskFacade().getAllByUserForDashboard(TaskStatus.PENDING, null, null, user.getUuid());
		
		// List should have one entry
		assertEquals(1, dashboardTaskDtos.size());
	}
	
	@Test
	public void testSampleDeletion() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		TaskDto task = creator.createTask(TaskContext.GENERAL, TaskType.OTHER, TaskStatus.PENDING, null, null, null, DateHelper.addDays(new Date(), 1), user.toReference());
		
		// Database should contain the created task
		assertNotNull(getTaskFacade().getByUuid(task.getUuid()));
		
		getTaskFacade().deleteTask(task, adminUuid);
		
		// Database should not contain the created task
		assertNull(getTaskFacade().getByUuid(task.getUuid()));
	}
	
	@Test
	public void testGetIndexList() {

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		
		// Database should contain the created task
		assertNotNull(getTaskFacade().getIndexList(user.getUuid(), null, 0, 100, null));
	}
	
	@Test
	public void testArchivedTaskNotGettingTransfered() {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		PersonDto contactPerson = creator.createPerson("Contact", "Person");
		ContactDto contact = creator.createContact(user.toReference(), user.toReference(), contactPerson.toReference(), caze.toReference(), new Date(), new Date());
		LocationDto eventLocation = new LocationDto();
		eventLocation.setDistrict(getDistrictFacade().getDistrictReferenceByUuid(rdcf.district.getUuid()));
		EventDto event = creator.createEvent(EventType.OUTBREAK, EventStatus.POSSIBLE, "Description", "First", "Name", "12345", TypeOfPlace.PUBLIC_PLACE, DateHelper.subtractDays(new Date(), 1), new Date(), user.toReference(), user.toReference(), Disease.EVD, eventLocation);
		
		creator.createTask(TaskContext.GENERAL, TaskType.OTHER, TaskStatus.PENDING, null, null, null, DateHelper.addDays(new Date(), 1), user.toReference());
		creator.createTask(TaskContext.CASE, TaskType.OTHER, TaskStatus.PENDING, caze.toReference(), null, null, DateHelper.addDays(new Date(), 1), user.toReference());
		creator.createTask(TaskContext.CONTACT, TaskType.OTHER, TaskStatus.PENDING, null, contact.toReference(), null, DateHelper.addDays(new Date(), 1), user.toReference());
		creator.createTask(TaskContext.EVENT, TaskType.OTHER, TaskStatus.PENDING, null, null, event.toReference(), DateHelper.addDays(new Date(), 1), user.toReference());
		
		// getAllActiveTasks and getAllUuids should return length 4+1 (case investigation)
		assertEquals(5, getTaskFacade().getAllActiveTasksAfter(null, user.getUuid()).size());
		assertEquals(5, getTaskFacade().getAllActiveUuids(user.getUuid()).size());
		
		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), true);
		getEventFacade().archiveOrDearchiveEvent(event.getUuid(), true);
		
		// getAllActiveTasks and getAllUuids should return length 1
		assertEquals(1, getTaskFacade().getAllActiveTasksAfter(null, user.getUuid()).size());
		assertEquals(1, getTaskFacade().getAllActiveUuids(user.getUuid()).size());

		getCaseFacade().archiveOrDearchiveCase(caze.getUuid(), false);
		getEventFacade().archiveOrDearchiveEvent(event.getUuid(), false);

		// getAllActiveTasks and getAllUuids should return length 4
		assertEquals(5, getTaskFacade().getAllActiveTasksAfter(null, user.getUuid()).size());
		assertEquals(5, getTaskFacade().getAllActiveUuids(user.getUuid()).size());
	}
}
