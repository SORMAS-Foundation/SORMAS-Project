/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.climate;

import java.io.IOException;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportReceiver;

/**
 * The import window for daily climate values from a met agency: the rules, the template, the upload with the overwrite
 * choice, and the error report. Opened from the Climate data screen.
 */
public class ClimateImportLayout extends AbstractImportLayout {

	private static final long serialVersionUID = 2958046418722410652L;

	public ClimateImportLayout() {

		super();

		Label rules = new Label(I18nProperties.getString(Strings.infoClimateImport));
		rules.setWidthFull();
		addComponent(rules);

		ImportFacade importFacade = FacadeProvider.getImportFacade();
		addDownloadImportTemplateComponent(
			1,
			importFacade.getClimateObservationImportTemplateFilePath(),
			importFacade.getClimateObservationImportTemplateFileName());

		addImportCsvComponentWithOverwrite(2, overwriteImports -> new ImportReceiver("_climate_observation_import_", file -> {
			resetDownloadErrorReportButton();

			try {
				DataImporter importer = new ClimateObservationImporter(file, currentUser, (ValueSeparator) separator.getValue(), overwriteImports);
				importer.startImport(this::extendDownloadErrorReportButton, currentUI, false);
			} catch (IOException | CsvValidationException e) {
				new Notification(
					I18nProperties.getString(Strings.headingImportFailed),
					I18nProperties.getString(Strings.messageImportFailed),
					Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		}));

		addDownloadErrorReportComponent(3);
	}
}
