/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.action.ActionStatsComponent;
import de.symeda.sormas.ui.document.DocumentListComponent;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class EventDataView extends AbstractEventView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String EVENT_LOC = "event";
	public static final String TASKS_LOC = "tasks";
	public static final String ACTIONS_LOC = "actions";
	public static final String CASES_LINK_LOC = "cases";
	public static final String CONTACTS_LINK_LOC = "contacts";
	public static final String DOCUMENTS_LOC = "documents";

	private CommitDiscardWrapperComponent<?> editComponent;

	public EventDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();

		String htmlLayout = LayoutUtil.fluidRow(
			LayoutUtil.fluidColumnLoc(8, 0, 12, 0, EVENT_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, TASKS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, ACTIONS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 12, 0, DOCUMENTS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASES_LINK_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CONTACTS_LINK_LOC));

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		editComponent = ControllerProvider.getEventController().getEventDataEditComponent(getEventRef().getUuid());
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, EVENT_LOC);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TASK_MANAGEMENT)) {
			TaskListComponent taskList = new TaskListComponent(TaskContext.EVENT, getEventRef());
			taskList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addComponent(taskList, TASKS_LOC);
		}

		ActionStatsComponent actionList = new ActionStatsComponent(ActionContext.EVENT, getEventRef());
		actionList.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addComponent(actionList, ACTIONS_LOC);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.DOCUMENTS)) {
			// TODO: user rights?
			DocumentListComponent documentList = new DocumentListComponent(DocumentRelatedEntityType.EVENT, getEventRef(), UserRight.EVENT_EDIT);
			documentList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addComponent(documentList, DOCUMENTS_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_VIEW)) {
			layout.addComponent(
				ButtonHelper.createButtonWithCaption(
					"eventLinkToCases",
					I18nProperties.getCaption(Captions.eventLinkToCases),
					event -> ControllerProvider.getCaseController().navigateTo(new CaseCriteria().eventLike(getEventRef().getUuid())),
					ValoTheme.BUTTON_PRIMARY),
				CASES_LINK_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			layout.addComponent(
				ButtonHelper.createButtonWithCaption(
					"eventLinkToContacts",
					I18nProperties.getCaption(Captions.eventLinkToContacts),
					event -> ControllerProvider.getContactController().navigateTo(new ContactCriteria().eventUuid(getEventRef().getUuid())),
					ValoTheme.BUTTON_PRIMARY,
					CssStyles.VSPACE_TOP_2),
				CONTACTS_LINK_LOC);
		}

		setEventEditPermission(container);
	}
}
