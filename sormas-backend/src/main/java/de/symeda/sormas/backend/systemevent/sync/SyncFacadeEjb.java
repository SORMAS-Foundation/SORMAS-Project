package de.symeda.sormas.backend.systemevent.sync;

import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventStatus;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.systemevents.sync.SyncFacade;
import de.symeda.sormas.backend.systemevent.SystemEventFacadeEjb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.Date;

@Stateless(name = "SyncFacade")
public class SyncFacadeEjb implements SyncFacade {

	@EJB
	private SystemEventFacadeEjb.SystemEventFacadeEjbLocal systemEventFacade;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public SystemEventDto startSyncFor(SystemEventType type) {
		systemEventFacade.markPreviouslyStartedAsUnclear(type);
		Date startDate = new Date();
		SystemEventDto systemEvent = SystemEventDto.build();
		systemEvent.setType(type);
		systemEvent.setStatus(SystemEventStatus.STARTED);
		systemEvent.setStartDate(startDate);
		systemEventFacade.saveSystemEvent(systemEvent);
		return systemEvent;
	}

	@Override
	public Date findLastSyncDateFor(SystemEventType type) {
		SystemEventDto latestSuccess = systemEventFacade.getLatestSuccessByType(type);
		long millis;
		if (latestSuccess != null) {
			millis = determineLatestSuccessMillis(latestSuccess);
		} else {
			logger.info("No previous successful attempt to synchronize could be found. The synchronization date is set to 0 (UNIX milliseconds)");
			millis = 0L;
		}
		return new Date(millis);
	}

	@Override
	public void reportSuccessfulSyncWithTimestamp(SystemEventDto systemEvent, Date syncDate) {
		String message = "Last synchronization date: " + syncDate.getTime();
		systemEventFacade.reportSuccess(systemEvent, message, new Date());
	}

	private long determineLatestSuccessMillis(SystemEventDto latestSuccess) {
		String info = latestSuccess.getAdditionalInfo();
		if (info != null) {
			try {
				//parse last synchronization date
				return Long.parseLong(info.replace("Last synchronization date: ", ""));
			} catch (NumberFormatException e) {
				logger.error("Synchronization date could not be parsed. Falling back to start date.");
				return latestSuccess.getStartDate().getTime();
			}
		} else {
			logger.warn("Synchronization date could not be found for the last successful retrieval. Falling back to start date.");
			return latestSuccess.getStartDate().getTime();
		}
	}

	@LocalBean
	@Stateless
	public static class SyncFacadeEjbLocal extends SyncFacadeEjb {

	}
}
