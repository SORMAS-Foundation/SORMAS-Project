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

import static de.symeda.sormas.ui.utils.CssStyles.H3;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

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
	private QuarantineTemplatesGrid Qgrid;
	protected Button uploadButton;

	private MenuBar bulkOperationsDropdown;

	public TemplatesView() {

		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(TemplatesView.class).get(ViewConfiguration.class);

		gridLayout = new VerticalLayout();

		// Add Quarantine Template Section
		Qgrid = new QuarantineTemplatesGrid();
		Qgrid.setWidth("500px"); // Remove this line to fit whole page
		Label QuarantineTemplatesLabel = new Label("Quarantine Templates (i18n required)");
		QuarantineTemplatesLabel.addStyleName(H3);
		gridLayout.addComponent(QuarantineTemplatesLabel);
		gridLayout.addComponent(Qgrid);
		Label emptyRow = new Label(" ");
		emptyRow.setHeight("10px");
		gridLayout.addComponent(emptyRow); // add some spacing
		gridLayout.addComponent(buildUploadButton());

		gridLayout.setWidth(100, Unit.PERCENTAGE);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(Qgrid, 1); // Ensure that everything stays at the top
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");

		// maybe use another permission here
		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_IMPORT)) {
			uploadButton = ButtonHelper.createIconButton("i18n upload", VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new TemplateUploadLayout());
				window.setCaption("i18n string");
				window.addCloseListener(c -> {
					Qgrid.reload();
				});
			}, ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(uploadButton);
		}

		addComponent(gridLayout);
	}

	Button buildUploadButton() {
		Button newBut = ButtonHelper.createIconButton("i18n upload", VaadinIcons.UPLOAD, e -> {
			Window window = VaadinUiUtil.showPopupWindow(new TemplateUploadLayout());
			window.setCaption("i18n string");
			window.addCloseListener(c -> {
				Qgrid.reload();
			});
		}, ValoTheme.BUTTON_PRIMARY);
		return newBut;
	}

}
