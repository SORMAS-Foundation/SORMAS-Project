package de.symeda.sormas.ui.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Property;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.ui.login.LoginHelper;
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

					String zipPath = FacadeProvider.getImportExportFacade().generateDatabaseExportArchive(tablesToExport);
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

	@SuppressWarnings("serial")
	public static StreamResource createGridExportStreamResource(Grid grid, String tempFilePrefix, String fileName, String mimeType, String... ignoredPropertyIds) {
		StreamResource streamResource = new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				try {
					Indexed container = grid.getContainerDataSource();
					List<Column> columns = new ArrayList<>(grid.getColumns());
					List<String> ignoredPropertyIdsList = Arrays.asList(ignoredPropertyIds);
					columns.removeIf(c -> c.isHidden());
					columns.removeIf(c -> ignoredPropertyIdsList.contains(c.getPropertyId()));
					Collection<?> itemIds = container.getItemIds();

					List<List<String>> exportedRows = new ArrayList<>();
					
					List<String> headerRow = new ArrayList<>();
					columns.forEach(c -> {
						headerRow.add(c.getHeaderCaption());
					});
					exportedRows.add(headerRow);
					
					itemIds.forEach(i -> {
						List<String> row = new ArrayList<>();
						columns.forEach(c -> {
							Property<?> property = container.getItem(i).getItemProperty(c.getPropertyId());
							if (property.getValue() != null) {
								if (property.getType() == Date.class) {
									row.add(DateHelper.formatDateTime((Date) property.getValue()));
								} else if (property.getType() == Boolean.class) {
									if ((Boolean) property.getValue() == true) {
										row.add("Yes");
									} else
										row.add("No");
								} else {
									row.add(property.getValue().toString());
								}
							} else {
								row.add("");
							}
						});

						exportedRows.add(row);
					});

					String csvPath = FacadeProvider.getImportExportFacade().generateGridExportCsv(exportedRows, tempFilePrefix, LoginHelper.getCurrentUser().getUuid());
					return new BufferedInputStream(Files.newInputStream(new File(csvPath).toPath()));
				} catch (IOException e) {
					// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
					// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
					new Notification("Export failed", "There was an error trying to provide the data to export. Please contact an admin and inform them about this issue.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
					return null;
				}
			}
		}, fileName);
		streamResource.setMIMEType(mimeType);
		streamResource.setCacheTime(0);
		return streamResource;
	}

	@SuppressWarnings("serial")
	public static StreamResource createStreamResource(AbstractView sourceView, String filePath, String fileName, String mimeType, String errorTitle, String errorText) {
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