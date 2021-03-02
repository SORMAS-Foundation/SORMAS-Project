package de.symeda.sormas.api.systemevents;

import javax.ejb.Remote;

/**
 * As subsequent manipulation of SystemEvents is undesired, this Facade shall not provide methods to fetch specific SystemEvents.
 */
@Remote
public interface SystemEventFacade {

	SystemEventDto getLatestSuccessByType(SystemEventType type);

	void saveSystemEvent(SystemEventDto dto);

	void deleteAllDeletableSystemEvents(int daysAfterSystemEventGetsDeleted);

}
