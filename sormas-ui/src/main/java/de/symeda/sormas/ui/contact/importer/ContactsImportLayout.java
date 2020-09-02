package de.symeda.sormas.ui.contact.importer;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

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

public class ContactsImportLayout extends AbstractImportLayout {

	private static final long serialVersionUID = -5604794287598407322L;

	public ContactsImportLayout() {
		super();

		addDownloadResourcesComponent(
			1,
			new ClassResource("/SORMAS_Contact_Import_Guide.pdf"),
			new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));
		addDownloadImportTemplateComponent(
			2,
			FacadeProvider.getImportFacade().getContactImportTemplateFilePath(),
			"sormas_import_contact_template.csv");
		addImportCsvComponent(3, new ImportReceiver("_contact_import_", new Consumer<File>() {

			@Override
			public void accept(File file) {
				resetDownloadErrorReportButton();

				try {
					ContactImporter importer = new ContactImporter(file, false, currentUser, null);
					importer.startImport(new Consumer<StreamResource>() {

						@Override
						public void accept(StreamResource resource) {
							extendDownloadErrorReportButton(resource);
						}
					}, currentUI, false);
				} catch (IOException e) {
					new Notification(
						I18nProperties.getString(Strings.headingImportFailed),
						I18nProperties.getString(Strings.messageImportFailed),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}
		}));
		addDownloadErrorReportComponent(4);
	}
}
