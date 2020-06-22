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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Grid.Column;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.BirthDateDto;
import de.symeda.sormas.api.caze.BurialInfoDto;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitExportDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.importexport.ExportTarget;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.Sex;
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
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitExportType;
import de.symeda.sormas.api.visit.VisitSummaryExportDto;
import de.symeda.sormas.ui.statistics.DatabaseExportView;

public final class DownloadUtil {

	private DownloadUtil() {
		// Hide Utility Class Constructor
	}

	public static final int DETAILED_EXPORT_STEP_SIZE = 50;

	public static StreamResource createDatabaseExportStreamResource(DatabaseExportView databaseExportView, String fileName, String mimeType) {

		StreamResource streamResource = new StreamResource(() -> {
			Map<CheckBox, DatabaseTable> databaseToggles = databaseExportView.getDatabaseTableToggles();
			List<DatabaseTable> tablesToExport = new ArrayList<>();
			for (CheckBox checkBox : databaseToggles.keySet()) {
				if (checkBox.getValue() == true) {
					tablesToExport.add(databaseToggles.get(checkBox));
				}
			}
			return new DelayedInputStream(() -> {

				try {
					String zipPath = FacadeProvider.getExportFacade().generateDatabaseExportArchive(tablesToExport);
					return new BufferedInputStream(Files.newInputStream(new File(zipPath).toPath()));
				} catch (IOException | ExportErrorException e) {
					LoggerFactory.getLogger(DownloadUtil.class).error(e.getMessage(), e);
					// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
					// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
					databaseExportView.showExportErrorNotification();
					return null;
				}

			});
		}, fileName);
		streamResource.setMIMEType(mimeType);
		streamResource.setCacheTime(0);
		return streamResource;
	}

	public static StreamResource createGridExportStreamResource(
		Indexed container,
		List<Column> columns,
		String tempFilePrefix,
		String fileName,
		String... ignoredPropertyIds) {

		return new V7GridExportStreamResource(container, columns, tempFilePrefix, fileName, ignoredPropertyIds);
	}

	public static StreamResource createFileStreamResource(String filePath, String fileName, String mimeType, String errorTitle, String errorText) {

		StreamResource streamResource = new StreamResource(() -> {
			try {
				return new BufferedInputStream(Files.newInputStream(new File(filePath).toPath()));
			} catch (IOException e) {
				// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
				// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
				new Notification(errorTitle, errorText, Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				return null;
			}
		}, fileName);
		streamResource.setMIMEType(mimeType);
		streamResource.setCacheTime(0);
		return streamResource;
	}

	public static StreamResource createStringStreamResource(String content, String fileName, String mimeType) {

		StreamResource streamResource = new StreamResource(() -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), fileName);
		streamResource.setMIMEType(mimeType);
		return streamResource;
	}

	@SuppressWarnings("serial")
	public static StreamResource createPopulationDataExportResource(String exportFileName) {

		StreamResource populationDataStreamResource = new StreamResource(new StreamSource() {

			@Override
			public InputStream getStream() {
				try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
					try (CSVWriter writer = CSVUtils.createCSVWriter(
						new OutputStreamWriter(byteStream, StandardCharsets.UTF_8.name()),
						FacadeProvider.getConfigFacade().getCsvSeparator())) {
						// Generate and write columns to CSV writer
						List<String> columnNames = new ArrayList<>();
						columnNames.add(I18nProperties.getPrefixCaption(PopulationDataDto.I18N_PREFIX, PopulationDataDto.REGION));
						columnNames.add(I18nProperties.getPrefixCaption(PopulationDataDto.I18N_PREFIX, PopulationDataDto.DISTRICT));
						columnNames.add(I18nProperties.getString(Strings.total));
						columnNames.add(I18nProperties.getCaption(Captions.populationDataMaleTotal));
						columnNames.add(I18nProperties.getCaption(Captions.populationDataFemaleTotal));

						Map<AgeGroup, Integer> ageGroupPositions = new HashMap<>();
						int ageGroupIndex = 5;
						for (AgeGroup ageGroup : AgeGroup.values()) {
							columnNames.add(DataHelper.getSexAndAgeGroupString(ageGroup, null));
							columnNames.add(DataHelper.getSexAndAgeGroupString(ageGroup, Sex.MALE));
							columnNames.add(DataHelper.getSexAndAgeGroupString(ageGroup, Sex.FEMALE));
							ageGroupPositions.put(ageGroup, ageGroupIndex);
							ageGroupIndex += 3;
						}

						writer.writeNext(columnNames.toArray(new String[columnNames.size()]));

						List<Object[]> populationExportDataList = FacadeProvider.getPopulationDataFacade().getPopulationDataForExport();

						String[] exportLine = new String[columnNames.size()];
						String regionName = "";
						String districtName = "";
						for (Object[] populationExportData : populationExportDataList) {
							String dataRegionName = (String) populationExportData[0];
							String dataDistrictName = populationExportData[1] == null ? "" : (String) populationExportData[1];
							if (exportLine[0] != null && (!dataRegionName.equals(regionName) || !dataDistrictName.equals(districtName))) {
								// New region or district reached; write line to CSV
								writer.writeNext(exportLine);
								exportLine = new String[columnNames.size()];
							}
							regionName = dataRegionName;
							districtName = dataDistrictName;

							// Region
							if (exportLine[0] == null) {
								exportLine[0] = (String) populationExportData[0];
							}
							// District
							if (exportLine[1] == null) {
								exportLine[1] = (String) populationExportData[1];
							}

							if (populationExportData[2] == null) {
								// Total population
								String sexString = (String) populationExportData[3];
								if (Sex.MALE.getName().equals(sexString)) {
									exportLine[3] = String.valueOf((int) populationExportData[4]);
								} else if (Sex.FEMALE.getName().equals(sexString)) {
									exportLine[4] = String.valueOf((int) populationExportData[4]);
								} else {
									exportLine[2] = String.valueOf((int) populationExportData[4]);
								}
							} else {
								// Population based on age group position and sex
								Integer ageGroupPosition = ageGroupPositions.get(AgeGroup.valueOf((String) populationExportData[2]));
								String sexString = (String) populationExportData[3];
								if (Sex.MALE.getName().equals(sexString)) {
									ageGroupPosition += 1;
								} else if (Sex.FEMALE.getName().equals(sexString)) {
									ageGroupPosition += 2;
								}
								exportLine[ageGroupPosition] = String.valueOf((int) populationExportData[4]);
							}
						}

						// Write last line to CSV
						writer.writeNext(exportLine);
						writer.flush();
					}
					return new ByteArrayInputStream(byteStream.toByteArray());
				} catch (IOException e) {
					// TODO This currently requires the user to click the "Export" button again or reload the page as the UI
					// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
					new Notification(
						I18nProperties.getString(Strings.headingExportFailed),
						I18nProperties.getString(Strings.messageExportFailed),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
					return null;
				}
			}
		}, exportFileName);
		populationDataStreamResource.setMIMEType("text/csv");
		populationDataStreamResource.setCacheTime(0);
		return populationDataStreamResource;
	}

	public static StreamResource createCaseManagementExportResource(CaseCriteria criteria, String exportFileName) {

		StreamResource casesResource = createCsvExportStreamResource(
			CaseExportDto.class,
			CaseExportType.CASE_MANAGEMENT,
			(Integer start, Integer max) -> FacadeProvider.getCaseFacade()
				.getExportList(criteria, CaseExportType.CASE_MANAGEMENT, start, max, null, I18nProperties.getUserLanguage()),
			(propertyId, type) -> {
				String caption = I18nProperties.getPrefixCaption(
					CaseExportDto.I18N_PREFIX,
					propertyId,
					I18nProperties.getPrefixCaption(
						CaseDataDto.I18N_PREFIX,
						propertyId,
						I18nProperties.getPrefixCaption(
							PersonDto.I18N_PREFIX,
							propertyId,
							I18nProperties.getPrefixCaption(
								SymptomsDto.I18N_PREFIX,
								propertyId,
								I18nProperties.getPrefixCaption(
									EpiDataDto.I18N_PREFIX,
									propertyId,
									I18nProperties.getPrefixCaption(
										HospitalizationDto.I18N_PREFIX,
										propertyId,
										I18nProperties.getPrefixCaption(HealthConditionsDto.I18N_PREFIX, propertyId)))))));
				if (Date.class.isAssignableFrom(type)) {
					caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
				}
				return caption;
			},
			"sormas_cases_" + DateHelper.formatDateForExport(new Date()) + ".csv",
			null);

		StreamResource prescriptionsResource = createCsvExportStreamResource(
			PrescriptionExportDto.class,
			null,
			(Integer start, Integer max) -> FacadeProvider.getPrescriptionFacade().getExportList(criteria, start, max),
			(propertyId, type) -> {
				String caption = I18nProperties.getPrefixCaption(
					PrescriptionExportDto.I18N_PREFIX,
					propertyId,
					I18nProperties.getPrefixCaption(PrescriptionDto.I18N_PREFIX, propertyId));
				if (Date.class.isAssignableFrom(type)) {
					caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
				}
				return caption;
			},
			"sormas_prescriptions_" + DateHelper.formatDateForExport(new Date()) + ".csv",
			null);

		StreamResource treatmentsResource = createCsvExportStreamResource(
			TreatmentExportDto.class,
			null,
			(Integer start, Integer max) -> FacadeProvider.getTreatmentFacade().getExportList(criteria, start, max),
			(propertyId, type) -> {
				String caption = I18nProperties.getPrefixCaption(
					TreatmentExportDto.I18N_PREFIX,
					propertyId,
					I18nProperties.getPrefixCaption(TreatmentDto.I18N_PREFIX, propertyId));
				if (Date.class.isAssignableFrom(type)) {
					caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
				}
				return caption;
			},
			"sormas_prescriptions_" + DateHelper.formatDateForExport(new Date()) + ".csv",
			null);

		StreamResource clinicalVisitsResource = createCsvExportStreamResource(
			ClinicalVisitExportDto.class,
			null,
			(Integer start, Integer max) -> FacadeProvider.getClinicalVisitFacade().getExportList(criteria, start, max),
			(propertyId, type) -> {
				String caption = I18nProperties.getPrefixCaption(
					ClinicalVisitExportDto.I18N_PREFIX,
					propertyId,
					I18nProperties.getPrefixCaption(
						ClinicalVisitDto.I18N_PREFIX,
						propertyId,
						I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, propertyId)));
				if (Date.class.isAssignableFrom(type)) {
					caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
				}
				return caption;
			},
			"sormas_clinical_assessments_" + DateHelper.formatDateForExport(new Date()) + ".csv",
			null);

		StreamResource caseManagementStreamResource = new StreamResource(() -> {
			String zipFile = FacadeProvider.getExportFacade()
				.generateZipArchive(DateHelper.formatDateForExport(new Date()), new Random().nextInt(Integer.MAX_VALUE));
			try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {
				writeCsvToZip(zos, casesResource.getStreamSource(), "cases.csv");
				writeCsvToZip(zos, prescriptionsResource.getStreamSource(), "prescriptions.csv");
				writeCsvToZip(zos, treatmentsResource.getStreamSource(), "treatments.csv");
				writeCsvToZip(zos, clinicalVisitsResource.getStreamSource(), "clinical_assessments.csv");
				zos.close();
				return new BufferedInputStream(new FileInputStream(new File(zipFile)));
			} catch (IOException e) {
				LoggerFactory.getLogger(DownloadUtil.class).error("Failed to generate a zip file for case management export.");
				return null;
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

	public static interface OutputStreamConsumer {

		void writeTo(OutputStream os) throws IOException;
	}

	/**
	 * The buffer can be used for an input stream without having to copy it
	 */
	private static class SharedByteArrayOutputStream extends ByteArrayOutputStream {

		public SharedByteArrayOutputStream() {
			super(2048);
		}

		public ByteArrayInputStream toInputStream() {
			return new ByteArrayInputStream(buf, 0, count);
		}
	}

	public static class DelayedInputStream extends FilterInputStream {

		private Supplier<InputStream> lazyInputStreamSupplier;

		protected DelayedInputStream(Supplier<InputStream> lazyInputStreamSupplier) {
			super(null);
			this.lazyInputStreamSupplier = lazyInputStreamSupplier;
		}

		protected DelayedInputStream(OutputStreamConsumer osConsumer, Consumer<IOException> exceptionHandler) {
			this(() -> {
				try (SharedByteArrayOutputStream os = new SharedByteArrayOutputStream()) {
					osConsumer.writeTo(os);
					return os.toInputStream();
				} catch (IOException e) {
					exceptionHandler.accept(e);
					throw new UncheckedIOException(e);
				}
			});
		}

		private void ensureInited() {
			if (lazyInputStreamSupplier != null) {
				in = lazyInputStreamSupplier.get();
				lazyInputStreamSupplier = null;
			}
		}

		@Override
		public int read() throws IOException {
			ensureInited();
			return super.read();
		}

		@Override
		public int read(byte[] b) throws IOException {
			ensureInited();
			return super.read(b);
		}

		@Override
		public synchronized int read(byte[] b, int off, int len) throws IOException {
			ensureInited();
			return super.read(b, off, len);
		}

		@Override
		public synchronized long skip(long n) throws IOException {
			ensureInited();
			return super.skip(n);
		}

		@Override
		public synchronized int available() throws IOException {
			ensureInited();
			return super.available();
		}

		@Override
		public synchronized void mark(int readAheadLimit) {
			ensureInited();
			super.mark(readAheadLimit);
		}
	}

	public static StreamResource createVisitsExportStreamResource(ContactCriteria contactCriteria, final String exportFileName) {

		StreamResource extendedStreamResource = new StreamResource(() -> new DelayedInputStream((out) -> {
			try (CSVWriter writer = CSVUtils
				.createCSVWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8.name()), FacadeProvider.getConfigFacade().getCsvSeparator())) {

				final List<String> columnNames = new ArrayList<>();
				final List<String> dayColumns = new ArrayList<>();
				columnNames.add(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.UUID));
				columnNames.add(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
				columnNames.add(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
				dayColumns.add("");
				dayColumns.add("");
				dayColumns.add("");

				final long maximumFollowUps = FacadeProvider.getContactFacade().countMaximumFollowUpDays(contactCriteria);

				for (int index = 0; index < maximumFollowUps; index++) {
					columnNames.add(I18nProperties.getPrefixCaption(VisitDto.I18N_PREFIX, VisitDto.VISIT_DATE_TIME));
					columnNames.add(I18nProperties.getPrefixCaption(VisitDto.I18N_PREFIX, VisitDto.VISIT_STATUS));
					columnNames.add(I18nProperties.getPrefixCaption(VisitDto.I18N_PREFIX, VisitDto.SYMPTOMS));
					final String dayString = I18nProperties.getCaption(Captions.contactFollowUpDay) + " " + (index + 1);

					dayColumns.add(dayString);
					dayColumns.add(dayString);
					dayColumns.add(dayString);
				}

				writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
				writer.writeNext(dayColumns.toArray(new String[columnNames.size()]));

				int startIndex = 0;
				List<VisitSummaryExportDto> exportRows = FacadeProvider.getContactFacade()
					.getVisitSummaryExportList(contactCriteria, 0, DETAILED_EXPORT_STEP_SIZE, I18nProperties.getUserLanguage());
				while (!exportRows.isEmpty()) {

					for (VisitSummaryExportDto exportRow : exportRows) {
						final List<String> values = new ArrayList<>();
						values.add(exportRow.getUuid());
						values.add(exportRow.getFirstName());
						values.add(exportRow.getLastName());
						exportRow.getVisitDetails().forEach(contactVisitsDetailsExportDto -> {
							values.add(DateFormatHelper.formatDate(contactVisitsDetailsExportDto.getVisitDateTime()));
							values.add(contactVisitsDetailsExportDto.getVisitStatus().toString());
							values.add(contactVisitsDetailsExportDto.getSymptoms());
						});

						writer.writeNext(values.toArray(new String[columnNames.size()]));
					}

					writer.flush();
					startIndex += DETAILED_EXPORT_STEP_SIZE;
					exportRows = FacadeProvider.getContactFacade()
						.getVisitSummaryExportList(contactCriteria, startIndex, DETAILED_EXPORT_STEP_SIZE, I18nProperties.getUserLanguage());
				}
			}
		},
			e -> {
				// TODO This currently requires the user to click the "Export" button again or reload the page
				//  as the UI
				// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
				VaadinSession.getCurrent()
					.access(
						() -> new Notification(
							I18nProperties.getString(Strings.headingExportFailed),
							I18nProperties.getString(Strings.messageExportFailed),
							Type.ERROR_MESSAGE,
							false).show(Page.getCurrent()));
			}), exportFileName);
		extendedStreamResource.setMIMEType("text/csv");
		extendedStreamResource.setCacheTime(0);
		return extendedStreamResource;
	}

	public static <T> StreamResource createCsvExportStreamResource(
		Class<T> exportRowClass,
		Enum<?> exportType,
		BiFunction<Integer, Integer, List<T>> exportRowsSupplier,
		BiFunction<String, Class<?>, String> propertyIdCaptionFunction,
		String exportFileName,
		ExportConfigurationDto exportConfiguration) {


		CountryFieldVisibilityChecker countryFieldVisibilityChecker = new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale());
		StreamResource extendedStreamResource = new StreamResource(() -> {

			return new DelayedInputStream((out) -> {
				try (CSVWriter writer = CSVUtils.createCSVWriter(
					new OutputStreamWriter(out, StandardCharsets.UTF_8.name()),
					FacadeProvider.getConfigFacade().getCsvSeparator())) {

					// 1. fields in order of declaration - not using Introspector here, because it gives properties in alphabetical order
					List<Method> readMethods = new ArrayList<Method>();
					readMethods.addAll(
						Arrays.stream(exportRowClass.getDeclaredMethods())
							.filter(
								m -> (m.getName().startsWith("get") || m.getName().startsWith("is"))
									&& m.isAnnotationPresent(Order.class)
									&& (countryFieldVisibilityChecker.isVisible(m))
									&& (exportType == null || hasExportTarget(exportType, m))
									&& (exportConfiguration == null
										|| exportConfiguration.getProperties().contains(m.getAnnotation(ExportProperty.class).value())))
							.sorted(Comparator.comparingInt(a -> a.getAnnotationsByType(Order.class)[0].value()))
							.collect(Collectors.toList()));

					// 2. replace entity fields with all the columns of the entity
					Map<Method, Function<T, ?>> subEntityProviders = new HashMap<Method, Function<T, ?>>();
					for (int i = 0; i < readMethods.size(); i++) {
						Method method = readMethods.get(i);
						if (EntityDto.class.isAssignableFrom(method.getReturnType())) {

							// allows us to access the sub entity
							Function<T, ?> subEntityProvider = o -> {
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
								.sorted(Comparator.comparingInt(a2 -> a2.getAnnotationsByType(Order.class)[0].value()))
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
						final Method method = readMethods.get(i);
						// field caption
						String propertyId = method.getName().startsWith("get") ? method.getName().substring(3) : method.getName().substring(2);
						if (method.isAnnotationPresent(ExportProperty.class)) {
							// TODO not sure why we are using the export property name to get the caption here
							final ExportProperty exportProperty = method.getAnnotation(ExportProperty.class);
							if (!exportProperty.combined()) {
								propertyId = exportProperty.value();
							}
						}
						propertyId = Character.toLowerCase(propertyId.charAt(0)) + propertyId.substring(1);
						fieldValues[i] = propertyIdCaptionFunction.apply(propertyId, method.getReturnType());
					}
					writer.writeNext(fieldValues);

					int startIndex = 0;
					List<T> exportRows = exportRowsSupplier.apply(startIndex, DETAILED_EXPORT_STEP_SIZE);
					Language userLanguage = I18nProperties.getUserLanguage();
					while (!exportRows.isEmpty()) {
						try {
							for (T exportRow : exportRows) {
								for (int i = 0; i < readMethods.size(); i++) {
									Method method = readMethods.get(i);
									Function<T, ?> subEntityProvider = subEntityProviders.getOrDefault(method, null);
									Object entity = subEntityProvider != null ? subEntityProvider.apply(exportRow) : exportRow;
									// Sub entity might be null
									Object value = entity != null ? method.invoke(entity) : null;
									if (value == null) {
										fieldValues[i] = "";
									} else if (value instanceof Date) {
										fieldValues[i] = DateFormatHelper.formatDate((Date) value);
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
									} else if (value instanceof BurialInfoDto) {
										fieldValues[i] = PersonHelper.buildBurialInfoString((BurialInfoDto) value, userLanguage);
									} else if (value instanceof AgeAndBirthDateDto) {
										AgeAndBirthDateDto ageAndBirthDate = (AgeAndBirthDateDto) value;
										fieldValues[i] = PersonHelper.getAgeAndBirthdateString(
											ageAndBirthDate.getAge(),
											ageAndBirthDate.getAgeType(),
											ageAndBirthDate.getBirthdateDD(),
											ageAndBirthDate.getBirthdateMM(),
											ageAndBirthDate.getBirthdateYYYY(),
											userLanguage);
									} else if (value instanceof BirthDateDto) {
										BirthDateDto birthDate = (BirthDateDto) value;
										fieldValues[i] = PersonHelper.formatBirthdate(
											birthDate.getBirthdateDD(),
											birthDate.getBirthdateMM(),
											birthDate.getBirthdateYYYY(),
											userLanguage);
									} else {
										fieldValues[i] = value.toString();
									}
								}
								writer.writeNext(fieldValues);
							} ;
						} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
							throw new RuntimeException(e);
						}

						writer.flush();
						startIndex += DETAILED_EXPORT_STEP_SIZE;
						exportRows = exportRowsSupplier.apply(startIndex, DETAILED_EXPORT_STEP_SIZE);
					}
				}
			},
				e -> {
					// TODO This currently requires the user to click the "Export" button again or reload the page
					//  as the UI
					// is not automatically updated; this should be changed once Vaadin push is enabled (see #516)
					VaadinSession.getCurrent()
						.access(
							() -> new Notification(
								I18nProperties.getString(Strings.headingExportFailed),
								I18nProperties.getString(Strings.messageExportFailed),
								Type.ERROR_MESSAGE,
								false).show(Page.getCurrent()));
				});
		}, exportFileName);
		extendedStreamResource.setMIMEType("text/csv");
		extendedStreamResource.setCacheTime(0);
		return extendedStreamResource;
	}

	@SuppressWarnings("rawtypes")
	private static boolean hasExportTarget(Enum<?> exportType, Method m) {

		if (m.isAnnotationPresent(ExportTarget.class)) {
			final Class<? extends Enum> exportTypeClass = exportType.getClass();
			final ExportTarget exportTarget = m.getAnnotation(ExportTarget.class);
			Supplier<Enum[]> exportTypeSupplier = null;
			if (exportTypeClass.isAssignableFrom(CaseExportType.class)) {
				exportTypeSupplier = exportTarget::caseExportTypes;
			}
			if (exportTypeClass.isAssignableFrom(VisitExportType.class)) {
				exportTypeSupplier = exportTarget::visitExportTypes;

			}
			return exportTypeSupplier == null ? false : containsExportType(exportType, exportTypeSupplier);
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private static boolean containsExportType(Enum<?> exportType, Supplier<Enum[]> supplier) {
		return Arrays.asList(supplier.get()).contains(exportType);
	}

	/**
	 * <p>
	 * When downloading a Resource via FileDownloader,
	 * the Component of the FileDownloader must remain visible in the UI.
	 * Otherwise the Resource is unregistered and the download may fail.
	 * </p>
	 * <p>
	 * This method display a modal dialog that includes the exportComponent without actually showing it to the user.
	 * When the dialog is closed, it up to the closeListener to decide the fate of the exportComponent.
	 * </p>
	 *
	 * @param exportButton
	 * @param closeListener
	 */
	public static void showExportWaitDialog(AbstractComponent exportComponent, CloseListener closeListener) {

		//the button has to remain in the UI for the download to succeed, but it should not be seen 
		CustomLayout hidingLayout = new CustomLayout();
		hidingLayout.setSizeUndefined();
		hidingLayout.setTemplateContents("");
		hidingLayout.addComponent(exportComponent);

		Label lbl = new Label(I18nProperties.getString(Strings.infoDownloadExport), ContentMode.HTML);
		HorizontalLayout layout = new HorizontalLayout(lbl, hidingLayout);
		layout.setMargin(true);
		layout.setExpandRatio(lbl, 1);
		Window dialog = VaadinUiUtil.showPopupWindow(layout);
		dialog.setCaption(exportComponent.getCaption());

		dialog.addCloseListener(closeListener);
	}
}
