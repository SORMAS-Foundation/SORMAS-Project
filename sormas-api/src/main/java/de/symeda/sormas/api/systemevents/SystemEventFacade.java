package de.symeda.sormas.api.systemevents;

import javax.ejb.Remote;
import java.util.Date;

@Remote
public interface SystemEventFacade {

	Date getLatestSuccessByType(SystemEventType type);

	void saveSystemEvent(SystemEventDto dto);
}
