package de.symeda.sormas.ui.events;

import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventController {
	
	private EventFacade ef = FacadeProvider.getEventFacade();

	public void registerViews(Navigator navigator) {
		navigator.addView(EventsView.VIEW_NAME, EventsView.class);
		navigator.addView(EventDataView.VIEW_NAME, EventDataView.class);
		navigator.addView(EventParticipantsView.VIEW_NAME, EventParticipantsView.class);
	}
	
	public void create() {
		CommitDiscardWrapperComponent<EventCreateForm> eventCreateComponent = getEventCreateComponent();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, "Create new event");
	}
	
	public void navigateToData(String eventUuid) {
		String navigationState = EventDataView.VIEW_NAME + "/" + eventUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}
	
	public void setUriFragmentParameter(String eventUuid) {
		String fragmentParameter;
		if(eventUuid == null || eventUuid.isEmpty()) {
			fragmentParameter = "";
		} else {
			fragmentParameter = eventUuid;
		}
		
		Page page = SormasUI.get().getPage();
		page.setUriFragment("!" + EventsView.VIEW_NAME + "/" + fragmentParameter, false);
	}
	
	public List<EventDto> getEventIndexList() {
    	UserDto user = LoginHelper.getCurrentUser();
    	return FacadeProvider.getEventFacade().getAllEventsAfter(null, user.getUuid());
	}
	
	private EventDto findEvent(String uuid) {
		return ef.getEventByUuid(uuid);
	}
	
	public CommitDiscardWrapperComponent<EventCreateForm> getEventCreateComponent() {
		EventCreateForm eventCreateForm = new EventCreateForm();
		eventCreateForm.setValue(createNewEvent());
		final CommitDiscardWrapperComponent<EventCreateForm> editView = new CommitDiscardWrapperComponent<EventCreateForm>(eventCreateForm, eventCreateForm.getFieldGroup());
		editView.setWidth(1040, Unit.PIXELS);
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(eventCreateForm.getFieldGroup().isValid()) {
					EventDto dto = eventCreateForm.getValue();
					ef.saveEvent(dto);
					Notification.show("New event created", Type.TRAY_NOTIFICATION);
					navigateToData(dto.getUuid());
				}
			}
		});
		
		return editView;
	}
	
	public CommitDiscardWrapperComponent<EventDataForm> getEventDataEditComponent(final String eventUuid) {
		EventDataForm eventEditForm = new EventDataForm();
		EventDto event = findEvent(eventUuid);
		eventEditForm.setValue(event);
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<EventDataForm>(eventEditForm, eventEditForm.getFieldGroup());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(eventEditForm.getFieldGroup().isValid()) {
					EventDto eventDto = eventEditForm.getValue();
					eventDto = ef.saveEvent(eventDto);
					Notification.show("Event data saved", Type.TRAY_NOTIFICATION);
					navigateToData(eventDto.getUuid());
				}
			}
		});
		
		return editView;
	}
	
	private EventDto createNewEvent() {
		EventDto event = new EventDto();
		event.setUuid(DataHelper.createUuid());
		
		event.setEventStatus(EventStatus.POSSIBLE);
		event.setSrcFirstName("Herr");
		event.setSrcLastName("Testevent");
		event.setSrcTelNo("01901234567");
		
		LocationDto location = new LocationDto();
		location.setUuid(DataHelper.createUuid());
		event.setEventLocation(location);
		
		event.setReportDateTime(new Date());
		UserReferenceDto userReference = LoginHelper.getCurrentUserAsReference();
		event.setReportingUser(userReference);
		
		return event;
	}
	
}
