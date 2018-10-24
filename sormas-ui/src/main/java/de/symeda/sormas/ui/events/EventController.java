package de.symeda.sormas.ui.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventController {

	public void registerViews(Navigator navigator) {
		navigator.addView(EventsView.VIEW_NAME, EventsView.class);
		navigator.addView(EventDataView.VIEW_NAME, EventDataView.class);
		navigator.addView(EventParticipantsView.VIEW_NAME, EventParticipantsView.class);
	}

	public void create() {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, "Create new event");
	}

	public void navigateToIndex() {
		String navigationState = EventsView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
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

	private EventDto findEvent(String uuid) {
		return FacadeProvider.getEventFacade().getEventByUuid(uuid);
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponent() {
		EventDataForm eventCreateForm = new EventDataForm(true, UserRight.EVENT_CREATE);
		eventCreateForm.setValue(createNewEvent());
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<EventDataForm>(eventCreateForm, eventCreateForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(!eventCreateForm.getFieldGroup().isModified()) {
					EventDto dto = eventCreateForm.getValue();
					FacadeProvider.getEventFacade().saveEvent(dto);
					Notification.show("New event created", Type.WARNING_MESSAGE);
					navigateToParticipants(dto.getUuid());
				}
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventDataEditComponent(final String eventUuid) {
		EventDataForm eventEditForm = new EventDataForm(false, UserRight.EVENT_EDIT);
		EventDto event = findEvent(eventUuid);
		eventEditForm.setValue(event);
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<EventDataForm>(eventEditForm, eventEditForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(!eventEditForm.getFieldGroup().isModified()) {
					EventDto eventDto = eventEditForm.getValue();
					eventDto = FacadeProvider.getEventFacade().saveEvent(eventDto);
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

		// Initialize 'Archive' button
		if (LoginHelper.hasUserRight(UserRight.EVENT_ARCHIVE)) {
			boolean archived = FacadeProvider.getEventFacade().isArchived(eventUuid);
			Button archiveEventButton = new Button();
			archiveEventButton.addStyleName(ValoTheme.BUTTON_LINK);
			if (archived) {
				archiveEventButton.setCaption("De-Archive");
			} else {
				archiveEventButton.setCaption("Archive");
			}
			archiveEventButton.addClickListener(e -> {
				editView.commit();
				archiveOrDearchiveEvent(eventUuid, !archived);
			});

			editView.getButtonsPanel().addComponentAsFirst(archiveEventButton);
			editView.getButtonsPanel().setComponentAlignment(archiveEventButton, Alignment.BOTTOM_LEFT);
		}

		return editView;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void showBulkEventDataEditComponent(Collection<Object> selectedRows) {
		if (selectedRows.size() == 0) {
			new Notification("No events selected", "You have not selected any events.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
			return;
		}

		List<EventIndexDto> selectedEvents = new ArrayList(selectedRows);

		// Create a temporary event in order to use the CommitDiscardWrapperComponent
		EventDto tempEvent = new EventDto();

		BulkEventDataForm form = new BulkEventDataForm();
		form.setValue(tempEvent);
		final CommitDiscardWrapperComponent<BulkEventDataForm> editView = new CommitDiscardWrapperComponent<BulkEventDataForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, "Edit events");

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				EventDto updatedTempEvent = form.getValue();
				for (EventIndexDto indexDto : selectedEvents) {
					EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(indexDto.getUuid());
					if (form.getEventStatusCheckBox().getValue() == true) {
						eventDto.setEventStatus(updatedTempEvent.getEventStatus());
					}
					if (form.getEventTypeCheckBox().getValue() == true) {
						eventDto.setEventType(updatedTempEvent.getEventType());
					}

					FacadeProvider.getEventFacade().saveEvent(eventDto);
				}
				popupWindow.close();
				navigateToIndex();
				Notification.show("All events have been edited", Type.HUMANIZED_MESSAGE);
			}
		});

		editView.addDiscardListener(new DiscardListener() {
			@Override
			public void onDiscard() {
				popupWindow.close();
			}
		});
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

	private void archiveOrDearchiveEvent(String eventUuid, boolean archive) {
		if (archive) {
			Label contentLabel = new Label("Are you sure you want to archive this event? This will not remove it from the system or any statistics, but hide it from the normal event directory.");
			VaadinUiUtil.showConfirmationPopup("Archive Event", contentLabel, "Yes", "No", 640, e -> {
				if (e.booleanValue() == true) {
					FacadeProvider.getEventFacade().archiveOrDearchiveEvent(eventUuid, true);
					Notification.show("Event has been archived.", Type.ASSISTIVE_NOTIFICATION);
					navigateToData(eventUuid);
				}
			});
		} else {
			Label contentLabel = new Label("Are you sure you want to de-archive this event? This will make it appear in the normal event directory again.");
			VaadinUiUtil.showConfirmationPopup("De-Archive Event", contentLabel, "Yes", "No", 640, e -> {
				if (e.booleanValue()) {
					FacadeProvider.getEventFacade().archiveOrDearchiveEvent(eventUuid, false);
					Notification.show("Event has been de-archived.", Type.ASSISTIVE_NOTIFICATION);
					navigateToData(eventUuid);
				}
			});
		}
	}

	public void deleteAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No events selected", "You have not selected any events.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow("Are you sure you want to delete all " + selectedRows.size() + " selected events?", new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getEventFacade().deleteEvent(new EventReferenceDto(((EventIndexDto) selectedRow).getUuid()), LoginHelper.getCurrentUser().getUuid());
					}
					callback.run();
					new Notification("Events deleted", "All selected events have been deleted.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}


	public void archiveAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No events selected", "You have not selected any events.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup("Confirm archiving", new Label("Are you sure you want to archive all " + selectedRows.size() + " selected events?"), "Yes", "No", null, e -> {
				if (e.booleanValue() == true) {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getEventFacade().archiveOrDearchiveEvent(((EventIndexDto) selectedRow).getUuid(), true);
					}
					callback.run();
					new Notification("Events archived", "All selected events have been archived.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

	public void dearchiveAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No events selected", "You have not selected any events.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup("Confirm de-archiving", new Label("Are you sure you want to de-archive all " + selectedRows.size() + " selected events?"), "Yes", "No", null, e -> {
				if (e.booleanValue() == true) {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getEventFacade().archiveOrDearchiveEvent(((EventIndexDto) selectedRow).getUuid(), false);
					}
					callback.run();
					new Notification("Events de-archived", "All selected events have been de-archived.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

}
