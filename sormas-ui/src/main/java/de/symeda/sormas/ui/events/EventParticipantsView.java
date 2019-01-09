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

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventParticipantsView extends AbstractEventView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/eventparticipants";

	private EventParticipantsGrid grid;
	private Button addButton;
	private VerticalLayout gridLayout;

	public EventParticipantsView() {
		super(VIEW_NAME);

		setSizeFull();
		addStyleName("crud-view");

		grid = new EventParticipantsGrid();

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

	public HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth("100%");

		Label header = new Label(I18nProperties.getPrefixFieldCaption(EventDto.I18N_PREFIX, EventDto.EVENT_PERSONS));
		header.setSizeUndefined();
		CssStyles.style(header, CssStyles.H2, CssStyles.VSPACE_NONE);
		topLayout.addComponent(header);

		// Bulk operation dropdown
		if (CurrentUser.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			topLayout.setWidth(100, Unit.PERCENTAGE);

			MenuBar bulkOperationsDropdown = new MenuBar();	
			MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem("Bulk Actions", null);

			Command deleteCommand = selectedItem -> {
				ControllerProvider.getEventParticipantController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload(getEventRef());
					}
				});
			};
			bulkOperationsItem.addItem("Delete", FontAwesome.TRASH, deleteCommand);

			topLayout.addComponent(bulkOperationsDropdown);
			topLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
			topLayout.setExpandRatio(bulkOperationsDropdown, 1);
		}

		if (CurrentUser.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE)) {
			addButton = new Button("Add person");
			addButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			addButton.setIcon(FontAwesome.PLUS_CIRCLE);
			addButton.addClickListener(e -> {
				ControllerProvider.getEventParticipantController().createEventParticipant(this.getEventRef(),
						r -> grid.reload(getEventRef()));
			});
			topLayout.addComponent(addButton);
			topLayout.setComponentAlignment(addButton, Alignment.MIDDLE_RIGHT);
		}

		topLayout.addStyleName(CssStyles.VSPACE_3);
		return topLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if(event != null) {
			super.enter(event);
		}
		grid.reload(getEventRef());
	}

}
