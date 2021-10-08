package de.symeda.sormas.api.systemevents;

import java.util.Date;

import javax.ejb.Remote;
import javax.validation.Valid;

/**
 * As subsequent manipulation of SystemEvents is undesired, this Facade shall not provide methods to fetch specific SystemEvents.
 */
@Remote
public interface SystemEventFacade {

	boolean existsStartedEvent(SystemEventType type);

	SystemEventDto getLatestSuccessByType(SystemEventType type);

	void saveSystemEvent(@Valid SystemEventDto dto);

	void deleteAllDeletableSystemEvents(int daysAfterSystemEventGetsDeleted);

	void reportSuccess(SystemEventDto systemEvent, String message, Date end);

	void reportSuccess(SystemEventDto systemEvent, Date end);

	void reportError(SystemEventDto systemEvent, String errorMessage, Date end);

	void markPreviouslyStartedAsUnclear(SystemEventType type);
}
