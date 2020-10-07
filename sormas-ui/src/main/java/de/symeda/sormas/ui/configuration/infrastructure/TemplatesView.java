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
package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class TemplatesView extends AbstractConfigurationView {

	private static final long serialVersionUID = -4759099406008618416L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/templates";

	private ViewConfiguration viewConfiguration;

	private VerticalLayout gridLayout;
	private Grid<String> Tgrid;
	protected Button createButton;
	protected Button importButton;

	private MenuBar bulkOperationsDropdown;

	public TemplatesView() {

		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(TemplatesView.class).get(ViewConfiguration.class);

		Tgrid = new Grid<>(String.class);
		gridLayout = new VerticalLayout();
		ListDataProvider<String> dataProvider = DataProvider.fromStream(FacadeProvider.getQuarantineOrderFacade().getAvailableTemplates().stream());
		Tgrid.setDataProvider(dataProvider);
		Tgrid.removeAllColumns();
		Tgrid.addColumn(x -> x.toString()).setCaption("Document Title");
		Tgrid.addItemClickListener(elem -> {
			VaadinUiUtil.showDeleteConfirmationWindow("Permanently delete this Template? (i18n required)", () -> {
				FacadeProvider.getQuarantineOrderFacade().deleteQuarantineTemplate(elem.getItem().toString());
				Tgrid.getDataProvider().refreshAll();
			});
		});
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(Tgrid);

		gridLayout.setWidth(100, Unit.PERCENTAGE);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(Tgrid, 1);
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_IMPORT)) {
			importButton = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new TemplateImportLayout());
				window.setCaption("i18n string");
				window.addCloseListener(c -> {
					Tgrid.getDataProvider().refreshAll();
				});
			}, ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(importButton);
		}

		addComponent(gridLayout);
	}

}
