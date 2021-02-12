package de.symeda.sormas.ui.events.importer;

import java.io.IOException;
import java.util.function.Consumer;

import com.opencsv.exceptions.CsvValidationException;
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

public class LineListingImportLayout extends AbstractImportLayout {

	private static final long serialVersionUID = 8979253668010454656L;

	public LineListingImportLayout() {

		super();

		addDownloadResourcesComponent(
			1,
			new ClassResource("/SORMAS_Line_Listing_Import_Guide.pdf"),
			new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));
		addDownloadImportTemplateComponent(
			2,
			FacadeProvider.getImportFacade().getCaseLineListingImportTemplateFilePath(),
			"sormas_import_line_listing_template.csv");
		addImportCsvComponent(3, new ImportReceiver("_line_listing_import_", file -> {
			resetDownloadErrorReportButton();

			try {
				EventImporter importer = new EventImporter(file, false, currentUser);
				importer.startImport(this::extendDownloadErrorReportButton, currentUI, true);
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
