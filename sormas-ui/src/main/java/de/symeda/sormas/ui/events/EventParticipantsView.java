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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.MenuBarHelper;

public class EventParticipantsView extends AbstractEventView {

	private static final long serialVersionUID = -1L;

	public static final String EVENTPARTICIPANTS = "eventparticipants";
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/" + EVENTPARTICIPANTS;

	private final EventParticipantCriteria criteria;

	private EventParticipantsGrid grid;
	private Button addButton;
	private VerticalLayout gridLayout;
	private Button activeStatusButton;
	private EventParticipantsFilterForm filterForm;

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

		filterForm = new EventParticipantsFilterForm();
		filterForm.addValueChangeListener(e -> {
			navigateTo(criteria);
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(EventParticipantsView.class).remove(EventParticipantCriteria.class);
			navigateTo(null);
		});

		topLayout.addComponent(filterForm);

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
		}

		topLayout.addStyleName(CssStyles.VSPACE_3);
		return topLayout;
	}

	@Override
	protected void initView(String params) {

		criteria.event(getEventRef());

		if (grid == null) {
			grid = new EventParticipantsGrid(criteria);
			gridLayout = new VerticalLayout();
			gridLayout.setSizeFull();
			gridLayout.setMargin(true);
			gridLayout.setSpacing(false);
			gridLayout.addComponent(createTopBar());
			gridLayout.addComponent(createStatusFilterBar());
			gridLayout.addComponent(grid);
			gridLayout.setExpandRatio(grid, 1);
			gridLayout.setStyleName("crud-main-layout");
			grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());
			setSubComponent(gridLayout);
		}

		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();

		grid.reload();
	}

	public HorizontalLayout createStatusFilterBar() {

		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		Button statusAll = ButtonHelper.createButton(Captions.all, e -> {
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);
		statusFilterLayout.addComponent(statusAll);
		activeStatusButton = statusAll;

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE)) {
			addButton = ButtonHelper.createIconButton(Captions.eventParticipantAddPerson, VaadinIcons.PLUS_CIRCLE, e -> {
				ControllerProvider.getEventParticipantController().createEventParticipant(this.getEventRef(), r -> navigateTo(criteria));
			}, ValoTheme.BUTTON_PRIMARY);

			statusFilterLayout.addComponent(addButton);
			statusFilterLayout.setComponentAlignment(addButton, Alignment.MIDDLE_RIGHT);
		}

		return statusFilterLayout;
	}

	public void updateFilterComponents() {

		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		updateStatusButtons();

		filterForm.setValue(criteria);

		applyingCriteria = false;
	}

	private void updateStatusButtons() {

		if (activeStatusButton != null) {
			activeStatusButton
				.setCaption(I18nProperties.getCaption(Captions.all) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}
	}
}
