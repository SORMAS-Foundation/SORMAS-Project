package de.symeda.sormas.backend.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.Test;

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
		creator.createTask(TaskContext.GENERAL, TaskType.OTHER, TaskStatus.PENDING, null, null, DateHelper.addDays(new Date(), 1), user.toReference());
		
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
		TaskDto task = creator.createTask(TaskContext.GENERAL, TaskType.OTHER, TaskStatus.PENDING, null, null, DateHelper.addDays(new Date(), 1), user.toReference());
		
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
		assertNotNull(getTaskFacade().getIndexList(user.getUuid(), null));
	}
}
