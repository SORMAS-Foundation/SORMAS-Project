package de.symeda.sormas.api.systemevents.sync;

import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventType;

import javax.ejb.Remote;
import java.util.Date;

@Remote
public interface SyncFacade {

	SystemEventDto startSyncFor(SystemEventType type);

	Date findLastSyncDateFor(SystemEventType type);

	void reportSuccessfulSyncWithTimestamp(SystemEventDto systemEvent, Date syncDate);

	void reportSyncErrorWithTimestamp(SystemEventDto sync, String errorMessage);

    boolean hasAtLeastOneSuccessfullSyncOf(SystemEventType type);
}
