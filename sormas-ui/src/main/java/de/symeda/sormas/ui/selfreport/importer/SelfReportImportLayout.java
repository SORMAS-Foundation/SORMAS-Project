package de.symeda.sormas.ui.selfreport.importer;

import java.io.IOException;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.ImportReceiver;

public class SelfReportImportLayout extends AbstractImportLayout {

	private static final long serialVersionUID = 1L;

	public SelfReportImportLayout() {

		super();

		ImportFacade importFacade = FacadeProvider.getImportFacade();

		addDownloadResourcesComponent(1, new ClassResource("/SORMAS_Self_Report_Import_Guide.pdf"));
		addDownloadImportTemplateComponent(2, importFacade.getSelfReportImportTemplateFilePath(), importFacade.getSelfReportImportTemplateFileName());
		addImportCsvComponent(3, new ImportReceiver("_self_report_import_", file -> {
			resetDownloadErrorReportButton();

			try {
				SelfReportImporter importer = new SelfReportImporter(file, true, currentUser, (ValueSeparator) separator.getValue());
				importer.startImport(SelfReportImportLayout.this::extendDownloadErrorReportButton, currentUI, true);
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
