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
import com.vaadin.server.Page;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationCriteria;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;

@SuppressWarnings("serial")
public class ExportConfigurationsGrid extends Grid<ExportConfigurationDto> {

	public static final String COLUMN_ACTIONS = "actions";
	public static final String COLUMN_ACTIONS_PUBLIC = "actionsPublic";

	private Consumer<ExportConfigurationDto> exportCallback;

	private long nbOfSharedExportsToPublic;

	private final ExportType exportType;

	public ExportConfigurationsGrid(
		ExportType exportType,
		List<DataHelper.Pair<String, ExportGroupType>> availableProperties,
		Function<String, String> propertyCaptionProvider,
		Boolean isPublicExport) {
		this.exportType = exportType;

		buildGrid(availableProperties, propertyCaptionProvider, isPublicExport);
		if (isPublicExport) {
			reloadPublicExportCustomConfiguration();
		} else {
			reload();
		}
	}

	private void buildGrid(
		List<DataHelper.Pair<String, ExportGroupType>> availableProperties,
		Function<String, String> propertyCaptionProvider,
		Boolean isPublicExport) {

		setSelectionMode(SelectionMode.NONE);
		setHeightMode(HeightMode.ROW);

		addColumn(ExportConfigurationDto::getName)
			.setCaption(I18nProperties.getPrefixCaption(ExportConfigurationDto.I18N_PREFIX, ExportConfigurationDto.NAME))
			.setExpandRatio(1);

		addComponentColumn((config) -> this.buildButtonLayout(config, availableProperties, propertyCaptionProvider, !isPublicExport))
			.setId(isPublicExport ? COLUMN_ACTIONS_PUBLIC : COLUMN_ACTIONS)
			.setCaption("");
	}

	public void reload() {
		List<ExportConfigurationDto> configs =
			FacadeProvider.getExportFacade().getExportConfigurations(new ExportConfigurationCriteria().exportType(exportType), false);
		setItems(configs);
		setHeightByRows(configs.size() > 0 ? (Math.min(configs.size(), 10)) : 1);
	}

	public void reloadPublicExportCustomConfiguration() {
		List<ExportConfigurationDto> sharedConfigs =
			FacadeProvider.getExportFacade().getExportConfigurations(new ExportConfigurationCriteria().exportType(exportType), true);
		setItems(sharedConfigs);
		setHeightByRows(sharedConfigs.size() > 0 ? (Math.min(sharedConfigs.size(), 10)) : 1);
		setNbOfSharedExportsToPublic(sharedConfigs.size());
	}

	private HorizontalLayout buildButtonLayout(
		ExportConfigurationDto config,
		List<DataHelper.Pair<String, ExportGroupType>> availableProperties,
		Function<String, String> propertyCaptionProvider,
		boolean canEditOrDelete) {

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);

		Button btnExport;
		btnExport = ButtonHelper.createIconButtonWithCaption(
			config.getUuid() + "-download",
			null,
			VaadinIcons.DOWNLOAD,
			e -> exportCallback.accept(config),
			ValoTheme.BUTTON_PRIMARY);
		layout.addComponent(btnExport);

		Button btnEdit = ButtonHelper.createIconButtonWithCaption(config.getUuid() + "-edit", null, VaadinIcons.EDIT, e -> {
			ControllerProvider.getCustomExportController()
				.openEditExportConfigurationWindow(this, config, availableProperties, propertyCaptionProvider);
		});
		btnEdit.setEnabled(canEditOrDelete);
		layout.addComponent(btnEdit);

		Button btnDelete = ButtonHelper.createIconButtonWithCaption(config.getUuid() + "-delete", null, VaadinIcons.TRASH, e -> {
			FacadeProvider.getExportFacade().deleteExportConfiguration(config.getUuid());
			new Notification(null, I18nProperties.getString(Strings.messageExportConfigurationDeleted), Type.WARNING_MESSAGE, false)
				.show(Page.getCurrent());
			reload();
		});
		btnDelete.setEnabled(canEditOrDelete);
		layout.addComponent(btnDelete);

		return layout;
	}

	public void setExportCallback(Consumer<ExportConfigurationDto> exportCallback) {
		this.exportCallback = exportCallback;
	}

	public long getNbOfSharedExportsToPublic() {
		return nbOfSharedExportsToPublic;
	}

	public void setNbOfSharedExportsToPublic(int nbOfSharedExportsToPublic) {
		this.nbOfSharedExportsToPublic = nbOfSharedExportsToPublic;
	}
}
