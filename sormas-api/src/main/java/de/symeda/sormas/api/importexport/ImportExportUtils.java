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

import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.Order;

public final class ImportExportUtils {

	private ImportExportUtils() {
		// Hide Utility Class Constructor
	}

	public static final String FILE_PREFIX = "sormas";
	public static final String TEMP_FILE_PREFIX = "sormas_temp";

	public static List<Pair<String, ExportGroupType>> getCaseExportProperties(boolean withFollowUp, boolean withCaseManagement) {
		List<Method> readMethods = new ArrayList<>();
		for (Method method : CaseExportDto.class.getDeclaredMethods()) {
			if ((!method.getName().startsWith("get") && !method.getName().startsWith("is")) || !method.isAnnotationPresent(ExportGroup.class)) {
				continue;
			}
			readMethods.add(method);
		}
		Collections.sort(readMethods, new Comparator<Method>() {
			@Override public int compare(Method m1, Method m2) {
				return Integer.compare(getOrderValue(m1), getOrderValue(m2));
			}
		});

		Set<String> combinedProperties = new HashSet<>();
		List<Pair<String, ExportGroupType>> properties = new ArrayList<>();
		for (Method method : readMethods) {
			ExportGroupType groupType = method.getAnnotation(ExportGroup.class).value();

			if (ExportGroupType.CASE_MANAGEMENT == groupType && !withCaseManagement) {
				continue;
			}
			if (ExportGroupType.FOLLOW_UP == groupType && !withFollowUp) {
				continue;
			}
			String property = method.getAnnotation(ExportProperty.class).value();
			if (method.getAnnotation(ExportProperty.class).combined()) {
				if (!combinedProperties.add(property)) {
					continue;
				}
			}
			properties.add(Pair.createPair(property, groupType));
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
}
