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
package de.symeda.sormas.api.importexport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.event.EventExportDto;
import de.symeda.sormas.api.event.EventParticipantExportDto;
import de.symeda.sormas.api.person.PersonExportDto;
import de.symeda.sormas.api.task.TaskExportDto;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;

public final class ImportExportUtils {

	public static final String TEMP_FILE_PREFIX = "sormas_temp";

	private ImportExportUtils() {
		// Hide Utility Class Constructor
	}

	public static List<ExportPropertyMetaInfo> getCaseExportProperties(
		PropertyCaptionProvider propertyCaptionProvider,
		final boolean withFollowUp,
		final boolean withClinicalCourse,
		final boolean withTherapy,
		String countryLocale) {
		return getExportProperties(CaseExportDto.class, new PropertyTypeFilter() {

			@Override
			public boolean accept(ExportGroupType groupType) {
				if (ExportGroupType.CLINICAL_COURSE == groupType && !withClinicalCourse) {
					return false;
				} else if (ExportGroupType.THERAPY == groupType && !withTherapy) {
					return false;
				}

				return ExportGroupType.FOLLOW_UP != groupType || withFollowUp;
			}
		}, propertyCaptionProvider, countryLocale);
	}

	public static List<ExportPropertyMetaInfo> getEventExportProperties(
		PropertyCaptionProvider propertyCaptionProvider,
		final boolean withEventGroups,
		String countryLocale) {
		return getExportProperties(EventExportDto.class, new PropertyTypeFilter() {

			@Override
			public boolean accept(ExportGroupType groupType) {
				return ExportGroupType.EVENT_GROUP != groupType || withEventGroups;
			}
		}, propertyCaptionProvider, countryLocale);
	}

	public static List<ExportPropertyMetaInfo> getContactExportProperties(PropertyCaptionProvider propertyCaptionProvider, String countryLocale) {
		return getExportProperties(ContactExportDto.class, new PropertyTypeFilter() {

			@Override
			public boolean accept(ExportGroupType type) {
				return true;
			}
		}, propertyCaptionProvider, countryLocale);
	}

	public static List<ExportPropertyMetaInfo> getEventParticipantExportProperties(
		PropertyCaptionProvider propertyCaptionProvider,
		String countryLocale) {
		return getExportProperties(EventParticipantExportDto.class, new PropertyTypeFilter() {

			@Override
			public boolean accept(ExportGroupType type) {
				return true;
			}
		}, propertyCaptionProvider, countryLocale);
	}

	public static List<ExportPropertyMetaInfo> getTaskExportProperties(PropertyCaptionProvider propertyCaptionProvider, String countryLocale) {
		return getExportProperties(TaskExportDto.class, new PropertyTypeFilter() {

			@Override
			public boolean accept(ExportGroupType type) {
				return true;
			}
		}, propertyCaptionProvider, countryLocale);
	}

	public static List<ExportPropertyMetaInfo> getPersonExportProperties(PropertyCaptionProvider propertyCaptionProvider, String countryLocale) {
		return getExportProperties(PersonExportDto.class, new PropertyTypeFilter() {

			@Override
			public boolean accept(ExportGroupType type) {
				return true;
			}
		}, propertyCaptionProvider, countryLocale);
	}

	private static List<ExportPropertyMetaInfo> getExportProperties(
		Class<?> exportDtoClass,
		PropertyTypeFilter filterExportGroup,
		PropertyCaptionProvider propertyCaptionProvider,
		String countryLocale) {
		List<Method> readMethods = new ArrayList<>();
		for (Method method : exportDtoClass.getDeclaredMethods()) {
			if ((!method.getName().startsWith("get") && !method.getName().startsWith("is")) || !method.isAnnotationPresent(ExportGroup.class)) {
				continue;
			}
			readMethods.add(method);
		}
		Collections.sort(readMethods, new Comparator<Method>() {

			@Override
			public int compare(Method m1, Method m2) {
				return Integer.compare(getOrderValue(m1), getOrderValue(m2));
			}
		});

		CountryFieldVisibilityChecker countryFieldVisibilityChecker = new CountryFieldVisibilityChecker(countryLocale);
		Set<String> combinedProperties = new HashSet<>();
		List<ExportPropertyMetaInfo> properties = new ArrayList<>();
		for (Method method : readMethods) {

			if (!countryFieldVisibilityChecker.isVisible(method)) {
				continue;
			}

			ExportGroupType groupType = method.getAnnotation(ExportGroup.class).value();

			if (!filterExportGroup.accept(groupType)) {
				continue;
			}

			String[] propertyPath = method.getAnnotation(ExportProperty.class).value();
			String property = StringUtils.join(propertyPath, ".");
			if (method.getAnnotation(ExportProperty.class).combined()) {
				if (!combinedProperties.add(property)) {
					continue;
				}
			}

			// prepare ExportPropertyMetaInfo
			// In order to get the correct caption, we try to fetch the i18n-prefix of the methods declaring class
			String i18n_prefix = null;
			ExportEntity MethodClassEntity = method.getAnnotation(ExportEntity.class);
			if (MethodClassEntity != null) {
				try {
					i18n_prefix = (String) MethodClassEntity.value().getDeclaredField("I18N_PREFIX").get(null);
				} catch (NoSuchFieldException | IllegalAccessException ex) {
					// Field doesn't exist or is private
				}
			}
			properties.add(
				new ExportPropertyMetaInfo(property, propertyCaptionProvider.get(propertyPath[propertyPath.length - 1], i18n_prefix), groupType));

		}

		return properties;
	}

	private static int getOrderValue(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType() == Order.class) {
				return ((Order) annotation).value();
			}
		}
		// XXX throw an exception ?
		return -1;
	}

	public static char getCSVSeparatorDifferentFromCurrent(char currentSeparator) {
		char separator = ',';
		if (CharUtils.compare(',', currentSeparator) == 0) {
			separator = ';';
		}
		return separator;
	}

	public interface PropertyTypeFilter {

		boolean accept(ExportGroupType type);
	}

	public interface PropertyNameFilter {

		boolean accept(String name);
	}

	public interface PropertyCaptionProvider {

		String get(String propertyId, String prefixId);
	}
}
