package de.symeda.sormas.backend.systemevent;

import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventStatus;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

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

}
