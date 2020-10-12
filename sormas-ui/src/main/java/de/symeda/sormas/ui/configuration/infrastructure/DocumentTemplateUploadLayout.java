package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.Upload;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.importer.DocumentTemplateReceiver;
import de.symeda.sormas.ui.importer.ImportLayoutComponent;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/*
 * Layout for Uploading Templates
 */
public class DocumentTemplateUploadLayout extends VerticalLayout {

	protected Upload upload;

	public DocumentTemplateUploadLayout() {

		super();

		addDownloadResourcesComponent(
			1,
			new ClassResource("/SORMAS_Document_Template_Guide.pdf"),
			new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));

		DocumentTemplateReceiver receiver = new DocumentTemplateReceiver();

		String headline = I18nProperties.getCaption(Captions.DocumentTemplate_uploadTemplate);
		String infoText = I18nProperties.getString(Strings.infoUploadDocumentTemplate);

		ImportLayoutComponent uploadTemplateComponent = new ImportLayoutComponent(2, headline, infoText, null, null);
		addComponent(uploadTemplateComponent);
		upload = new Upload("", receiver);
		upload.setButtonCaption(I18nProperties.getCaption(Captions.DocumentTemplate_uploadTemplate));
		CssStyles.style(upload, CssStyles.VSPACE_2);
		upload.addSucceededListener(receiver);
		addComponent(upload);
	}

	protected void addDownloadResourcesComponent(int step, ClassResource importGuideResource, ClassResource dataDictionaryResource) {
		String headline = I18nProperties.getString(Strings.headingDownloadDocumentTemplateGuide);
		String infoText = I18nProperties.getString(Strings.infoDownloadDocumentTemplateImportGuide);
		Resource buttonIcon = VaadinIcons.FILE_PRESENTATION;
		String buttonCaption = I18nProperties.getCaption(Captions.DocumentTemplate_downloadUploadGuide);
		ImportLayoutComponent importGuideComponent = new ImportLayoutComponent(step, headline, infoText, buttonIcon, buttonCaption);
		FileDownloader importGuideDownloader = new FileDownloader(importGuideResource);
		importGuideDownloader.extend(importGuideComponent.getButton());
		addComponent(importGuideComponent);

		Button dataDictionaryButton = ButtonHelper.createIconButton(
			Captions.importDownloadDataDictionary,
			VaadinIcons.FILE_TABLE,
			null,
			ValoTheme.BUTTON_PRIMARY,
			CssStyles.VSPACE_TOP_3,
			CssStyles.VSPACE_2);

		FileDownloader dataDictionaryDownloader = new FileDownloader(dataDictionaryResource);
		dataDictionaryDownloader.extend(dataDictionaryButton);

		addComponent(dataDictionaryButton);
	}
}
