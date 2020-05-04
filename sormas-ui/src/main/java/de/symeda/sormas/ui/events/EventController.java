/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import java.util.Collection;

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
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
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
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
	}

	public void navigateToIndex() {
		String navigationState = EventsView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToData(String eventUuid) {
		navigateToData(eventUuid, false);
	}
	
	public void navigateToData(String eventUuid, boolean openTab) {
		String navigationState = EventDataView.VIEW_NAME + "/" + eventUuid;
		if (openTab) {
			SormasUI.get().getPage().open(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + navigationState, "_blank", false);
		} else {
			SormasUI.get().getNavigator().navigateTo(navigationState);
		}	
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
					Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.WARNING_MESSAGE);
					navigateToParticipants(dto.getUuid());
				}
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventDataEditComponent(final String eventUuid) {
		EventDto event = findEvent(eventUuid);
		EventDataForm eventEditForm = new EventDataForm(false, UserRight.EVENT_EDIT);
		eventEditForm.setValue(event);
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<EventDataForm>(eventEditForm, eventEditForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!eventEditForm.getFieldGroup().isModified()) {
				EventDto eventDto = eventEditForm.getValue();
				eventDto = FacadeProvider.getEventFacade().saveEvent(eventDto);
				Notification.show(I18nProperties.getString(Strings.messageEventSaved), Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			}
		});

		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getEventFacade().deleteEvent(event.getUuid());
				UI.getCurrent().getNavigator().navigateTo(EventsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityEvent));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_ARCHIVE)) {
			boolean archived = FacadeProvider.getEventFacade().isArchived(eventUuid);
			Button archiveEventButton = ButtonHelper.createButton(archived ? Captions.actionDearchive : Captions.actionArchive, e -> {
				editView.commit();
				archiveOrDearchiveEvent(eventUuid, !archived);
			}, ValoTheme.BUTTON_LINK);

			editView.getButtonsPanel().addComponentAsFirst(archiveEventButton);
			editView.getButtonsPanel().setComponentAlignment(archiveEventButton, Alignment.BOTTOM_LEFT);
		}

		return editView;
	}

	public void showBulkEventDataEditComponent(Collection<EventIndexDto> selectedEvents) {
		if (selectedEvents.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoEventsSelected), 
					I18nProperties.getString(Strings.messageNoEventsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
			return;
		}

		// Create a temporary event in order to use the CommitDiscardWrapperComponent
		EventDto tempEvent = new EventDto();

		BulkEventDataForm form = new BulkEventDataForm();
		form.setValue(tempEvent);
		final CommitDiscardWrapperComponent<BulkEventDataForm> editView = new CommitDiscardWrapperComponent<BulkEventDataForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditEvents));

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				EventDto updatedTempEvent = form.getValue();
				for (EventIndexDto indexDto : selectedEvents) {
					EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(indexDto.getUuid());
					if (form.getEventStatusCheckBox().getValue() == true) {
						eventDto.setEventStatus(updatedTempEvent.getEventStatus());
					}

					FacadeProvider.getEventFacade().saveEvent(eventDto);
				}
				popupWindow.close();
				navigateToIndex();
				Notification.show(I18nProperties.getString(Strings.messageEventsEdited), Type.HUMANIZED_MESSAGE);
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
		EventDto event = EventDto.build();

		event.getEventLocation().setRegion(UserProvider.getCurrent().getUser().getRegion());
		UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
		event.setReportingUser(userReference);

		return event;
	}

	private void archiveOrDearchiveEvent(String eventUuid, boolean archive) {
		if (archive) {
			Label contentLabel = new Label(String.format(I18nProperties.getString(Strings.confirmationArchiveEvent), I18nProperties.getString(Strings.entityEvent).toLowerCase(), I18nProperties.getString(Strings.entityEvent).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingArchiveEvent), contentLabel, I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), 640, e -> {
				if (e.booleanValue() == true) {
					FacadeProvider.getEventFacade().archiveOrDearchiveEvent(eventUuid, true);
					Notification.show(String.format(I18nProperties.getString(Strings.messageEventArchived), I18nProperties.getString(Strings.entityEvent)), Type.ASSISTIVE_NOTIFICATION);
					navigateToData(eventUuid);
				}
			});
		} else {
			Label contentLabel = new Label(String.format(I18nProperties.getString(Strings.confirmationDearchiveEvent), I18nProperties.getString(Strings.entityEvent).toLowerCase(), I18nProperties.getString(Strings.entityEvent).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingDearchiveEvent), contentLabel, I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), 640, e -> {
				if (e.booleanValue()) {
					FacadeProvider.getEventFacade().archiveOrDearchiveEvent(eventUuid, false);
					Notification.show(String.format(I18nProperties.getString(Strings.messageEventDearchived), I18nProperties.getString(Strings.entityEvent)), Type.ASSISTIVE_NOTIFICATION);
					navigateToData(eventUuid);
				}
			});
		}
	}

	public void deleteAllSelectedItems(Collection<EventIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoEventsSelected), 
					I18nProperties.getString(Strings.messageNoEventsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteEvents), selectedRows.size()), () -> {
				for (EventIndexDto selectedRow : selectedRows) {
					FacadeProvider.getEventFacade().deleteEvent(selectedRow.getUuid());
				}
				callback.run();
				new Notification(I18nProperties.getString(Strings.headingEventsDeleted),
						I18nProperties.getString(Strings.messageEventsDeleted), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
			});
		}
	}


	public void archiveAllSelectedItems(Collection<EventIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoEventsSelected), 
					I18nProperties.getString(Strings.messageNoEventsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingConfirmArchiving), 
					new Label(String.format(I18nProperties.getString(Strings.confirmationArchiveEvents), selectedRows.size())), 
					I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), null, e -> {
				if (e.booleanValue() == true) {
					for (EventIndexDto selectedRow : selectedRows) {
						FacadeProvider.getEventFacade().archiveOrDearchiveEvent(selectedRow.getUuid(), true);
					}
					callback.run();
					new Notification(I18nProperties.getString(Strings.headingEventsArchived), 
							I18nProperties.getString(Strings.messageEventsArchived), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

	public void dearchiveAllSelectedItems(Collection<EventIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoEventsSelected), 
					I18nProperties.getString(Strings.messageNoEventsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingConfirmDearchiving), 
					new Label(String.format(I18nProperties.getString(Strings.confirmationDearchiveEvents), selectedRows.size())), 
					I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), null, e -> {
				if (e.booleanValue() == true) {
					for (EventIndexDto selectedRow : selectedRows) {
						FacadeProvider.getEventFacade().archiveOrDearchiveEvent(selectedRow.getUuid(), false);
					}
					callback.run();
					new Notification(I18nProperties.getString(Strings.headingEventsDearchived), 
							I18nProperties.getString(Strings.messageEventsDearchived), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

}
