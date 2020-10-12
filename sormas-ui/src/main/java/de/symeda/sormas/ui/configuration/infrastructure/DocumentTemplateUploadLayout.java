package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.server.ClassResource;
import com.vaadin.v7.ui.Upload;

import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.DocumentTemplateReceiver;
import de.symeda.sormas.ui.importer.ImportLayoutComponent;
import de.symeda.sormas.ui.utils.CssStyles;

/*
 * Layout for Uploading Templates
 */
public class DocumentTemplateUploadLayout extends AbstractImportLayout {

	public DocumentTemplateUploadLayout() {

		super();

		addDownloadResourcesComponent(
			1,
			new ClassResource("/SORMAS_Infrastructure_Import_Guide.pdf"),
			new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));

		DocumentTemplateReceiver receiver = new DocumentTemplateReceiver();

		String headline = "i18nheadline";
		String infoText = "i18nInfotext WARNING: Uploading new Documents might override existing Documents with identical Names!";
		ImportLayoutComponent uploadTemplateComponent = new ImportLayoutComponent(2, headline, infoText, null, null);
		addComponent(uploadTemplateComponent);
		upload = new Upload("", receiver);
		upload.setButtonCaption("I18n upload");
		CssStyles.style(upload, CssStyles.VSPACE_2);
		upload.addSucceededListener(receiver);
		addComponent(upload);
	}
}
