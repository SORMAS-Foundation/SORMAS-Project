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

package de.symeda.sormas.ui.travelentry.importer;

import static com.vaadin.ui.Notification.Type;

import java.io.IOException;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.ImportReceiver;

public class TravelEntryImportLayout extends AbstractImportLayout {

	private static final long serialVersionUID = 4034832956498126408L;

	public TravelEntryImportLayout() {

		super();

		addDownloadResourcesComponent(1, new ClassResource("/SORMAS_Travel_Entry_Import_Guide.pdf"));
		addImportCsvComponent(2, new ImportReceiver("_travel_entry_import_", file -> {

			resetDownloadErrorReportButton();
			try {
				TravelEntryImporter importer = new TravelEntryImporter(file, false, currentUser, (ValueSeparator) separator.getValue());
				importer.startImport(this::extendDownloadErrorReportButton, currentUI, true);
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
