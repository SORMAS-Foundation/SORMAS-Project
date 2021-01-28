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

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;

@SuppressWarnings("serial")
public class ExportConfigurationsLayout extends VerticalLayout {

	private Label lblDescription;
	private Button btnNewExportConfiguration;
	private Button btnExport;
	private ExportConfigurationsGrid grid;
	private ExportConfigurationsGrid gridPublicCustomConfiguration;

	public ExportConfigurationsLayout(
		ExportType exportType,
		List<DataHelper.Pair<String, ExportGroupType>> availableProperties,
		Function<String, String> propertyCaptionProvider,
		Runnable closeCallback) {

		lblDescription = new Label(I18nProperties.getString(Strings.infoCustomExport));
		lblDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblDescription);

		btnNewExportConfiguration = ButtonHelper.createIconButton(Captions.exportNewExportConfiguration, VaadinIcons.PLUS, e -> {
			ExportConfigurationDto newConfig = ExportConfigurationDto.build(UserProvider.getCurrent().getUserReference(), exportType);
			ControllerProvider.getCustomExportController()
				.openEditExportConfigurationWindow(grid, newConfig, availableProperties, propertyCaptionProvider);
		}, ValoTheme.BUTTON_PRIMARY);
		addComponent(btnNewExportConfiguration);
		setComponentAlignment(btnNewExportConfiguration, Alignment.MIDDLE_RIGHT);

		grid = new ExportConfigurationsGrid(exportType, availableProperties, propertyCaptionProvider);
		grid.setWidth(100, Unit.PERCENTAGE);
		addComponent(grid);

		gridPublicCustomConfiguration = new ExportConfigurationsGrid(exportType, availableProperties, propertyCaptionProvider, true);
		if (gridPublicCustomConfiguration.getPublicExportCustomConfigurationRecords() > 0 ) {
			gridPublicCustomConfiguration.setWidth(100, Unit.PERCENTAGE);
			addComponent(gridPublicCustomConfiguration);
		}

		Button btnClose = ButtonHelper.createButton(Captions.actionClose, e -> closeCallback.run());
		addComponent(btnClose);
		setComponentAlignment(btnClose, Alignment.MIDDLE_RIGHT);
	}

	public Button getExportButton() {
		return btnExport;
	}

	public void setExportCallback(Consumer<ExportConfigurationDto> exportCallback) {
		grid.setExportCallback(exportCallback);
	}

	public void setExportCallbackPublicCustomConfiguration(Consumer<ExportConfigurationDto> exportCallbackPublicCustomConfiguration) {
		gridPublicCustomConfiguration.setExportCallbackPublicExportCustomConfiguration(exportCallbackPublicCustomConfiguration);
	}
}
