package de.symeda.sormas.backend.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.task.DashboardTask;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sample.SampleTestFacadeEjb.SampleTestFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.visit.VisitFacadeEjb;
import info.novatec.beantest.api.BaseBeanTest;

public class TaskFacadeEjbTest extends BaseBeanTest {
	
	@Before
	public void resetMocks() {
		MockProducer.resetMocks();
	}
	
	@Test
	public void testDashboardTaskListCreation() {
		TaskFacade taskFacade = getBean(TaskFacadeEjb.class);
		
		TestDataCreator creator = createTestDataCreator();
		
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		creator.createTask(TaskContext.GENERAL, TaskType.OTHER, TaskStatus.PENDING, null, null, DateHelper.addDays(new Date(), 1), user);
		
		List<DashboardTask> dashboardTasks = taskFacade.getAllByUserForDashboard(TaskStatus.PENDING, user.getUuid());
		
		// List should have one entry
		assertEquals(1, dashboardTasks.size());
	}
	
	@Test
	public void testSampleDeletion() {
		TaskFacade taskFacade = getBean(TaskFacadeEjb.class);

		TestDataCreator creator = createTestDataCreator();

		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		UserDto admin = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Ad", "Min", UserRole.ADMIN);
		String adminUuid = admin.getUuid();
		TaskDto task = creator.createTask(TaskContext.GENERAL, TaskType.OTHER, TaskStatus.PENDING, null, null, DateHelper.addDays(new Date(), 1), user);
		
		// Database should contain the created task
		assertNotNull(taskFacade.getByUuid(task.getUuid()));
		
		taskFacade.deleteTask(task, adminUuid);
		
		// Database should not contain the created task
		assertNull(taskFacade.getByUuid(task.getUuid()));
	}
	
	private TestDataCreator createTestDataCreator() {
		return new TestDataCreator(getBean(UserFacadeEjbLocal.class), getBean(PersonFacadeEjbLocal.class),
				getBean(CaseFacadeEjbLocal.class), getBean(ContactFacadeEjbLocal.class), getBean(TaskFacadeEjb.class),
				getBean(VisitFacadeEjb.class), getBean(WeeklyReportFacadeEjbLocal.class), getBean(EventFacadeEjb.class), getBean(EventParticipantFacadeEjb.class),
				getBean(SampleFacadeEjb.class), getBean(SampleTestFacadeEjbLocal.class), getBean(RegionFacadeEjbLocal.class), 
				getBean(DistrictFacadeEjbLocal.class), getBean(CommunityFacadeEjb.class), getBean(FacilityFacadeEjbLocal.class), 
				getBean(RegionService.class), getBean(DistrictService.class), getBean(CommunityService.class), getBean(FacilityService.class));
	}

}
