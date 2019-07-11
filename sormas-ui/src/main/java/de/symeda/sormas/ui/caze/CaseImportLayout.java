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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.caze;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.Upload;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.importer.CaseImporter;
import de.symeda.sormas.ui.importer.ImportLayoutComponent;
import de.symeda.sormas.ui.importer.ImportUploader;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

@SuppressWarnings("serial")
public class CaseImportLayout extends VerticalLayout {

	private Button downloadErrorReportButton;
	private final UserReferenceDto currentUser;
	private final UI currentUI;

	public CaseImportLayout() {
		currentUser = UserProvider.getCurrent().getUserReference();
		currentUI = UI.getCurrent();
		setSpacing(false);

		// Step 1: Download SORMAS Import Guide
		String headline = I18nProperties.getString(Strings.headingDownloadImportGuide);
		String infoText = I18nProperties.getString(Strings.infoDownloadImportGuide);
		Resource buttonIcon = VaadinIcons.FILE_PRESENTATION;
		String buttonCaption = I18nProperties.getCaption(Captions.importDownloadImportGuide);
		ImportLayoutComponent importGuideComponent = new ImportLayoutComponent(1, headline, infoText, buttonIcon, buttonCaption);
		FileDownloader importGuideDownloader = new FileDownloader(new ClassResource("/SORMAS_Import_Guide.pdf"));
		importGuideDownloader.extend(importGuideComponent.getButton());
		addComponent(importGuideComponent);

		Button dataDictionaryButton = new Button(I18nProperties.getCaption(Captions.importDownloadDataDictionary), VaadinIcons.FILE_TABLE);
		CssStyles.style(dataDictionaryButton, ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_TOP_3);
		FileDownloader dataDictionaryDownloader = new FileDownloader(new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));
		dataDictionaryDownloader.extend(dataDictionaryButton);
		addComponent(dataDictionaryButton);
		CssStyles.style(dataDictionaryButton, CssStyles.VSPACE_2);

		// Step 2: Download case import template
		headline = I18nProperties.getString(Strings.headingDownloadCaseImportTemplate);
		infoText = I18nProperties.getString(Strings.infoDownloadCaseImportTemplate);
		buttonIcon = VaadinIcons.DOWNLOAD;
		buttonCaption = I18nProperties.getCaption(Captions.importDownloadCaseImportTemplate);
		ImportLayoutComponent importTemplateComponent = new ImportLayoutComponent(2, headline, infoText, buttonIcon, buttonCaption);
		String templateFilePath = FacadeProvider.getImportFacade().getCaseImportTemplateFilePath().toString();
		StreamResource templateResource = DownloadUtil.createFileStreamResource(templateFilePath, "sormas_import_case_template.csv", "text/csv",
				I18nProperties.getString(Strings.headingTemplateNotAvailable), I18nProperties.getString(Strings.messageTemplateNotAvailable));
		FileDownloader templateFileDownloader = new FileDownloader(templateResource);
		templateFileDownloader.extend(importTemplateComponent.getButton());
		CssStyles.style(importTemplateComponent, CssStyles.VSPACE_2);
		addComponent(importTemplateComponent);

		// Step 3: Upload .csv file
		headline = I18nProperties.getString(Strings.headingImportCsvFile);
		infoText = I18nProperties.getString(Strings.infoImportCsvFile);
		ImportLayoutComponent importCasesComponent = new ImportLayoutComponent(3, headline, infoText, null, null);
		addComponent(importCasesComponent);
		ImportUploader receiver = new ImportUploader("_case_import_", new Consumer<File>() {
			@Override
			public void accept(File file) {
				try {
					CaseImporter importer = new CaseImporter(file, downloadErrorReportButton, currentUser, currentUI);
					importer.startImport();
				} catch (IOException e) {
					new Notification(I18nProperties.getString(Strings.headingImportFailed), I18nProperties.getString(Strings.messageImportFailed), Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				}
			}
		});
		Upload upload = new Upload("", receiver);
		upload.setButtonCaption(I18nProperties.getCaption(Captions.importUploadCaseList));
		CssStyles.style(upload, CssStyles.VSPACE_2);
		upload.addSucceededListener(receiver);
		addComponent(upload);

		// Step 4: Download error report
		headline = I18nProperties.getString(Strings.headingDownloadErrorReport);
		infoText = I18nProperties.getString(Strings.infoDownloadErrorReport);
		buttonIcon = VaadinIcons.DOWNLOAD;
		buttonCaption = I18nProperties.getCaption(Captions.importDownloadErrorReport);
		ImportLayoutComponent errorReportComponent = new ImportLayoutComponent(4, headline, infoText, buttonIcon, buttonCaption);
		downloadErrorReportButton = errorReportComponent.getButton();
		errorReportComponent.getButton().setEnabled(false);
		addComponent(errorReportComponent);
	}

}
