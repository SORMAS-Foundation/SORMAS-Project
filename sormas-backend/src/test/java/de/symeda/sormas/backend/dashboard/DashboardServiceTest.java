package de.symeda.sormas.backend.dashboard;

import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DashboardServiceTest extends AbstractBeanTest {

	@Test
	public void testGetEventCountByStatusWithArchivingAndDeletion() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.NATIONAL_USER);
		EventDto signal = creator.createEvent(user.toReference(), EventStatus.SIGNAL);
		EventDto event1 = creator.createEvent(user.toReference(), EventStatus.EVENT);
		creator.createEvent(user.toReference(), EventStatus.EVENT);
		EventDto screening = creator.createEvent(user.toReference(), EventStatus.SCREENING);
		EventDto cluster = creator.createEvent(user.toReference(), EventStatus.CLUSTER);

		DashboardService sut = getDashboardService();
		EventFacadeEjb.EventFacadeEjbLocal eventFacade = getEventFacade();

		Map<EventStatus, Long> result = sut.getEventCountByStatus(new DashboardCriteria());
		assertEquals(4, result.size());
		assertEquals(Long.valueOf(1), result.get(EventStatus.SIGNAL));
		assertEquals(Long.valueOf(2), result.get(EventStatus.EVENT));
		assertEquals(Long.valueOf(1), result.get(EventStatus.SCREENING));
		assertEquals(Long.valueOf(1), result.get(EventStatus.CLUSTER));

		// archive events (should not have any effect on result)
		eventFacade.archive(Arrays.asList(signal.getUuid(), event1.getUuid(), screening.getUuid(), cluster.getUuid()));

		result = sut.getEventCountByStatus(new DashboardCriteria());

		assertEquals(4, result.size());
		assertEquals(Long.valueOf(1), result.get(EventStatus.SIGNAL));
		assertEquals(Long.valueOf(2), result.get(EventStatus.EVENT));
		assertEquals(Long.valueOf(1), result.get(EventStatus.SCREENING));
		assertEquals(Long.valueOf(1), result.get(EventStatus.CLUSTER));

		// delete events (should have an effect on result)
		eventFacade.delete(signal.getUuid());
		eventFacade.delete(event1.getUuid());
		eventFacade.delete(screening.getUuid());
		eventFacade.delete(cluster.getUuid());

		result = sut.getEventCountByStatus(new DashboardCriteria());
		assertEquals(1, result.size());
		assertEquals(Long.valueOf(1), result.get(EventStatus.EVENT));

	}
}
