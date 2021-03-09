package de.symeda.sormas.api.systemevents;

import javax.ejb.Remote;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * As subsequent manipulation of SystemEvents is undesired, this Facade shall not provide methods to fetch specific SystemEvents.
 */
@Remote
public interface SystemEventFacade {

	SystemEventDto getLatestSuccessByType(SystemEventType type);

	void saveSystemEvent(SystemEventDto dto);

	void deleteAllDeletableSystemEvents(int daysAfterSystemEventGetsDeleted);

	void reportSuccess(SystemEventDto systemEvent, String message, Date end);

	void reportSuccess(SystemEventDto systemEvent, Date end);

	void reportError(SystemEventDto systemEvent, String errorMessage, Date end);

}
