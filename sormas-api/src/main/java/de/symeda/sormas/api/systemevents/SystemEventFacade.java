package de.symeda.sormas.api.systemevents;

import javax.ejb.Remote;
import java.util.Date;

/**
 * As subsequent manipulation of SystemEvents is undesired, this Facade shall not provide methods to fetch specific SystemEvents.
 */
@Remote
public interface SystemEventFacade {

	Date getLatestSuccessByType(SystemEventType type);

	void saveSystemEvent(SystemEventDto dto);

	void deleteAllDeletableSystemEvents(int daysAfterSystemEventGetsDeleted);

}
