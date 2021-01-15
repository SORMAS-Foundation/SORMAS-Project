package de.symeda.sormas.backend.systemevent;

import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventStatus;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

import static org.hamcrest.Matchers.hasSize;
import org.junit.Test;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SystemEventFacadeEjbTest extends AbstractBeanTest {

	protected final TestDataCreator creator = new TestDataCreator(this);

	@Test
	public void testGetLatestSuccessByType() {

		Date earliestDate = new Date(100000L);
		Date intermediateDate = new Date(200000L);
		Date latestDate = new Date(300000L);

		SystemEventDto earlierSuccess = creator.createSystemEvent(SystemEventType.FETCH_LAB_MESSAGES, earliestDate, SystemEventStatus.SUCCESS);
		SystemEventDto latestSuccess = creator.createSystemEvent(SystemEventType.FETCH_LAB_MESSAGES, intermediateDate, SystemEventStatus.SUCCESS);
		SystemEventDto error = creator.createSystemEvent(SystemEventType.FETCH_LAB_MESSAGES, latestDate, SystemEventStatus.ERROR);

		getSystemEventFacade().saveSystemEvent(earlierSuccess);
		getSystemEventFacade().saveSystemEvent(latestSuccess);
		getSystemEventFacade().saveSystemEvent(error);
		assertEquals(intermediateDate, getSystemEventFacade().getLatestSuccessByType(SystemEventType.FETCH_LAB_MESSAGES));
	}

	@Test
	public void testDeleteAllDeletableSystemEvents() throws InterruptedException {

		LocalDateTime start = LocalDateTime.now();
		// Small delay for the timesteps to have different milliseconds
		Thread.sleep(5L);
		SystemEventDto systemEvent1 = creator.createSystemEvent(SystemEventType.FETCH_LAB_MESSAGES, new Date(), SystemEventStatus.ERROR);
		getSystemEventFacade().saveSystemEvent(systemEvent1);
		Thread.sleep(5L);

		LocalDateTime inBetween = LocalDateTime.now();
		Thread.sleep(5L);

		SystemEventDto systemEvent2 = creator.createSystemEvent(SystemEventType.FETCH_LAB_MESSAGES, new Date(), SystemEventStatus.SUCCESS);
		getSystemEventFacade().saveSystemEvent(systemEvent2);

		SystemEventFacadeEjb systemEventFacadeEjb = (SystemEventFacadeEjb) getSystemEventFacade();

		systemEventFacadeEjb.deleteAllDeletableSystemEvents(start);
		assertThat(getAllSystemEvents(), hasSize(2));

		getSystemEventFacade().deleteAllDeletableSystemEvents(1);
		assertThat(getAllSystemEvents(), hasSize(2));

		systemEventFacadeEjb.deleteAllDeletableSystemEvents(inBetween);
		assertEquals(systemEventFacadeEjb.fromDto(systemEvent2, null), getAllSystemEvents().get(0));

		getSystemEventFacade().deleteAllDeletableSystemEvents(-1);
		assertTrue(getAllSystemEvents().isEmpty());

	}

	private List<SystemEvent> getAllSystemEvents() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<SystemEvent> cq = cb.createQuery(SystemEvent.class);
		cq.from(SystemEvent.class);
		return getEntityManager().createQuery(cq).getResultList();
	}

}
