package de.symeda.sormas.ui.events;

import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.login.LoginHelper;

public class EventController {
	
	private EventFacade cof = FacadeProvider.getEventFacade();

	public void create() {
		
	}
	
	public void navigateToData(String data) {
		
	}
	
	public List<EventDto> getEventIndexList() {
    	UserDto user = LoginHelper.getCurrentUser();
    	return FacadeProvider.getEventFacade().getAllEventsAfter(null, user.getUuid());
	}
	
}
