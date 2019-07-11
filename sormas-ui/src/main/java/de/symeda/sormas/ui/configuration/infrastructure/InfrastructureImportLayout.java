package de.symeda.sormas.ui.configuration.infrastructure;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.Upload;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.importer.ImportLayoutComponent;
import de.symeda.sormas.ui.importer.ImportUploader;
import de.symeda.sormas.ui.importer.PointOfEntryImporter;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

@SuppressWarnings("serial")
public class InfrastructureImportLayout extends VerticalLayout {

	private Button downloadErrorReportButton;
	private final UserReferenceDto currentUser;
	private final UI currentUI;

	public InfrastructureImportLayout(InfrastructureType infrastructureType) {
		currentUser = UserProvider.getCurrent().getUserReference();
		currentUI = UI.getCurrent();
		setSpacing(false);
		setMargin(true);

		// Step 1: Download import template
		String headline = I18nProperties.getString(Strings.headingDownloadImportTemplate);
		String infoText = I18nProperties.getString(Strings.infoDownloadImportTemplate);
		Resource buttonIcon = VaadinIcons.DOWNLOAD;
		String buttonCaption = I18nProperties.getCaption(Captions.importDownloadImportTemplate);
		ImportLayoutComponent importTemplateComponent = new ImportLayoutComponent(1, headline, infoText, buttonIcon, buttonCaption);
		String templateFilePath = null;
		String templateFileName = null;
		switch (infrastructureType) {
		case POINT_OF_ENTRY:
			templateFilePath = FacadeProvider.getImportFacade().getPointOfEntryImportTemplateFilePath().toString();
			templateFileName = "sormas_import_point_of_entry_template.csv";
			break;
		default:
			throw new UnsupportedOperationException("Import is currently only implemented for points of entry");
		}
		StreamResource templateResource = DownloadUtil.createFileStreamResource(templateFilePath, templateFileName, "text/csv", 
				I18nProperties.getString(Strings.headingTemplateNotAvailable), I18nProperties.getString(Strings.messageTemplateNotAvailable));
		FileDownloader templateFileDownloader = new FileDownloader(templateResource);
		templateFileDownloader.extend(importTemplateComponent.getButton());
		CssStyles.style(importTemplateComponent, CssStyles.VSPACE_2);
		addComponent(importTemplateComponent);

		// Step 2: Upload .csv file
		headline = I18nProperties.getString(Strings.headingImportCsvFile);
		infoText = I18nProperties.getString(Strings.infoImportCsvFile);
		ImportLayoutComponent importCsvComponent = new ImportLayoutComponent(2, headline, infoText, null, null);
		addComponent(importCsvComponent);
		ImportUploader receiver = new ImportUploader("_point_of_entry_import_", new Consumer<File>() {
			@Override
			public void accept(File file) {
				try {
					switch (infrastructureType) {
					case POINT_OF_ENTRY:
						PointOfEntryImporter importer = new PointOfEntryImporter(file, downloadErrorReportButton, currentUser, currentUI);
						importer.startImport();
						break;
					default:
						throw new UnsupportedOperationException("Import is currently only implemented for points of entry");
					}
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

		// Step 3: Download error report
		headline = I18nProperties.getString(Strings.headingDownloadErrorReport);
		infoText = I18nProperties.getString(Strings.infoDownloadErrorReport);
		buttonIcon = VaadinIcons.DOWNLOAD;
		buttonCaption = I18nProperties.getCaption(Captions.importDownloadErrorReport);
		ImportLayoutComponent errorReportComponent = new ImportLayoutComponent(3, headline, infoText, buttonIcon, buttonCaption);
		downloadErrorReportButton = errorReportComponent.getButton();
		errorReportComponent.getButton().setEnabled(false);
		addComponent(errorReportComponent);
	}

}
