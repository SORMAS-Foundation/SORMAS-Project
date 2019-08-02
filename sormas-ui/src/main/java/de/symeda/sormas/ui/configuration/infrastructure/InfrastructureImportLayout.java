package de.symeda.sormas.ui.configuration.infrastructure;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.ImportReceiver;
import de.symeda.sormas.ui.importer.PointOfEntryImporter;

@SuppressWarnings("serial")
public class InfrastructureImportLayout extends AbstractImportLayout {

	public InfrastructureImportLayout(InfrastructureType infrastructureType) {
		super();
		
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

		addDownloadImportTemplateComponent(1, templateFilePath, templateFileName);
		addImportCsvComponent(2, new ImportReceiver("_point_of_entry_import_", new Consumer<File>() {
			@Override
			public void accept(File file) {
				resetDownloadErrorReportButton();
				
				try {
					switch (infrastructureType) {
					case POINT_OF_ENTRY:
						PointOfEntryImporter importer = new PointOfEntryImporter(file, currentUser, currentUI);
						importer.startImport(new Consumer<StreamResource>() {
							@Override
							public void accept(StreamResource resource) {
								extendDownloadErrorReportButton(resource);
							}
						});
						break;
					default:
						throw new UnsupportedOperationException("Import is currently only implemented for points of entry");
					}
				} catch (IOException e) {
					new Notification(I18nProperties.getString(Strings.headingImportFailed), I18nProperties.getString(Strings.messageImportFailed), Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				}
			}
		}));
		addDownloadErrorReportComponent(3);
	}

}
