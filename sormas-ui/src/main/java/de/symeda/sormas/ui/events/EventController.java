package de.symeda.sormas.ui.events;

import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventController {
	
	private EventFacade ef = FacadeProvider.getEventFacade();

	public void registerViews(Navigator navigator) {
		navigator.addView(EventsView.VIEW_NAME, EventsView.class);
		navigator.addView(EventDataView.VIEW_NAME, EventDataView.class);
		navigator.addView(EventParticipantsView.VIEW_NAME, EventParticipantsView.class);
	}
	
	public void create() {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, "Create new event");
	}
	
	public void navigateToData(String eventUuid) {
		String navigationState = EventDataView.VIEW_NAME + "/" + eventUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}
	
	public void navigateToParticipants(String eventUuid) {
		String navigationState = EventParticipantsView.VIEW_NAME + "/" + eventUuid;
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
	
	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponent() {
		EventDataForm eventCreateForm = new EventDataForm();
		eventCreateForm.setValue(createNewEvent());
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<EventDataForm>(eventCreateForm, eventCreateForm.getFieldGroup());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(!eventCreateForm.getFieldGroup().isModified()) {
					EventDto dto = eventCreateForm.getValue();
					ef.saveEvent(dto);
					Notification.show("New event created", Type.WARNING_MESSAGE);
					navigateToParticipants(dto.getUuid());
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
				if(!eventEditForm.getFieldGroup().isModified()) {
					EventDto eventDto = eventEditForm.getValue();
					eventDto = ef.saveEvent(eventDto);
					Notification.show("Event data saved", Type.WARNING_MESSAGE);
					navigateToData(eventDto.getUuid());
				}
			}
		});
		
		if (LoginHelper.getCurrentUserRoles().contains(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getEventFacade().deleteEvent(event.toReference(), LoginHelper.getCurrentUserAsReference().getUuid());
					UI.getCurrent().getNavigator().navigateTo(EventsView.VIEW_NAME);
				}
			}, I18nProperties.getFieldCaption("Event"));
		}
		
		return editView;
	}
	
	private EventDto createNewEvent() {
		EventDto event = new EventDto();
		event.setUuid(DataHelper.createUuid());
		
		event.setEventStatus(EventStatus.POSSIBLE);
		LocationDto location = new LocationDto();
		location.setUuid(DataHelper.createUuid());
		location.setRegion(LoginHelper.getCurrentUser().getRegion());
		event.setEventLocation(location);
		
		event.setReportDateTime(new Date());
		UserReferenceDto userReference = LoginHelper.getCurrentUserAsReference();
		event.setReportingUser(userReference);
		
		return event;
	}
	
}
