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
package de.symeda.sormas.ui.contact;

import java.util.HashMap;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.visit.VisitGrid;

public class ContactVisitsView extends AbstractContactView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/visits";

	private VisitGrid grid;    
	private Button newButton;
	private VerticalLayout gridLayout;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	public ContactVisitsView() {
		super(VIEW_NAME);

		setSizeFull();

		grid = new VisitGrid();

		gridLayout = new VerticalLayout();
		gridLayout.setSizeFull();
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);

		gridLayout.addComponent(createTopBar());
		gridLayout.addComponent(grid);
		gridLayout.setExpandRatio(grid, 1);

		grid.getContainer().addItemSetChangeListener(e -> {
			updateActiveStatusButtonCaption();
		});

		setSubComponent(gridLayout);
	}

	public HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth(100, Unit.PERCENTAGE);

		statusButtons = new HashMap<>();

		Button contactButton = new Button("Contact related", e -> {
			grid.reload(getContactRef());
			processStatusChangeVisuals(e.getButton());
		});
		CssStyles.style(contactButton, ValoTheme.BUTTON_LINK, CssStyles.LINK_HIGHLIGHTED);
		contactButton.setCaptionAsHtml(true);
		topLayout.addComponent(contactButton);
		statusButtons.put(contactButton, "Contact related");

		Button personButton = new Button("All visits of contact person", e -> {
			ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(getContactRef().getUuid());
			grid.reload(contact.getPerson());
			processStatusChangeVisuals(e.getButton());
		});
		CssStyles.style(personButton, ValoTheme.BUTTON_LINK, CssStyles.LINK_HIGHLIGHTED, CssStyles.LINK_HIGHLIGHTED_LIGHT);
		personButton.setCaptionAsHtml(true);
		topLayout.addComponent(personButton);
		statusButtons.put(personButton, "All visits of contact person");

		topLayout.setExpandRatio(topLayout.getComponent(topLayout.getComponentCount()-1), 1);

		// Bulk operation dropdown
		if (LoginHelper.hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			topLayout.setWidth(100, Unit.PERCENTAGE);

			MenuBar bulkOperationsDropdown = new MenuBar();	
			MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem("Bulk Actions", null);

			Command deleteCommand = selectedItem -> {
				ControllerProvider.getVisitController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload(getContactRef());
					}
				});
			};
			bulkOperationsItem.addItem("Delete", FontAwesome.TRASH, deleteCommand);

			topLayout.addComponent(bulkOperationsDropdown);
			topLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
			topLayout.setExpandRatio(bulkOperationsDropdown, 1);
		}

		if (LoginHelper.hasUserRight(UserRight.VISIT_CREATE)) {
			newButton = new Button("New visit");
			newButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			newButton.setIcon(FontAwesome.PLUS_CIRCLE);
			newButton.addClickListener(e -> {
				ControllerProvider.getVisitController().createVisit(this.getContactRef(), 
						r -> grid.reload(getContactRef()));
			});
			topLayout.addComponent(newButton);
			topLayout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);
		}

		topLayout.addStyleName(CssStyles.VSPACE_3);
		activeStatusButton = contactButton;
		return topLayout;
	}


	private void updateActiveStatusButtonCaption() {
		if (activeStatusButton != null) {
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getContainer().size())));
		}
	}

	private void processStatusChangeVisuals(Button button) {
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.LINK_HIGHLIGHTED_LIGHT);
			b.setCaption(statusButtons.get(b));
		});
		CssStyles.removeStyles(button, CssStyles.LINK_HIGHLIGHTED_LIGHT);
		activeStatusButton = button;
		updateActiveStatusButtonCaption();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		
		// Hide the "New visit" button for converted contacts
		if (FacadeProvider.getContactFacade().getContactByUuid(getContactRef().getUuid()).getContactStatus() == ContactStatus.CONVERTED) {
			newButton.setVisible(false);
		}
		
		grid.reload(getContactRef());
		updateActiveStatusButtonCaption();
	}
}
