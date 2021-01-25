/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.opencsv.CSVWriter;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportStreamUtils {

	public static final int STEP_SIZE = 50;

	public static <T> void writeCsvContentToStream(
			Class<T> csvRowClass,
			SupplierBiFunction<Integer, Integer, List<T>> exportRowsSupplier,
			SupplierBiFunction<String, Class<?>, String> propertyIdCaptionSupplier,
			ExportConfigurationDto exportConfiguration,
			final Predicate redMethodFilter,
			ConfigFacade configFacade,
			OutputStream out) {

		try (
				CSVWriter writer = CSVUtils.createCSVWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8.name()), configFacade.getCsvSeparator())) {

			// 1. fields in order of declaration - not using Introspector here, because it gives properties in alphabetical order
			List<Method> readMethods =
					getExportRowClassReadMethods(csvRowClass, exportConfiguration, redMethodFilter, configFacade.getCountryLocale());

			// 2. replace entity fields with all the columns of the entity
			Map<Method, SubEntityProvider<T>> subEntityProviders = new HashMap<Method, SubEntityProvider<T>>();
			for (int i = 0; i < readMethods.size(); i++) {
				final Method method = readMethods.get(i);
				if (EntityDto.class.isAssignableFrom(method.getReturnType())) {

					// allows us to access the sub entity
					SubEntityProvider<T> subEntityProvider = new SubEntityProvider<T>() {

						@Override
						public Object get(T o) {
							try {
								return method.invoke(o);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								throw new RuntimeException(e);
							}
						}
					};

					// remove entity field
					readMethods.remove(i);

					// add columns of the entity
					List<Method> subReadMethods = getReadMethods(method.getReturnType(), null);
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
				fieldValues[i] = propertyIdCaptionSupplier.apply(propertyId, method.getReturnType());
			}
			writer.writeNext(fieldValues);

			int startIndex = 0;
			List<T> exportRows = exportRowsSupplier.apply(startIndex, STEP_SIZE);
			while (!exportRows.isEmpty()) {
				try {
					for (T exportRow : exportRows) {
						for (int i = 0; i < readMethods.size(); i++) {
							Method method = readMethods.get(i);
							SubEntityProvider<T> subEntityProvider = subEntityProviders.get(method);
							Object entity = subEntityProvider != null ? subEntityProvider.get(exportRow) : exportRow;
							// Sub entity might be null
							Object value = entity != null ? method.invoke(entity) : null;

							fieldValues[i] = DataHelper.valueToString(value);
						}
						writer.writeNext(fieldValues);
					} ;
				} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
					throw new RuntimeException(e);
				}

				writer.flush();
				startIndex += STEP_SIZE;
				exportRows = exportRowsSupplier.apply(startIndex, STEP_SIZE);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void writeXslxContentToStream(
			Class<T> csvRowClass,
			SupplierBiFunction<Integer, Integer, List<T>> exportRowsSupplier,
			SupplierBiFunction<String, Class<?>, String> propertyIdCaptionSupplier,
			ExportConfigurationDto exportConfiguration,
			final Predicate redMethodFilter,
			ConfigFacade configFacade,
			OutputStream out) {

		try {
			SXSSFWorkbook workbook = new SXSSFWorkbook(STEP_SIZE);
			SXSSFSheet sheet = workbook.createSheet();
			// write header rows
			SXSSFRow xssfRow = sheet.createRow(0);

			// 1. fields in order of declaration - not using Introspector here, because it gives properties in alphabetical order
			List<Method> readMethods =
					getExportRowClassReadMethods(csvRowClass, exportConfiguration, redMethodFilter, configFacade.getCountryLocale());

			// 2. replace entity fields with all the columns of the entity
			Map<Method, SubEntityProvider<T>> subEntityProviders = new HashMap<Method, SubEntityProvider<T>>();
			for (int i = 0; i < readMethods.size(); i++) {
				final Method method = readMethods.get(i);
				if (EntityDto.class.isAssignableFrom(method.getReturnType())) {

					// allows us to access the sub entity
					SubEntityProvider<T> subEntityProvider = new SubEntityProvider<T>() {

						@Override
						public Object get(T o) {
							try {
								return method.invoke(o);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								throw new RuntimeException(e);
							}
						}
					};

					// remove entity field
					readMethods.remove(i);

					// add columns of the entity
					List<Method> subReadMethods = getReadMethods(method.getReturnType(), null);
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
				fieldValues[i] = propertyIdCaptionSupplier.apply(propertyId, method.getReturnType());

				xssfRow.createCell(i).setCellValue(fieldValues[i]); // TODO use Date, Boolean etc
			}

			int rowNumber = 1;
			int startIndex = 0;
			List<T> exportRows = exportRowsSupplier.apply(startIndex, STEP_SIZE);
			while (!exportRows.isEmpty()) {
				try {
					for (T exportRow : exportRows) {
						for (int i = 0; i < readMethods.size(); i++) {
							Method method = readMethods.get(i);
							SubEntityProvider<T> subEntityProvider = subEntityProviders.get(method);
							Object entity = subEntityProvider != null ? subEntityProvider.get(exportRow) : exportRow;
							// Sub entity might be null
							Object value = entity != null ? method.invoke(entity) : null;

							fieldValues[i] = DataHelper.valueToString(value);
						}
						xssfRow = sheet.createRow(rowNumber);
						for (int i = 0; i < fieldValues.length; i++) {
							xssfRow.createCell(i).setCellValue(fieldValues[i]);
						}
					}
				} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
					throw new RuntimeException(e);
				}

				workbook.write(out);
				out.flush();
				startIndex += STEP_SIZE;
				exportRows = exportRowsSupplier.apply(startIndex, STEP_SIZE);
			}
			workbook.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> List<Method> getExportRowClassReadMethods(
		Class<T> exportRowClass,
		final ExportConfigurationDto exportConfiguration,
		final Predicate redMethodFilter,
		String countryLocale) {
		final CountryFieldVisibilityChecker countryFieldVisibilityChecker = new CountryFieldVisibilityChecker(countryLocale);

		return getReadMethods(exportRowClass, new Predicate() {

			@Override
			public boolean evaluate(Object o) {
				Method m = (Method) o;

				return (countryFieldVisibilityChecker.isVisible(m))
					&& (redMethodFilter == null || redMethodFilter.evaluate(o))
					&& (exportConfiguration == null || isConfiguredForExport(m, exportConfiguration));
			}
		});
	}

	private static boolean isConfiguredForExport(Method m, ExportConfigurationDto exportConfiguration) {
		ExportProperty exportProperty = m.getAnnotation(ExportProperty.class);

		if (exportProperty == null) {
			throw new RuntimeException("Missing @ExportProperty annotation on method [" + m.getName() + "]");
		}

		return exportConfiguration.getProperties().contains(exportProperty.value());
	}

	private static List<Method> getReadMethods(Class<?> clazz, final Predicate filters) {
		ArrayList<Method> readMethods = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));

		CollectionUtils.filter(readMethods, new Predicate() {

			@Override
			public boolean evaluate(Object o) {
				Method m = (Method) o;
				return (m.getName().startsWith("get") || m.getName().startsWith("is"))
					&& m.isAnnotationPresent(Order.class)
					&& (filters == null || filters.evaluate(o));
			}
		});
		Collections.sort(readMethods, new Comparator<Method>() {

			@Override
			public int compare(Method m1, Method m2) {
				int o1 = m1.getAnnotation(Order.class).value();
				int o2 = m2.getAnnotation(Order.class).value();

				return o1 - o2;
			}
		});

		return readMethods;
	}

	public interface SupplierBiFunction<T, U, R> {

		R apply(T t, U u);
	}

	private interface SubEntityProvider<T> {

		Object get(T parent);
	}
}
