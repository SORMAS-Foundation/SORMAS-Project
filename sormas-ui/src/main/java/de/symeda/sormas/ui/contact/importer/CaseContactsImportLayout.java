package de.symeda.sormas.ui.contact.importer;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.ImportReceiver;

public class CaseContactsImportLayout extends AbstractImportLayout {

	private static final long serialVersionUID = 5037303173480715542L;

	public CaseContactsImportLayout(CaseDataDto caze) {
		super();

		ImportFacade importFacade = FacadeProvider.getImportFacade();

		addDownloadResourcesComponent(1, new ClassResource("/SORMAS_Contact_Import_Guide.pdf"));
		addDownloadImportTemplateComponent(
			2,
			importFacade.getCaseContactImportTemplateFilePath(),
			importFacade.getCaseContactImportTemplateFileName());
		addImportCsvComponent(3, new ImportReceiver("_case_contact_import_", file -> {

			resetDownloadErrorReportButton();
			try {
				ContactImporter importer = new ContactImporter(file, currentUser, caze, (ValueSeparator) separator.getValue());
				importer.startImport(this::extendDownloadErrorReportButton, currentUI, false);
			} catch (IOException | CsvValidationException e) {
				new Notification(
					I18nProperties.getString(Strings.headingImportFailed),
					I18nProperties.getString(Strings.messageImportFailed),
					Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		}));
		addDownloadErrorReportComponent(4);
	}
}
