package de.symeda.sormas.ui.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.opencsv.CSVWriter;
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
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.api.utils.Order;
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
	public static StreamResource createFileStreamResource(String filePath, String fileName, String mimeType, String errorTitle, String errorText) {
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

	@SuppressWarnings("serial")
	public static <T> StreamResource createCsvExportStreamResource(Class<T> exportRowClass, Supplier<List<T>> exportRowsSuplier, Function<String,String> propertyIdCaptionFunction, String exportFileName) {
		StreamResource extendedStreamResource = new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				
				try {

					List<T> exportRows = exportRowsSuplier.get();

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8.name());
					CSVWriter writer = CSVUtils.createCSVWriter(osw);
					
					// fields in order of declaration - not using Introspector here, because it gives properties in alphabetical order
					Method[] readMethods = Arrays.stream(exportRowClass.getDeclaredMethods())
							.filter(m -> m.getName().startsWith("get") || m.getName().startsWith("is"))
							.sorted((a,b) -> Integer.compare(a.getAnnotationsByType(Order.class)[0].value(), 
									b.getAnnotationsByType(Order.class)[0].value()))
							.toArray(Method[]::new);
									
					String[] fieldValues = new String[readMethods.length];
					for (int i=0; i<readMethods.length; i++) {
						Method method = readMethods[i];
						String propertyId = method.getName().startsWith("get") 
								? method.getName().substring(3)
								: method.getName().substring(2); 
								propertyId = Character.toLowerCase(propertyId.charAt(0)) + propertyId.substring(1);
						// field caption - export, case, person, symptoms, hospitalization
						fieldValues[i] = propertyIdCaptionFunction.apply(propertyId);
					}
					writer.writeNext(fieldValues);
					
					try {
						for (T exportRow : exportRows) {
							for (int i=0; i<readMethods.length; i++) {
								Method method = readMethods[i];
								Object value = method.invoke(exportRow);
								if (value == null) {
									fieldValues[i] = "";
								} else if (value instanceof Date) {
									fieldValues[i] = DateHelper.formatShortDate((Date)value);
								} else {
									fieldValues[i] = value.toString();
								}
							}
							writer.writeNext(fieldValues);
						};
					} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
						throw new RuntimeException(e);
					}

					osw.flush();
					baos.flush();
					
					return new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
				} catch (IOException e) {
					// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
					// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
					new Notification("Export failed", "There was an error trying to provide the data to export. Please contact an admin and inform them about this issue.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
					return null;
				}
			}
		}, exportFileName);
		extendedStreamResource.setMIMEType("text/csv");
		extendedStreamResource.setCacheTime(0);
		return extendedStreamResource;
	}

}