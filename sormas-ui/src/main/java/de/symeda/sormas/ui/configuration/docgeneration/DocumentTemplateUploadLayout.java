package de.symeda.sormas.ui.configuration.docgeneration;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Upload;

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
		addDownloadResourcesComponent();
		addUploadResourceComponent();
	}

	protected void addDownloadResourcesComponent() {
		ImportLayoutComponent importGuideComponent = new ImportLayoutComponent(
			1,
			I18nProperties.getString(Strings.headingDownloadDocumentTemplateGuide),
			I18nProperties.getString(Strings.infoDownloadDocumentTemplateImportGuide),
			VaadinIcons.FILE_PRESENTATION,
			I18nProperties.getCaption(Captions.DocumentTemplate_documentTemplateGuide));

		Button button = importGuideComponent.getButton();
		addFileDownloader(button, new ClassResource("/SORMAS_Document_Template_Guide.pdf"));

		Button exampleTemplateWordButton = ButtonHelper.createIconButton(
			Captions.DocumentTemplate_exampleTemplateWord,
			VaadinIcons.FILE_TEXT,
			null,
			ValoTheme.BUTTON_PRIMARY,
			CssStyles.VSPACE_TOP_3);
		addFileDownloader(exampleTemplateWordButton, new ClassResource("/ExampleTemplateMicrosoftWord.docx"));
		importGuideComponent.addComponent(exampleTemplateWordButton);

		Button exampleTemplateLibreOfficeButton = ButtonHelper.createIconButton(
			Captions.DocumentTemplate_exampleTemplateLibreOffice,
			VaadinIcons.FILE_TEXT,
			null,
			ValoTheme.BUTTON_PRIMARY,
			CssStyles.VSPACE_TOP_3);
		addFileDownloader(exampleTemplateLibreOfficeButton, new ClassResource("/ExampleTemplateLibreOffice.docx"));
		importGuideComponent.addComponent(exampleTemplateLibreOfficeButton);

		Button dataDictionaryButton = ButtonHelper
			.createIconButton(Captions.importDownloadDataDictionary, VaadinIcons.FILE_TABLE, null, ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_TOP_3);
		addFileDownloader(dataDictionaryButton, new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));
		importGuideComponent.addComponent(dataDictionaryButton);

		addComponent(importGuideComponent);

	}

	private void addUploadResourceComponent() {
		String headline = I18nProperties.getCaption(Captions.DocumentTemplate_uploadTemplate);
		String infoText = I18nProperties.getString(Strings.infoUploadDocumentTemplate);

		ImportLayoutComponent uploadTemplateComponent = new ImportLayoutComponent(2, headline, infoText, null, null);
		addComponent(uploadTemplateComponent);

		DocumentTemplateReceiver receiver = new DocumentTemplateReceiver();
		upload = new Upload("", receiver);
		CssStyles.style(upload, CssStyles.VSPACE_2);
		upload.addSucceededListener(receiver);
		addComponent(upload);
	}

	private void addFileDownloader(Button button, ClassResource importGuideResource) {
		FileDownloader importGuideDownloader = new FileDownloader(importGuideResource);
		importGuideDownloader.extend(button);
	}
}
