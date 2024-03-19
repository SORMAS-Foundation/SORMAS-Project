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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventGroupDto;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.events.groups.EventGroupMemberListComponent;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class EventGroupDataView extends AbstractDetailView<EventGroupReferenceDto> {

	private static final long serialVersionUID = -1L;

	public static final String ROOT_VIEW_NAME = "events";
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/groups/data";

	public static final String EVENT_GROUP_LOC = "event-group";
	public static final String EVENTS_LOC = "events";

	private CommitDiscardWrapperComponent<?> editComponent;

	public EventGroupDataView() {
		super(VIEW_NAME);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();

		String htmlLayout = fluidRow(
			fluidColumnLoc(8, 0, 12, 0, EVENT_GROUP_LOC),
			fluidColumnLoc(8, 0, 12, 0, EVENTS_LOC));

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

		editComponent = ControllerProvider.getEventGroupController().getEventGroupEditComponent(getReference().getUuid());
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, EVENT_GROUP_LOC);

		EventGroupMemberListComponent eventGroupMemberListComponent = new EventGroupMemberListComponent(getReference());
		layout.addComponent(eventGroupMemberListComponent, EVENTS_LOC);
		CssStyles.style(eventGroupMemberListComponent, CssStyles.VSPACE_TOP_2);

		if (!UiUtil.permitted(UserRight.EVENTGROUP_EDIT)) {
			layout.getComponent(EVENTS_LOC).setEnabled(false);
		}
	}

	@Override
	protected EventGroupReferenceDto getReferenceByUuid(String uuid) {

		final EventGroupReferenceDto reference;
		if (FacadeProvider.getEventGroupFacade().exists(uuid)) {
			reference = FacadeProvider.getEventGroupFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return ROOT_VIEW_NAME;
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {

		if (!findReferenceByParams(params)) {
			return;
		}

		menu.removeAllViews();
		menu.addView(EventsView.VIEW_NAME, I18nProperties.getCaption(Captions.eventEventsList));
		menu.addView(VIEW_NAME, I18nProperties.getCaption(EventGroupDto.I18N_PREFIX), params);

		setMainHeaderComponent(ControllerProvider.getEventGroupController().getEventGroupViewTitleLayout(getReference().getUuid()));
	}
}
