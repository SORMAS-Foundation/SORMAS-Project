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
import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportEntity;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;

public class CsvStreamUtils {

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
					SubEntityProvider<T> subEntityProvider = createSubEntityProvider(method);

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

			Class<?> entityClass = null;
			if (csvRowClass.isAnnotationPresent(ExportEntity.class)) {
				entityClass = csvRowClass.getAnnotation(ExportEntity.class).value();
			}
			String[] fieldClassNames = new String[readMethods.size()];
			String[] fieldIds = new String[readMethods.size()];
			String[] labels = new String[readMethods.size()];
			for (int i = 0; i < readMethods.size(); i++) {
				final Method method = readMethods.get(i);
				String fieldName = getFieldNameFromMethod(method);

				String propertyId = fieldName;

				if (method.isAnnotationPresent(ExportProperty.class)) {
					final ExportProperty exportProperty = method.getAnnotation(ExportProperty.class);
					if (!exportProperty.combined()) {
						propertyId = StringUtils.join(exportProperty.value(), ".");
					}
				}

				Class<?> fieldEntityClass = entityClass;
				if (method.isAnnotationPresent(ExportEntity.class)) {
					fieldEntityClass = method.getAnnotation(ExportEntity.class).value();
				}

				if (subEntityProviders.containsKey(method)) {
					fieldEntityClass = subEntityProviders.get(method).getEntityClass();
					propertyId = subEntityProviders.get(method).getName() + "." + propertyId;
				}

				if (fieldEntityClass != null) {
					fieldClassNames[i] = DataHelper.getHumanClassName(fieldEntityClass);
				}
				fieldIds[i] = propertyId;
				labels[i] = propertyIdCaptionSupplier.apply(fieldName, method.getReturnType());
			}

			if (entityClass != null) {
				writer.writeNext(fieldClassNames);
			}
			writer.writeNext(fieldIds);
			labels[0] = CSVCommentLineValidator.DEFAULT_COMMENT_LINE_PREFIX + labels[0];
			writer.writeNext(labels, false);

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

							labels[i] = DataHelper.valueToString(value);
						}
						writer.writeNext(labels);
					} ;
				} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (Exception e) {
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

	private static String getFieldNameFromMethod(Method method) {
		String propertyId = method.getName().startsWith("get") ? method.getName().substring(3) : method.getName().substring(2);
		propertyId = Character.toLowerCase(propertyId.charAt(0)) + propertyId.substring(1);
		return propertyId;
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

		return exportConfiguration.getProperties().contains(StringUtils.join(exportProperty.value(), "."));
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

		String getName();

		Class<?> getEntityClass();

		Object get(T parent);
	}

	private static <T> SubEntityProvider<T> createSubEntityProvider(final Method getter) {
		return new SubEntityProvider<T>() {

			@Override
			public String getName() {
				return getFieldNameFromMethod(getter);
			}

			@Override
			public Class<?> getEntityClass() {
				return getter.getReturnType();
			}

			@Override
			public Object get(T parent) {
				try {
					return getter.invoke(parent);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
