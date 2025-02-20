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

package de.symeda.sormas.ui.configuration.infrastructure;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.survey.SurveyTokenFacade;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportReceiver;
import de.symeda.sormas.ui.importer.SurveyTokenImporter;

import java.io.IOException;

public class ImportSurveyTokensLayout extends AbstractImportLayout {


	public ImportSurveyTokensLayout(SurveyDto survey) {
		super();
		SurveyTokenFacade surveyTokenFacade = FacadeProvider.getSurveyTokenFacade();
		String templateFilePath = surveyTokenFacade.getSurveyTokensImportTemplateFilePath();
		String templateFileName = surveyTokenFacade.getSurveyTokensImportTemplateFileName();
		String fileNameAddition = "_survey_tokens_import_";

		addDownloadResourcesComponent(1, new ClassResource("/SORMAS_Infrastructure_Import_Guide.pdf"));
		addDownloadImportTemplateComponent(2, templateFilePath, templateFileName);

		addImportCsvComponent(3, new ImportReceiver(fileNameAddition, file -> {
			resetDownloadErrorReportButton();
			try {
				DataImporter importer = new SurveyTokenImporter(
						file,
						currentUser,
						survey,
						(ValueSeparator) separator.getValue());
				importer.startImport(this::extendDownloadErrorReportButton, currentUI, false);
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
