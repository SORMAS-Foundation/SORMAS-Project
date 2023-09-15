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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupDto;
import de.symeda.sormas.api.event.EventGroupFacade;
import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.events.groups.EventGroupSelectionField;
import de.symeda.sormas.ui.utils.ArchivingController;
import de.symeda.sormas.ui.utils.BulkOperationHandler;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class EventGroupController {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private void linkEventsToGroup(
		List<EventReferenceDto> eventReferences,
		EventGroupReferenceDto eventGroupReference,
		Consumer<List<EventReferenceDto>> callback) {

		List<String> eventUuids = eventReferences.stream().map(EventReferenceDto::getUuid).collect(Collectors.toList());
		List<String> eventGroupUuids = Collections.singletonList(eventGroupReference.getUuid());

		List<String> alreadyLinkedEventUuidsToGroup =
			FacadeProvider.getEventGroupFacade().getAlreadyLinkedEventUuidsToGroup(eventUuids, eventGroupUuids);

		String messageEventsLinkedToGroup = eventReferences.size() > 1 ? Strings.messageEventsLinkedToGroup : Strings.messageEventLinkedToGroup;
		new BulkOperationHandler<EventReferenceDto>(
			messageEventsLinkedToGroup,
			null,
			Strings.headingSomeEventsNotLinked,
			Strings.headingEventsNotLinked,
			Strings.messageCountEventsNotLinked,
			null,
			null,
			Strings.messageCountEventsNotLinkedAccessDeniedReason,
			Strings.messageAllEventsAlreadyLinkedToGroup,
			Strings.infoBulkProcessFinishedWithSkipsOutsideJurisdictionOrNotEligible,
			Strings.infoBulkProcessFinishedWithoutSuccess)
				.doBulkOperation(
					batch -> FacadeProvider.getEventGroupFacade()
						.linkEventsToGroups(
							batch.stream().map(EventReferenceDto::getUuid).collect(Collectors.toList()),
							eventGroupUuids,
							alreadyLinkedEventUuidsToGroup),
					new ArrayList<>(eventReferences),
					callback);
	}

	public void create(EventReferenceDto eventReference) {

		EventDto eventByUuid = FacadeProvider.getEventFacade().getEventByUuid(eventReference.getUuid(), false);
		UserProvider user = UserProvider.getCurrent();

		EventGroupCriteria eventGroupCriteria = new EventGroupCriteria();
		Set<String> eventGroupUuids = FacadeProvider.getEventGroupFacade()
			.getCommonEventGroupsByEvents(Collections.singletonList(eventByUuid.toReference()))
			.stream()
			.map(EventGroupReferenceDto::getUuid)
			.collect(Collectors.toSet());
		eventGroupCriteria.setExcludedUuids(eventGroupUuids);
		if (user.hasUserRight(UserRight.EVENTGROUP_CREATE) && user.hasUserRight(UserRight.EVENTGROUP_LINK)) {
			long events = FacadeProvider.getEventGroupFacade().count(eventGroupCriteria);
			if (events > 0) {
				ControllerProvider.getEventGroupController().selectOrCreate(eventReference);
			} else {
				ControllerProvider.getEventGroupController().create(Collections.singletonList(eventReference), (r) -> SormasUI.refreshView());
			}
		} else {
			long events = FacadeProvider.getEventGroupFacade().count(eventGroupCriteria);
			if (events > 0) {
				ControllerProvider.getEventGroupController().select(eventReference);
			} else {
				new Notification(
					I18nProperties.getString(Strings.headingEventGroupLinkEventIssue),
					I18nProperties.getString(Strings.errorNotRequiredRights),
					Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		}
	}

	public EventGroupDto create(List<EventReferenceDto> events, Consumer<List<EventReferenceDto>> callback) {
		CommitDiscardWrapperComponent<EventGroupDataForm> eventCreateComponent = getEventGroupCreateComponent(events, callback);
		EventGroupDto eventGroupDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEventGroup));
		return eventGroupDto;
	}

	public void select(EventReferenceDto eventReference) {
		select(Collections.singletonList(eventReference), (r) -> SormasUI.refreshView());
	}

	public void select(List<EventReferenceDto> eventReferences, Consumer<List<EventReferenceDto>> callback) {
		Set<String> excludedEventGroupUuids = FacadeProvider.getEventGroupFacade()
			.getCommonEventGroupsByEvents(eventReferences)
			.stream()
			.map(EventGroupReferenceDto::getUuid)
			.collect(Collectors.toSet());
		EventGroupSelectionField selectionField = new EventGroupSelectionField(excludedEventGroupUuids);
		selectionField.setWidth(1024, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventGroupSelectionField> component = new CommitDiscardWrapperComponent<>(selectionField);
		component.addCommitListener(() -> {
			EventGroupIndexDto selectedEventGroup = selectionField.getValue();
			if (selectedEventGroup != null) {
				linkEventsToGroup(eventReferences, selectedEventGroup.toReference(), callback);
			}
		});

		selectionField.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickEventGroup));
	}

	public void selectOrCreate(EventReferenceDto eventReference) {
		selectOrCreate(Collections.singletonList(eventReference), (r) -> SormasUI.refreshView());
	}

	public void selectOrCreate(List<EventReferenceDto> eventReferences, Consumer<List<EventReferenceDto>> callback) {
		Set<String> excludedEventGroupUuids = FacadeProvider.getEventGroupFacade()
			.getCommonEventGroupsByEvents(eventReferences)
			.stream()
			.map(EventGroupReferenceDto::getUuid)
			.collect(Collectors.toSet());
		EventGroupSelectionField selectionField = new EventGroupSelectionField(excludedEventGroupUuids);
		selectionField.setWidth(1024, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventGroupSelectionField> component = new CommitDiscardWrapperComponent<>(selectionField);
		component.addCommitListener(() -> {
			EventGroupIndexDto selectedEventGroup = selectionField.getValue();
			if (selectedEventGroup != null) {
				linkEventsToGroup(eventReferences, selectedEventGroup.toReference(), callback);
			} else {
				create(eventReferences, callback);
			}
		});

		selectionField.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEventGroup));
	}

	public EventGroupDto createNewEventGroup() {
		return EventGroupDto.build();
	}

	public CommitDiscardWrapperComponent<EventGroupDataForm> getEventGroupCreateComponent(
		List<EventReferenceDto> eventReferences,
		Consumer<List<EventReferenceDto>> callback) {
		EventGroupDataForm createForm = new EventGroupDataForm(true);
		createForm.setValue(createNewEventGroup());

		final CommitDiscardWrapperComponent<EventGroupDataForm> editView = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENTGROUP_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				EventGroupDto dto = createForm.getValue();
				EventGroupFacade eventGroupFacade = FacadeProvider.getEventGroupFacade();
				dto = eventGroupFacade.saveEventGroup(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventGroupCreated), Type.WARNING_MESSAGE);

				linkEventsToGroup(eventReferences, dto.toReference(), callback);
			}
		});

		return editView;
	}

	public void unlinkEventGroup(EventReferenceDto eventReference, EventGroupReferenceDto eventGroupReference) {
		FacadeProvider.getEventGroupFacade().unlinkEventGroup(eventReference, eventGroupReference);
		FacadeProvider.getEventGroupFacade().notifyEventRemovedFromEventGroup(eventGroupReference, Collections.singletonList(eventReference));

		Notification.show(I18nProperties.getString(Strings.messageEventUnlinkedFromEventGroup), Type.TRAY_NOTIFICATION);
	}

	public CommitDiscardWrapperComponent<?> getEventGroupEditComponent(String uuid) {

		EventGroupDto eventGroup = FacadeProvider.getEventGroupFacade().getEventGroupByUuid(uuid);
		EventGroupDataForm eventGroupEditForm = new EventGroupDataForm(false);
		eventGroupEditForm.setValue(eventGroup);
		UserProvider user = UserProvider.getCurrent();
		final CommitDiscardWrapperComponent<EventGroupDataForm> editView =
			new CommitDiscardWrapperComponent<>(eventGroupEditForm, true, eventGroupEditForm.getFieldGroup());

		List<RegionReferenceDto> regions = FacadeProvider.getEventGroupFacade().getEventGroupRelatedRegions(uuid);
		boolean hasRegion = user.hasNationJurisdictionLevel() || regions.stream().allMatch(user::hasRegion);
		editView.setReadOnly(hasRegion);

		if (user.hasUserRight(UserRight.EVENTGROUP_EDIT) && hasRegion) {
			editView.addCommitListener(() -> {
				if (!eventGroupEditForm.getFieldGroup().isModified()) {
					EventGroupDto updatedEventGroup = eventGroupEditForm.getValue();
					FacadeProvider.getEventGroupFacade().saveEventGroup(updatedEventGroup);
					Notification.show(I18nProperties.getString(Strings.messageEventGroupSaved), Type.WARNING_MESSAGE);
					SormasUI.refreshView();
				}
			});
		}

		if (user.hasUserRight(UserRight.EVENTGROUP_DELETE) && hasRegion) {
			editView.addDeleteListener(() -> {
				deleteEventGroup(eventGroup);
				UI.getCurrent().getNavigator().navigateTo(EventsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityEventGroup));
		}

		// Initialize 'Archive' button
		if (user.hasUserRight(UserRight.EVENTGROUP_ARCHIVE) && hasRegion) {
			boolean archived = FacadeProvider.getEventGroupFacade().isArchived(uuid);
			Button archiveEventButton = ButtonHelper.createButton(
				ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID,
				I18nProperties.getCaption(archived ? Captions.actionDearchiveCoreEntity : Captions.actionArchiveCoreEntity),
				e -> archiveOrDearchiveEventGroup(uuid, !archived),
				ValoTheme.BUTTON_LINK);

			editView.getButtonsPanel().addComponentAsFirst(archiveEventButton);
			editView.getButtonsPanel().setComponentAlignment(archiveEventButton, Alignment.BOTTOM_LEFT);
		}

		editView.addDiscardListener(SormasUI::refreshView);

		editView.restrictEditableComponentsOnEditView(
			UserRight.EVENTGROUP_EDIT,
			null,
			UserRight.EVENTGROUP_DELETE,
			UserRight.EVENTGROUP_ARCHIVE,
			null,
			true);

		return editView;

	}

	public TitleLayout getEventGroupViewTitleLayout(String uuid) {
		EventGroupDto eventGroup = FacadeProvider.getEventGroupFacade().getEventGroupByUuid(uuid);

		TitleLayout titleLayout = new TitleLayout();

		String shortUuid = DataHelper.getShortUuid(eventGroup.getUuid());
		String mainRowText = eventGroup.getName() + " (" + shortUuid + ")";
		titleLayout.addMainRow(mainRowText);

		return titleLayout;
	}

	public void navigateToData(String uuid) {
		String navigationState = EventGroupDataView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	private void deleteEventGroup(EventGroupDto eventGroup) {
		FacadeProvider.getEventGroupFacade().deleteEventGroup(eventGroup.getUuid());
	}

	private void archiveOrDearchiveEventGroup(String uuid, boolean archive) {

		if (archive) {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationArchiveEventGroup),
					I18nProperties.getString(Strings.entityEventGroup).toLowerCase(),
					I18nProperties.getString(Strings.entityEventGroup).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingArchiveEventGroup),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				confirmed -> {
					if (confirmed) {
						FacadeProvider.getEventGroupFacade().archiveOrDearchiveEventGroup(uuid, true);
						Notification.show(
							String.format(
								I18nProperties.getString(Strings.messageEventGroupArchived),
								I18nProperties.getString(Strings.entityEventGroup)),
							Type.ASSISTIVE_NOTIFICATION);
						navigateToData(uuid);
					}
				});
		} else {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationDearchiveEventGroup),
					I18nProperties.getString(Strings.entityEventGroup).toLowerCase(),
					I18nProperties.getString(Strings.entityEventGroup).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingDearchiveEventGroup),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				confirmed -> {
					if (confirmed) {
						FacadeProvider.getEventGroupFacade().archiveOrDearchiveEventGroup(uuid, false);
						Notification.show(
							String.format(
								I18nProperties.getString(Strings.messageEventGroupDearchived),
								I18nProperties.getString(Strings.entityEventGroup)),
							Type.ASSISTIVE_NOTIFICATION);
						navigateToData(uuid);
					}
				});
		}
	}

	public void linkAllToGroup(Set<EventIndexDto> selectedItems, Consumer<List<EventIndexDto>> callback) {
		if (selectedItems.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoEventsSelected),
				I18nProperties.getString(Strings.messageNoEventsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		UserProvider user = UserProvider.getCurrent();

		List<EventReferenceDto> eventReferences = selectedItems.stream().map(EventIndexDto::toReference).collect(Collectors.toList());
		List<String> eventUuids = eventReferences.stream().map(EventReferenceDto::getUuid).collect(Collectors.toList());

		if (!user.hasNationJurisdictionLevel()) {
			for (String eventUuid : eventUuids) {
				if (!FacadeProvider.getEventFacade().isInJurisdictionOrOwned(eventUuid)) {
					new Notification(
						I18nProperties.getString(Strings.headingEventGroupLinkEventIssue),
						I18nProperties.getString(Strings.errorEventsFromAnotherJurisdiction),
						Type.WARNING_MESSAGE,
						false).show(Page.getCurrent());
					return;
				}
			}
		}

		EventGroupCriteria eventGroupCriteria = new EventGroupCriteria();
		Set<String> eventGroupUuids = FacadeProvider.getEventGroupFacade()
			.getCommonEventGroupsByEvents(eventReferences)
			.stream()
			.map(EventGroupReferenceDto::getUuid)
			.collect(Collectors.toSet());
		eventGroupCriteria.setExcludedUuids(eventGroupUuids);
		Consumer<List<EventReferenceDto>> callbackWrapper = remainingReferences -> callback.accept(
			selectedItems.stream()
				.filter(e -> remainingReferences.stream().anyMatch(r -> r.getUuid().equals(e.getUuid())))
				.collect(Collectors.toList()));

		if (user.hasUserRight(UserRight.EVENTGROUP_CREATE) && user.hasUserRight(UserRight.EVENTGROUP_LINK)) {
			long eventCount = FacadeProvider.getEventGroupFacade().count(eventGroupCriteria);
			if (eventCount > 0) {
				selectOrCreate(eventReferences, callbackWrapper);
			} else {
				create(eventReferences, callbackWrapper);
			}
		} else if (user.hasUserRight(UserRight.EVENTGROUP_CREATE)) {
			create(eventReferences, callbackWrapper);
		} else {
			long eventCount = FacadeProvider.getEventGroupFacade().count(eventGroupCriteria);
			if (eventCount > 0) {
				select(eventReferences, callbackWrapper);
			} else {
				new Notification(
					I18nProperties.getString(Strings.headingEventGroupLinkEventIssue),
					I18nProperties.getString(Strings.errorNotRequiredRights),
					Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		}

	}
}
