/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.events;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.action.ActionStatsComponent;
import de.symeda.sormas.ui.docgeneration.EventDocumentsComponent;
import de.symeda.sormas.ui.document.DocumentListComponent;
import de.symeda.sormas.ui.events.eventLink.EventListComponent;
import de.symeda.sormas.ui.events.eventLink.SuperordinateEventComponent;
import de.symeda.sormas.ui.events.groups.EventGroupListComponent;
import de.symeda.sormas.ui.externalsurveillanceservice.ExternalSurveillanceServiceGateway;
import de.symeda.sormas.ui.externalsurveillanceservice.ExternalSurveillanceShareComponent;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.ArchivingController;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

public class EventDataView extends AbstractEventView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String EVENT_LOC = "event";
	public static final String TASKS_LOC = "tasks";
	public static final String ACTIONS_LOC = "actions";
	public static final String SHORTCUT_LINKS_LOC = "shortcut-links";
	public static final String DOCUMENTS_LOC = "documents";
	public static final String SUBORDINATE_EVENTS_LOC = "subordinate-events";
	public static final String SUPERORDINATE_EVENT_LOC = "superordinate-event";
	public static final String EVENT_GROUPS_LOC = "event-groups";
	public static final String SORMAS_TO_SORMAS_LOC = "sormasToSormas";

	private CommitDiscardWrapperComponent<?> editComponent;
	private ExternalSurveillanceShareComponent externalSurvToolLayout;

	public EventDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(getEventRef().getUuid(), false);

		setHeightUndefined();

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		editComponent =
			ControllerProvider.getEventController().getEventDataEditComponent(getEventRef().getUuid(), this::setExternalSurvToolLayoutVisibility);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(
			editComponent,
			TASKS_LOC,
			ACTIONS_LOC,
			DOCUMENTS_LOC,
			EventDocumentsComponent.DOCGENERATION_LOC,
			SUPERORDINATE_EVENT_LOC,
			SUBORDINATE_EVENTS_LOC,
			EVENT_GROUPS_LOC,
			SORMAS_TO_SORMAS_LOC,
			ExternalSurveillanceServiceGateway.EXTERANEL_SURVEILLANCE_TOOL_GATEWAY_LOC,
			SHORTCUT_LINKS_LOC);

		container.addComponent(layout);

		externalSurvToolLayout = ExternalSurveillanceServiceGateway.addComponentToLayout(layout.getSidePanelComponent(), editComponent, event);
		setExternalSurvToolLayoutVisibility(event.getEventStatus());

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TASK_MANAGEMENT)) {
			TaskListComponent taskList = new TaskListComponent(TaskContext.EVENT, getEventRef(), event.getDisease());
			taskList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(taskList, TASKS_LOC);
		}

		ActionStatsComponent actionList = new ActionStatsComponent(ActionContext.EVENT, getEventRef());
		actionList.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addSidePanelComponent(actionList, ACTIONS_LOC);

		DocumentListComponent documentList = null;
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.DOCUMENTS)) {
			// TODO: user rights?
			documentList = new DocumentListComponent(DocumentRelatedEntityType.EVENT, getEventRef(), UserRight.EVENT_EDIT, event.isPseudonymized());
			layout.addSidePanelComponent(new SideComponentLayout(documentList), DOCUMENTS_LOC);
		}

		EventDocumentsComponent eventDocuments = new EventDocumentsComponent(getEventRef(), documentList);
		eventDocuments.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addSidePanelComponent(eventDocuments, EventDocumentsComponent.DOCGENERATION_LOC);

		boolean eventHierarchiesFeatureEnabled = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_HIERARCHIES);
		if (eventHierarchiesFeatureEnabled) {
			SuperordinateEventComponent superordinateEventComponent = new SuperordinateEventComponent(event, () -> editComponent.discard());
			superordinateEventComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(superordinateEventComponent, SUPERORDINATE_EVENT_LOC);

			EventListComponent subordinateEventList = new EventListComponent(event.toReference());
			subordinateEventList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(subordinateEventList, SUBORDINATE_EVENTS_LOC);
		}

		boolean eventGroupsFeatureEnabled = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_GROUPS);
		if (eventGroupsFeatureEnabled) {
			EventReferenceDto eventReference = event.toReference();
			EventGroupListComponent eventGroupsList = new EventGroupListComponent(eventReference);
			eventGroupsList.addSideComponentCreateEventListener(e -> showNavigationConfirmPopupIfDirty(() -> {
				EventDto eventByUuid = FacadeProvider.getEventFacade().getEventByUuid(eventReference.getUuid(), false);
				UserProvider user = UserProvider.getCurrent();
				if (!user.hasNationJurisdictionLevel() && !user.hasRegion(eventByUuid.getEventLocation().getRegion())) {
					new Notification(
						I18nProperties.getString(Strings.headingEventGroupLinkEventIssue),
						I18nProperties.getString(Strings.errorEventFromAnotherJurisdiction),
						Notification.Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
					return;
				}

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
						ControllerProvider.getEventGroupController().create(eventReference);
					}
				} else if (user.hasUserRight(UserRight.EVENTGROUP_CREATE)) {
					ControllerProvider.getEventGroupController().create(eventReference);
				} else {
					long events = FacadeProvider.getEventGroupFacade().count(eventGroupCriteria);
					if (events > 0) {
						ControllerProvider.getEventGroupController().select(eventReference);
					} else {
						new Notification(
							I18nProperties.getString(Strings.headingEventGroupLinkEventIssue),
							I18nProperties.getString(Strings.errorNotRequiredRights),
							Notification.Type.ERROR_MESSAGE,
							false).show(Page.getCurrent());
					}
				}
			}));
			layout.addSidePanelComponent(new SideComponentLayout(eventGroupsList), EVENT_GROUPS_LOC);
		}

		boolean sormasToSormasEnabled = FacadeProvider.getSormasToSormasFacade().isSharingEventsEnabledForUser();
		if (sormasToSormasEnabled || event.getSormasToSormasOriginInfo() != null) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(event, sormasToSormasEnabled);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addSidePanelComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		VerticalLayout shortcutLinksLayout = new VerticalLayout();
		shortcutLinksLayout.setMargin(false);
		shortcutLinksLayout.setSpacing(true);

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_VIEW)) {
			Button seeEventCasesBtn = ButtonHelper.createButton(
				"eventLinkToCases",
				I18nProperties.getCaption(Captions.eventLinkToCases),
				thisEvent -> ControllerProvider.getCaseController().navigateTo(new CaseCriteria().eventLike(getEventRef().getUuid())),
				ValoTheme.BUTTON_PRIMARY);
			shortcutLinksLayout.addComponent(seeEventCasesBtn);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			Button seeEventContactsBtn = ButtonHelper.createButton(
				"eventLinkToContacts",
				I18nProperties.getCaption(Captions.eventLinkToContacts),
				thisEvent -> ControllerProvider.getContactController().navigateTo(new ContactCriteria().eventUuid(getEventRef().getUuid())),
				ValoTheme.BUTTON_PRIMARY);
			shortcutLinksLayout.addComponent(seeEventContactsBtn);
		}

		LocationDto eventLocationDto = ((EventDataForm) editComponent.getWrappedComponent()).getValue().getEventLocation();
		if (eventLocationDto.getFacility() != null) {
			Button seeEventsWithinTheSameFacility = ButtonHelper.createButton(
				"eventLinkToEventsWithinTheSameFacility",
				I18nProperties.getCaption(Captions.eventLinkToEventsWithinTheSameFacility),
				thisEvent -> ControllerProvider.getEventController()
					.navigateTo(
						new EventCriteria().region(eventLocationDto.getRegion())
							.district(eventLocationDto.getDistrict())
							.eventCommunity(eventLocationDto.getCommunity())
							.typeOfPlace(TypeOfPlace.FACILITY)
							.facilityType(eventLocationDto.getFacilityType())
							.facility(eventLocationDto.getFacility())),
				ValoTheme.BUTTON_PRIMARY);
			shortcutLinksLayout.addComponent(seeEventsWithinTheSameFacility);
		}

		layout.addSidePanelComponent(shortcutLinksLayout, SHORTCUT_LINKS_LOC);

		if(!UserProvider.getCurrent().hasUserRight(UserRight.EVENT_EDIT)){
			layout.setEnabled(false);
		}

		EditPermissionType eventEditAllowed = FacadeProvider.getEventFacade().isEventEditAllowed(event.getUuid());

		if (eventEditAllowed == EditPermissionType.ARCHIVING_STATUS_ONLY) {
			layout.disable(ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID);
		} else if (eventEditAllowed == EditPermissionType.REFUSED) {
			layout.disable();
		}
	}

	private void setExternalSurvToolLayoutVisibility(EventStatus eventStatus) {
		if (externalSurvToolLayout != null) {
			externalSurvToolLayout.setVisible(eventStatus == EventStatus.CLUSTER);
		}
	}
}
