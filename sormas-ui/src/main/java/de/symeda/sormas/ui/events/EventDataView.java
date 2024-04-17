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

import com.vaadin.ui.Button;
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
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
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
		container.setEnabled(true);

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

		final String uuid = event.getUuid();
		final EditPermissionType eventEditAllowed = FacadeProvider.getEventFacade().getEditPermissionType(uuid);
		boolean isEditAllowed = isEditAllowed();

		externalSurvToolLayout = ExternalSurveillanceServiceGateway.addComponentToLayout(layout, editComponent, event, isEditAllowed);
		setExternalSurvToolLayoutVisibility(event.getEventStatus());

		if (UiUtil.permitted(FeatureType.TASK_MANAGEMENT, UserRight.TASK_VIEW)) {
			TaskListComponent taskList =
				new TaskListComponent(TaskContext.EVENT, getEventRef(), event.getDisease(), this::showUnsavedChangesPopup, isEditAllowed);
			taskList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(taskList, TASKS_LOC);
		}

		ActionStatsComponent actionList = new ActionStatsComponent(ActionContext.EVENT, getEventRef());
		actionList.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addSidePanelComponent(actionList, ACTIONS_LOC);

		DocumentListComponent documentList = null;
		if (UiUtil.permitted(FeatureType.DOCUMENTS, UserRight.DOCUMENT_VIEW)) {

			boolean isDocumentDeleteAllowed =
				EditPermissionType.ALLOWED.equals(eventEditAllowed) || EditPermissionType.WITHOUT_OWNERSHIP.equals(eventEditAllowed);
			documentList = new DocumentListComponent(
				DocumentRelatedEntityType.EVENT,
				getEventRef(),
				UserRight.EVENT_EDIT,
				event.isPseudonymized(),
				isEditAllowed,
				isDocumentDeleteAllowed);
			layout.addSidePanelComponent(new SideComponentLayout(documentList), DOCUMENTS_LOC);
		}

		EventDocumentsComponent eventDocuments = new EventDocumentsComponent(getEventRef(), documentList);
		eventDocuments.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addSidePanelComponent(eventDocuments, EventDocumentsComponent.DOCGENERATION_LOC);

		boolean eventHierarchiesFeatureEnabled = UiUtil.enabled(FeatureType.EVENT_HIERARCHIES);
		if (eventHierarchiesFeatureEnabled) {
			SuperordinateEventComponent superordinateEventComponent = new SuperordinateEventComponent(event, this::showUnsavedChangesPopup);
			superordinateEventComponent.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(superordinateEventComponent, SUPERORDINATE_EVENT_LOC);

			EventListComponent subordinateEventList = new EventListComponent(event.toReference(), this::showUnsavedChangesPopup, isEditAllowed);
			subordinateEventList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(subordinateEventList, SUBORDINATE_EVENTS_LOC);
		}

		boolean eventGroupsFeatureEnabled = UiUtil.enabled(FeatureType.EVENT_GROUPS);
		if (eventGroupsFeatureEnabled) {
			layout.addSidePanelComponent(
				new SideComponentLayout(new EventGroupListComponent(event.toReference(), this::showUnsavedChangesPopup)),
				EVENT_GROUPS_LOC);
		}

		boolean sormasToSormasEnabled = FacadeProvider.getSormasToSormasFacade().isAnyFeatureConfigured(FeatureType.SORMAS_TO_SORMAS_SHARE_EVENTS);
		if (sormasToSormasEnabled || event.getSormasToSormasOriginInfo() != null || event.isOwnershipHandedOver()) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(event);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addSidePanelComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		VerticalLayout shortcutLinksLayout = new VerticalLayout();
		shortcutLinksLayout.setMargin(false);
		shortcutLinksLayout.setSpacing(true);

		if (UiUtil.permitted(UserRight.CASE_VIEW)) {
			Button seeEventCasesBtn = ButtonHelper.createButton(
				"eventLinkToCases",
				I18nProperties.getCaption(Captions.eventLinkToCases),
				thisEvent -> ControllerProvider.getCaseController().navigateTo(new CaseCriteria().eventLike(getEventRef().getUuid())),
				ValoTheme.BUTTON_PRIMARY);
			shortcutLinksLayout.addComponent(seeEventCasesBtn);
		}

		if (UiUtil.permitted(UserRight.CONTACT_VIEW)) {
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

		final boolean deleted = FacadeProvider.getEventFacade().isDeleted(uuid);
		layout.disableIfNecessary(deleted, eventEditAllowed);
	}

	private void setExternalSurvToolLayoutVisibility(EventStatus eventStatus) {
		if (externalSurvToolLayout != null) {
			externalSurvToolLayout.setVisible(eventStatus == EventStatus.CLUSTER);
		}
	}
}
