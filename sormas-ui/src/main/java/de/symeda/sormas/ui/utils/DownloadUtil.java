package de.symeda.sormas.ui.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container.Indexed;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.ui.statistics.DatabaseExportView;

public class DownloadUtil {

	private DownloadUtil() {

	}

	@SuppressWarnings("serial")
	public static StreamResource createDatabaseExportStreamResource(DatabaseExportView databaseExportView, String fileName, String mimeType) {
		StreamResource streamResource = new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				try {
					Map<CheckBox, DatabaseTable> databaseToggles = databaseExportView.getDatabaseTableToggles();
					List<DatabaseTable> tablesToExport = new ArrayList<>();
					for (CheckBox checkBox : databaseToggles.keySet()) {
						if (checkBox.getValue() == true) {
							tablesToExport.add(databaseToggles.get(checkBox));
						}
					}

					String zipPath = FacadeProvider.getExportFacade().generateDatabaseExportArchive(tablesToExport);
					return new BufferedInputStream(Files.newInputStream(new File(zipPath).toPath()));
				} catch (IOException | ExportErrorException e) {
					// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
					// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
					databaseExportView.showExportErrorNotification();
					return null;
				}
			}
		}, fileName);
		streamResource.setMIMEType(mimeType);
		streamResource.setCacheTime(0);
		return streamResource;
	}

	public static StreamResource createGridExportStreamResource(Indexed container, List<Column> columns, String tempFilePrefix, String fileName, String... ignoredPropertyIds) {
		return new GridExportStreamResource(container, columns, tempFilePrefix, fileName, ignoredPropertyIds);
	}

	@SuppressWarnings("serial")
	public static StreamResource createStreamResource(String filePath, String fileName, String mimeType, String errorTitle, String errorText) {
		StreamResource streamResource = new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				try {
					return new BufferedInputStream(Files.newInputStream(new File(filePath).toPath()));
				} catch (IOException e) {
					// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
					// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
					new Notification(errorTitle, errorText, Type.ERROR_MESSAGE, false).show(Page.getCurrent());
					return null;
				}
			}
		}, fileName);
		streamResource.setMIMEType(mimeType);
		streamResource.setCacheTime(0);
		return streamResource;
	}

}