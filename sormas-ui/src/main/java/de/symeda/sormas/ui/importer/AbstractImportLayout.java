package de.symeda.sormas.ui.importer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.Upload;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

@SuppressWarnings("serial")
public class AbstractImportLayout extends VerticalLayout {

	protected Button downloadErrorReportButton;
	protected Upload upload;
	protected final UserReferenceDto currentUser;
	protected final UI currentUI;
	
	public AbstractImportLayout() {
		currentUser = UserProvider.getCurrent().getUserReference();
		currentUI = UI.getCurrent();
		setSpacing(false);
		setMargin(true);
	}
	
	protected void addDownloadResourcesComponent(int step, ClassResource importGuideResource, ClassResource dataDictionaryResource) {
		String headline = I18nProperties.getString(Strings.headingDownloadImportGuide);
		String infoText = I18nProperties.getString(Strings.infoDownloadImportGuide);
		Resource buttonIcon = VaadinIcons.FILE_PRESENTATION;
		String buttonCaption = I18nProperties.getCaption(Captions.importDownloadImportGuide);
		ImportLayoutComponent importGuideComponent = new ImportLayoutComponent(step, headline, infoText, buttonIcon, buttonCaption);
		FileDownloader importGuideDownloader = new FileDownloader(importGuideResource);
		importGuideDownloader.extend(importGuideComponent.getButton());
		addComponent(importGuideComponent);

		Button dataDictionaryButton = ButtonHelper.createIconButton(Captions.importDownloadDataDictionary, VaadinIcons.FILE_TABLE, null,
				ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_TOP_3, CssStyles.VSPACE_2);

		FileDownloader dataDictionaryDownloader = new FileDownloader(dataDictionaryResource);
		dataDictionaryDownloader.extend(dataDictionaryButton);

		addComponent(dataDictionaryButton);
	}
	
	protected void addDownloadImportTemplateComponent(int step, String templateFilePath, String templateFileName) {
		String headline = I18nProperties.getString(Strings.headingDownloadImportTemplate);
		String infoText = I18nProperties.getString(Strings.infoDownloadImportTemplate);
		Resource buttonIcon = VaadinIcons.DOWNLOAD;
		String buttonCaption = I18nProperties.getCaption(Captions.importDownloadImportTemplate);
		ImportLayoutComponent importTemplateComponent = new ImportLayoutComponent(step, headline, infoText, buttonIcon, buttonCaption);
		StreamResource templateResource = DownloadUtil.createFileStreamResource(templateFilePath, templateFileName, "text/csv",
				I18nProperties.getString(Strings.headingTemplateNotAvailable), I18nProperties.getString(Strings.messageTemplateNotAvailable));
		FileDownloader templateFileDownloader = new FileDownloader(templateResource);
		templateFileDownloader.extend(importTemplateComponent.getButton());
		CssStyles.style(importTemplateComponent, CssStyles.VSPACE_2);
		addComponent(importTemplateComponent);
	}
	
	protected void addImportCsvComponent(int step, ImportReceiver receiver) {
		String headline = I18nProperties.getString(Strings.headingImportCsvFile);
		String infoText = I18nProperties.getString(Strings.infoImportCsvFile);
		ImportLayoutComponent importCsvComponent = new ImportLayoutComponent(step, headline, infoText, null, null);
		addComponent(importCsvComponent);
		upload = new Upload("", receiver);
		upload.setButtonCaption(I18nProperties.getCaption(Captions.importImportData));
		CssStyles.style(upload, CssStyles.VSPACE_2);
		upload.addSucceededListener(receiver);
		addComponent(upload);
	}
	
	protected void addDownloadErrorReportComponent(int step) {
		String headline = I18nProperties.getString(Strings.headingDownloadErrorReport);
		String infoText = I18nProperties.getString(Strings.infoDownloadErrorReport);
		Resource buttonIcon = VaadinIcons.DOWNLOAD;
		String buttonCaption = I18nProperties.getCaption(Captions.importDownloadErrorReport);
		ImportLayoutComponent errorReportComponent = new ImportLayoutComponent(step, headline, infoText, buttonIcon, buttonCaption);
		downloadErrorReportButton = errorReportComponent.getButton();
		errorReportComponent.getButton().setEnabled(false);
		addComponent(errorReportComponent);
	}
	
	protected void resetDownloadErrorReportButton() {
		downloadErrorReportButton.setEnabled(false);
		for (int i = 0; i < downloadErrorReportButton.getExtensions().size(); i++) {
			Extension ext = downloadErrorReportButton.getExtensions().iterator().next();
			downloadErrorReportButton.removeExtension(ext);
		}
	}
	
	protected void extendDownloadErrorReportButton(StreamResource streamResource) {
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(downloadErrorReportButton);
		downloadErrorReportButton.setEnabled(true);
	}

}
