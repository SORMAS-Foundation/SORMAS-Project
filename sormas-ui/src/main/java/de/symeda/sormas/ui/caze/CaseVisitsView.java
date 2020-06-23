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
package de.symeda.sormas.ui.caze;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.visit.VisitGrid;

public class CaseVisitsView extends AbstractCaseView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/visits";
	private static final long serialVersionUID = -4715387348091488461L;
	private VisitCriteria criteria;

	private VisitGrid grid;
	private Button newButton;
	private VerticalLayout gridLayout;

	public CaseVisitsView() {
		super(VIEW_NAME, false);
		setSizeFull();

		criteria = ViewModelProviders.of(CaseVisitsView.class).get(VisitCriteria.class);
	}

	public HorizontalLayout createTopBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.addStyleName(CssStyles.VSPACE_3);

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			topLayout.setWidth(100, Unit.PERCENTAGE);
			MenuBar bulkOperationsDropdown = MenuBarHelper.createDropDown(
				Captions.bulkActions,
				new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, selectedItem -> {
					ControllerProvider.getVisitController().deleteAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
				}));
			topLayout.addComponent(bulkOperationsDropdown);
			topLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
			topLayout.setExpandRatio(bulkOperationsDropdown, 1);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.VISIT_EXPORT)) {
			// TODO (xca): handle exports
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.VISIT_CREATE)) {
			newButton = ButtonHelper.createIconButton(Captions.visitNewVisit, VaadinIcons.PLUS_CIRCLE, e -> {
				ControllerProvider.getVisitController().createVisit(this.getCaseRef(), r -> navigateTo(criteria));
			}, ValoTheme.BUTTON_PRIMARY);

			topLayout.addComponent(newButton);
			topLayout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);
		}

		return topLayout;
	}

	@Override
	protected void initView(String params) {
		criteria.caze(getCaseRef());

		if (grid == null) {
			grid = new VisitGrid(criteria);
			gridLayout = new VerticalLayout();
			gridLayout.setSizeFull();
			gridLayout.setMargin(true);
			gridLayout.setSpacing(false);
			gridLayout.addComponent(createTopBar());
			gridLayout.addComponent(grid);
			gridLayout.setExpandRatio(grid, 1);
			setSubComponent(gridLayout);
		}

		grid.reload();

		setCaseEditPermission(gridLayout);
	}
}
