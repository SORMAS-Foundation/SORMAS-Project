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
package de.symeda.sormas.ui.caze.importer;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.ImportReceiver;

@SuppressWarnings("serial")
public class CaseImportLayout extends AbstractImportLayout {

	public CaseImportLayout() {

		super();

		addDownloadResourcesComponent(1, new ClassResource("/SORMAS_Import_Guide.pdf"), new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));
		addDownloadImportTemplateComponent(
			2,
			FacadeProvider.getImportFacade().getCaseImportTemplateFilePath().toString(),
			"sormas_import_case_template.csv");
		addImportCsvComponent(3, new ImportReceiver("_case_import_", new Consumer<File>() {

			@Override
			public void accept(File file) {
				resetDownloadErrorReportButton();

				try {
					CaseImporter importer = new CaseImporter(file, true, currentUser);
					importer.startImport(new Consumer<StreamResource>() {

						@Override
						public void accept(StreamResource resource) {
							extendDownloadErrorReportButton(resource);
						}
					}, currentUI, true);
				} catch (IOException e) {
					new Notification(
						I18nProperties.getString(Strings.headingImportFailed),
						I18nProperties.getString(Strings.messageImportFailed),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}
		}));
		addDownloadErrorReportComponent(4);
	}
}
