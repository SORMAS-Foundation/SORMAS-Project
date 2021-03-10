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

package de.symeda.sormas.ui.customexport;

import static de.symeda.sormas.ui.utils.CssStyles.H3;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportPropertyMetaInfo;
import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;

@SuppressWarnings("serial")
public class ExportConfigurationsLayout extends VerticalLayout {

	private Label lblDescription;
	private Button btnNewExportConfiguration;
	private ExportConfigurationsGrid grid;
	private ExportConfigurationsGrid gridSharedExportsToPublic;

	public ExportConfigurationsLayout(
		ExportType exportType,
		List<ExportPropertyMetaInfo> availableProperties,
		Runnable closeCallback) {

		lblDescription = new Label(I18nProperties.getString(Strings.infoCustomExport));
		lblDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblDescription);

		btnNewExportConfiguration = ButtonHelper.createIconButton(Captions.exportNewExportConfiguration, VaadinIcons.PLUS, e -> {
			ExportConfigurationDto newConfig = ExportConfigurationDto.build(UserProvider.getCurrent().getUserReference(), exportType);
			ControllerProvider.getCustomExportController()
				.openEditExportConfigurationWindow(grid, newConfig, availableProperties);
		}, ValoTheme.BUTTON_PRIMARY);
		addComponent(btnNewExportConfiguration);
		setComponentAlignment(btnNewExportConfiguration, Alignment.MIDDLE_RIGHT);

		Label myExportsLabel = new Label(I18nProperties.getPrefixCaption(ExportConfigurationDto.I18N_PREFIX, Captions.ExportConfiguration_myExports));
		myExportsLabel.addStyleName(H3);
		addComponent(myExportsLabel);

		grid = new ExportConfigurationsGrid(exportType, availableProperties, false);
		grid.setWidth(100, Unit.PERCENTAGE);
		addComponent(grid);

		Label sharedExportsLabel =
			new Label(I18nProperties.getPrefixCaption(ExportConfigurationDto.I18N_PREFIX, Captions.ExportConfiguration_sharedExports));
		sharedExportsLabel.addStyleName(H3);

		gridSharedExportsToPublic = new ExportConfigurationsGrid(exportType, availableProperties, true);
		if (gridSharedExportsToPublic.getNbOfSharedExportsToPublic() > 0) {
			gridSharedExportsToPublic.setWidth(100, Unit.PERCENTAGE);
			addComponent(sharedExportsLabel);
			addComponent(gridSharedExportsToPublic);
		}

		Button btnClose = ButtonHelper.createButton(Captions.actionClose, e -> closeCallback.run());
		addComponent(btnClose);
		setComponentAlignment(btnClose, Alignment.MIDDLE_RIGHT);
	}

	public void setExportCallback(Consumer<ExportConfigurationDto> exportCallback) {
		grid.setExportCallback(exportCallback);
		gridSharedExportsToPublic.setExportCallback(exportCallback);
	}

}
