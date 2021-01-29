package de.symeda.sormas.backend.task;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyObject;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.TaskCreationException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

public class TaskServiceTest extends AbstractBeanTest {

	@InjectMocks
	private TaskService taskService;

	@Mock
	private UserService userService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetTaskAssigneeFromContactWithContactOfficer() throws TaskCreationException {
		User contactOfficer = new User();
		contactOfficer.setId(1L);
		Contact contact = new Contact();
		contact.setContactOfficer(contactOfficer);

		User actualAssignee = taskService.getTaskAssignee(contact);
		assertEquals(actualAssignee.getId(), contactOfficer.getId());
	}

	@Test
	public void testGetTaskAssigneeFromDistrictOfficers() throws TaskCreationException {
		User contactOfficer = new User();
		contactOfficer.setId(1L);
		District district = new District();
		Contact contact = new Contact();
		contact.setDistrict(district);

		Mockito.when(userService.getAllByDistrict(any(District.class), anyBoolean(), anyObject()))
			.thenReturn(Collections.singletonList(contactOfficer));

		User actualAssignee = taskService.getTaskAssignee(contact);
		assertEquals(actualAssignee.getId(), contactOfficer.getId());
	}

	@Test
	public void testGetTaskAssigneeFromRegionSupervisors() throws TaskCreationException {
		User contactOfficer = new User();
		contactOfficer.setId(1L);
		Contact contact = new Contact();
		Location location = new Location();
		Person person = new Person();
		person.setAddress(location);
		contact.setPerson(person);
		Region region = new Region();
		contact.setRegion(region);

		Mockito.when(userService.getAllByRegionAndUserRoles(any(Region.class), anyObject())).thenReturn(Collections.singletonList(contactOfficer));

		User actualAssignee = taskService.getTaskAssignee(contact);
		assertEquals(actualAssignee.getId(), contactOfficer.getId());
	}

	@Test(expected = TaskCreationException.class)
	public void testGetTaskAssigneeException() throws TaskCreationException {
		Contact contact = new Contact();
		Location location = new Location();
		Person person = new Person();
		person.setAddress(location);
		contact.setPerson(person);
		Case caze = new Case();
		contact.setCaze(caze);

		Mockito.when(userService.getAllByRegionAndUserRoles(any(Region.class), anyObject())).thenReturn(Collections.emptyList());

		taskService.getTaskAssignee(contact);
	}
}
