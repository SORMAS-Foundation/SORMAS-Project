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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.MenuBarHelper;

public class EventParticipantsView extends AbstractEventView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/eventparticipants";

	private EventParticipantCriteria criteria;

	private EventParticipantsGrid grid;
	private Button addButton;
	private VerticalLayout gridLayout;

	public EventParticipantsView() {
		super(VIEW_NAME);

		setSizeFull();
		addStyleName("crud-view");

		criteria = ViewModelProviders.of(EventParticipantsView.class).get(EventParticipantCriteria.class);
	}

	public HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth("100%");

		Label header = new Label(I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.EVENT_PERSONS));
		header.setSizeUndefined();
		CssStyles.style(header, CssStyles.H2, CssStyles.VSPACE_NONE);
		topLayout.addComponent(header);

		// Bulk operation dropdown
		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			topLayout.setWidth(100, Unit.PERCENTAGE);

			MenuBar bulkOperationsDropdown = MenuBarHelper.createDropDown(
				Captions.bulkActions,
				new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, selectedItem -> {
					ControllerProvider.getEventParticipantController()
						.deleteAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
				}));

			topLayout.addComponent(bulkOperationsDropdown);
			topLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
			topLayout.setExpandRatio(bulkOperationsDropdown, 1);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE)) {
			addButton = ButtonHelper.createIconButton(Captions.eventParticipantAddPerson, VaadinIcons.PLUS_CIRCLE, e -> {
				ControllerProvider.getEventParticipantController().createEventParticipant(this.getEventRef(), r -> navigateTo(criteria));
			}, ValoTheme.BUTTON_PRIMARY);

			topLayout.addComponent(addButton);
			topLayout.setComponentAlignment(addButton, Alignment.MIDDLE_RIGHT);
		}

		topLayout.addStyleName(CssStyles.VSPACE_3);
		return topLayout;
	}

	@Override
	public void initView(String params) {

		criteria.event(getEventRef());

		if (grid == null) {
			grid = new EventParticipantsGrid(criteria);
			gridLayout = new VerticalLayout();
			gridLayout.setSizeFull();
			gridLayout.setMargin(true);
			gridLayout.setSpacing(false);
			gridLayout.addComponent(createTopBar());
			gridLayout.addComponent(grid);
			gridLayout.setExpandRatio(grid, 1);
			gridLayout.setStyleName("crud-main-layout");
			setSubComponent(gridLayout);
		}

		grid.reload();
	}
}
