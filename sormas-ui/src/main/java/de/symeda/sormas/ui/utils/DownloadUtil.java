/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
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
import java.util.function.BiFunction;

import com.opencsv.CSVWriter;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Grid.Column;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.ui.statistics.DatabaseExportView;

public class DownloadUtil {

	public static final int DETAILED_EXPORT_STEP_SIZE = 200;

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
		return new V7GridExportStreamResource(container, columns, tempFilePrefix, fileName, ignoredPropertyIds);
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
	public static StreamResource createStringStreamResource(String content, String fileName, String mimeType) {
		StreamResource streamResource = new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
			}
		}, fileName);
		streamResource.setMIMEType(mimeType);
		return streamResource;
	}

	@SuppressWarnings("serial")
	public static <T> StreamResource createCsvExportStreamResource(Class<T> exportRowClass, BiFunction<Integer, Integer, List<T>> exportRowsSupplier, BiFunction<String,Class<?>,String> propertyIdCaptionFunction, String exportFileName) {
		StreamResource extendedStreamResource = new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
					try (CSVWriter writer = CSVUtils.createCSVWriter(
							new OutputStreamWriter(byteStream, StandardCharsets.UTF_8.name()), FacadeProvider.getConfigFacade().getCsvSeparator())) {
	
						// fields in order of declaration - not using Introspector here, because it gives properties in alphabetical order
						Method[] readMethods = Arrays.stream(exportRowClass.getDeclaredMethods())
								.filter(m -> (m.getName().startsWith("get") || m.getName().startsWith("is")) && m.isAnnotationPresent(Order.class))
								.sorted((a,b) -> Integer.compare(a.getAnnotationsByType(Order.class)[0].value(), 
										b.getAnnotationsByType(Order.class)[0].value()))
								.toArray(Method[]::new);
	
						String[] fieldValues = new String[readMethods.length];
						for (int i = 0; i < readMethods.length; i++) {
							Method method = readMethods[i];
							String propertyId = method.getName().startsWith("get") 
									? method.getName().substring(3)
											: method.getName().substring(2); 
									propertyId = Character.toLowerCase(propertyId.charAt(0)) + propertyId.substring(1);
									// field caption - export, case, person, symptoms, hospitalization
									fieldValues[i] = propertyIdCaptionFunction.apply(propertyId, method.getReturnType());
						}
						writer.writeNext(fieldValues);
	
						int startIndex = 0;
						List<T> exportRows = exportRowsSupplier.apply(startIndex, DETAILED_EXPORT_STEP_SIZE);
						while (!exportRows.isEmpty()) {						
							try {
								for (T exportRow : exportRows) {
									for (int i=0; i<readMethods.length; i++) {
										Method method = readMethods[i];
										Object value = method.invoke(exportRow);
										if (value == null) {
											fieldValues[i] = "";
										} else if (value instanceof Date) {
											fieldValues[i] = DateHelper.formatLocalShortDate((Date)value);
										} else {
											fieldValues[i] = value.toString();
										}
									}
									writer.writeNext(fieldValues);
								};
							} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
								throw new RuntimeException(e);
							}
	
							writer.flush();
							startIndex += DETAILED_EXPORT_STEP_SIZE;
							exportRows = exportRowsSupplier.apply(startIndex, DETAILED_EXPORT_STEP_SIZE);
						}
					}
					return new BufferedInputStream(new ByteArrayInputStream(byteStream.toByteArray()));
				} catch (IOException e) {
					// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
					// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
					new Notification(I18nProperties.getString(Strings.headingExportFailed), I18nProperties.getString(Strings.messageExportFailed), 
							Type.ERROR_MESSAGE, false).show(Page.getCurrent());
					return null;
				}
			}
		}, exportFileName);
		extendedStreamResource.setMIMEType("text/csv");
		extendedStreamResource.setCacheTime(0);
		return extendedStreamResource;
	}

}