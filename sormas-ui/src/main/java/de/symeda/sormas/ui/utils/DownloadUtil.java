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
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Grid.Column;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ExportTarget;
import de.symeda.sormas.api.ExportType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitExportDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.PrescriptionExportDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentExportDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.ui.statistics.DatabaseExportView;

public class DownloadUtil {

	public static final int DETAILED_EXPORT_STEP_SIZE = 200;
	private static final Logger logger = LoggerFactory.getLogger(DownloadUtil.class);

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
	public static StreamResource createCaseManagementExportResource(String userUuid, CaseCriteria criteria, String exportFileName) {
		StreamResource casesResource = createCsvExportStreamResource(CaseExportDto.class, ExportType.CASE_MANAGEMENT,
				(Integer start, Integer max) -> FacadeProvider.getCaseFacade().getExportList(criteria, ExportType.CASE_MANAGEMENT, start, max, userUuid),
				(propertyId,type) -> {
					String caption = I18nProperties.getPrefixCaption(CaseExportDto.I18N_PREFIX, propertyId,
							I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, propertyId,
									I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, propertyId,
											I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, propertyId,
													I18nProperties.getPrefixCaption(EpiDataDto.I18N_PREFIX, propertyId,
															I18nProperties.getPrefixCaption(HospitalizationDto.I18N_PREFIX, propertyId,
																	I18nProperties.getPrefixCaption(HealthConditionsDto.I18N_PREFIX, propertyId)))))));
					if (Date.class.isAssignableFrom(type)) {
						caption += " (" + DateHelper.getLocalShortDatePattern() + ")";
					}
					return caption;
				},
				"sormas_cases_" + DateHelper.formatDateForExport(new Date()) + ".csv");
		
		StreamResource prescriptionsResource = createCsvExportStreamResource(PrescriptionExportDto.class, null,
				(Integer start, Integer max) -> FacadeProvider.getPrescriptionFacade().getExportList(userUuid, criteria, start, max),
				(propertyId,type) -> {
					String caption = I18nProperties.getPrefixCaption(PrescriptionExportDto.I18N_PREFIX, propertyId,
							I18nProperties.getPrefixCaption(PrescriptionDto.I18N_PREFIX, propertyId));
					if (Date.class.isAssignableFrom(type)) {
						caption += " (" + DateHelper.getLocalShortDatePattern() + ")";
					}
					return caption;
				},
				"sormas_prescriptions_" + DateHelper.formatDateForExport(new Date()) + ".csv");

		StreamResource treatmentsResource = createCsvExportStreamResource(TreatmentExportDto.class, null,
				(Integer start, Integer max) -> FacadeProvider.getTreatmentFacade().getExportList(userUuid, criteria, start, max),
				(propertyId,type) -> {
					String caption = I18nProperties.getPrefixCaption(TreatmentExportDto.I18N_PREFIX, propertyId,
							I18nProperties.getPrefixCaption(TreatmentDto.I18N_PREFIX, propertyId));
					if (Date.class.isAssignableFrom(type)) {
						caption += " (" + DateHelper.getLocalShortDatePattern() + ")";
					}
					return caption;
				},
				"sormas_prescriptions_" + DateHelper.formatDateForExport(new Date()) + ".csv");
		
		StreamResource clinicalVisitsResource = createCsvExportStreamResource(ClinicalVisitExportDto.class, null,
				(Integer start, Integer max) -> FacadeProvider.getClinicalVisitFacade().getExportList(userUuid, criteria, start, max),
				(propertyId,type) -> {
					String caption = I18nProperties.getPrefixCaption(ClinicalVisitExportDto.I18N_PREFIX, propertyId,
							I18nProperties.getPrefixCaption(ClinicalVisitDto.I18N_PREFIX, propertyId,
									I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, propertyId)));
					if (Date.class.isAssignableFrom(type)) {
						caption += " (" + DateHelper.getLocalShortDatePattern() + ")";
					}
					return caption;
				},
				"sormas_clinical_assessments_" + DateHelper.formatDateForExport(new Date()) + ".csv");
		
		StreamResource caseManagementStreamResource = new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				String zipFile = FacadeProvider.getExportFacade().generateZipArchive(DateHelper.formatDateForExport(new Date()), new Random().nextInt(Integer.MAX_VALUE));
				try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {
					writeCsvToZip(zos, casesResource.getStreamSource(), "cases.csv");
					writeCsvToZip(zos, prescriptionsResource.getStreamSource(), "prescriptions.csv");
					writeCsvToZip(zos, treatmentsResource.getStreamSource(), "treatments.csv");
					writeCsvToZip(zos, clinicalVisitsResource.getStreamSource(), "clinical_assessments.csv");
					zos.close();
					return new FileInputStream(new File(zipFile));
				} catch (IOException e) {
					logger.error("Failed to generate a zip file for case management export.");
					return null;
				}
			}
		}, exportFileName);
		caseManagementStreamResource.setMIMEType("application/zip");
		caseManagementStreamResource.setCacheTime(0);
		return caseManagementStreamResource;
	}
	
	private static void writeCsvToZip(ZipOutputStream zos, StreamSource source, String fileName) throws IOException {
		zos.putNextEntry(new ZipEntry(fileName));
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		InputStream input = source.getStream();
		while ((length = input.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		result.writeTo(zos);
		zos.closeEntry();
	}

	@SuppressWarnings("serial")
	public static <T> StreamResource createCsvExportStreamResource(Class<T> exportRowClass, ExportType exportType, BiFunction<Integer, Integer, List<T>> exportRowsSupplier, BiFunction<String,Class<?>,String> propertyIdCaptionFunction, String exportFileName) {
		StreamResource extendedStreamResource = new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
					try (CSVWriter writer = CSVUtils.createCSVWriter(
							new OutputStreamWriter(byteStream, StandardCharsets.UTF_8.name()), FacadeProvider.getConfigFacade().getCsvSeparator())) {
	
						// 1. fields in order of declaration - not using Introspector here, because it gives properties in alphabetical order
						List<Method> readMethods = new ArrayList<Method>();
						readMethods.addAll(Arrays.stream(exportRowClass.getDeclaredMethods())
								.filter(m -> (m.getName().startsWith("get") || m.getName().startsWith("is")) 
										&& m.isAnnotationPresent(Order.class)
										&& (exportType == null || (m.isAnnotationPresent(ExportTarget.class) && Arrays.asList(m.getAnnotation(ExportTarget.class).exportTypes()).contains(exportType))))
								.sorted((a,b) -> Integer.compare(a.getAnnotationsByType(Order.class)[0].value(), 
										b.getAnnotationsByType(Order.class)[0].value()))
								.collect(Collectors.toList()));

						// 2. replace entity fields with all the columns of the entity 
						Map<Method, Function<T,?>> subEntityProviders = new HashMap<Method, Function<T,?>>();
						for (int i = 0; i < readMethods.size(); i++) {
							Method method = readMethods.get(i);
							if (EntityDto.class.isAssignableFrom(method.getReturnType())) {
								
								// allows us to access the sub entity
								Function<T, ?> subEntityProvider = (o) -> {
									try {
										return method.invoke(o);
									} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
										throw new RuntimeException(e);
									}
								};

								// remove entity field
								readMethods.remove(i);
								
								// add columns of the entity
								List<Method> subReadMethods = Arrays.stream(method.getReturnType().getDeclaredMethods())
										.filter(m -> (m.getName().startsWith("get") || m.getName().startsWith("is")) && m.isAnnotationPresent(Order.class))
										.sorted((a,b) -> Integer.compare(a.getAnnotationsByType(Order.class)[0].value(), 
												b.getAnnotationsByType(Order.class)[0].value()))
										.collect(Collectors.toList());
								readMethods.addAll(i, subReadMethods);
								i--;
								
								for (Method subReadMethod : subReadMethods) {
									subEntityProviders.put(subReadMethod, subEntityProvider);
								}
							}							
						}
						
						String[] fieldValues = new String[readMethods.size()];
						for (int i = 0; i < readMethods.size(); i++) {
							Method method = readMethods.get(i);
							// field caption
							String propertyId = method.getName().startsWith("get") 
									? method.getName().substring(3)
											: method.getName().substring(2); 
							propertyId = Character.toLowerCase(propertyId.charAt(0)) + propertyId.substring(1);
							fieldValues[i] = propertyIdCaptionFunction.apply(propertyId, method.getReturnType());
						}
						writer.writeNext(fieldValues);
	
						int startIndex = 0;
						List<T> exportRows = exportRowsSupplier.apply(startIndex, DETAILED_EXPORT_STEP_SIZE);
						while (!exportRows.isEmpty()) {						
							try {
								for (T exportRow : exportRows) {
									for (int i=0; i<readMethods.size(); i++) {
										Method method = readMethods.get(i);
										Function<T,?> subEntityProvider = subEntityProviders.getOrDefault(method, null);
										Object entity = subEntityProvider != null ? subEntityProvider.apply(exportRow) : exportRow;
										// Sub entity might be null
										Object value = entity != null ? method.invoke(entity) : null;
										if (value == null) {
											fieldValues[i] = "";
										} else if (value instanceof Date) {
											fieldValues[i] = DateHelper.formatLocalShortDate((Date)value);
										} else if (value.getClass().equals(boolean.class) || value.getClass().equals(Boolean.class)) {
											fieldValues[i] = DataHelper.parseBoolean((Boolean) value);
										} else if (value instanceof Set) {
											StringBuilder sb = new StringBuilder();
											for (Object o : (Set<?>) value) {
												if (sb.length() != 0) {
													sb.append(", ");
												}
												sb.append(o);
											}
											fieldValues[i] = sb.toString();
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