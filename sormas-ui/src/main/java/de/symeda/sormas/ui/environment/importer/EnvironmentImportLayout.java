/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.environment.importer;

import java.io.IOException;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.ImportReceiver;

public class EnvironmentImportLayout extends AbstractImportLayout {

	private static final long serialVersionUID = 7909449323455621050L;

	public EnvironmentImportLayout() {

		super();

		ImportFacade importFacade = FacadeProvider.getImportFacade();

		addDownloadResourcesComponent(1, new ClassResource("/SORMAS_Environment_Import_Guide.pdf"));
		addDownloadImportTemplateComponent(
			2,
			importFacade.getEnvironmentImportTemplateFilePath(),
			importFacade.getEnvironmentImportTemplateFileName());
		addImportCsvComponent(3, new ImportReceiver("_environment_import_", file -> {
			resetDownloadErrorReportButton();

			try {
				EnvironmentImporter importer = new EnvironmentImporter(file, true, currentUser, (ValueSeparator) separator.getValue());
				importer.startImport(EnvironmentImportLayout.this::extendDownloadErrorReportButton, currentUI, true);
			} catch (IOException | CsvValidationException e) {
				new Notification(
					I18nProperties.getString(Strings.headingImportFailed),
					I18nProperties.getString(Strings.messageImportFailed),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		}));
		addDownloadErrorReportComponent(4);
	}
}
