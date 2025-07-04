/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestType;

public final class AnnotationFieldHelper {

	private AnnotationFieldHelper() {
	}

	public static List<String> getFieldNamesWithMatchingDiseaseAndTestAnnotations(
		Class<?> clazz,
		List<Disease> diseases,
		List<PathogenTestType> pathogenTestTypes) {

		if (clazz == null) {
			return new ArrayList<>();
		}

		List<String> matchingFieldNames = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			boolean diseaseMatches = false;
			boolean pathogenTestMatches = false;

			// Check @Diseases annotation
			if (diseases != null && !diseases.isEmpty()) {
				Diseases diseasesAnnotation = field.getAnnotation(Diseases.class);
				if (diseasesAnnotation != null) {
					List<Disease> fieldDiseases = Arrays.asList(diseasesAnnotation.value());
					if (fieldDiseases.stream().anyMatch(diseases::contains)) {
						diseaseMatches = true;
					}
				}
			}

			// Check @ApplicableToPathogenTests annotation
			if (diseaseMatches && pathogenTestTypes != null && !pathogenTestTypes.isEmpty()) {
				ApplicableToPathogenTests pathogenTestsAnnotation = field.getAnnotation(ApplicableToPathogenTests.class);
				if (pathogenTestsAnnotation != null) {
					List<PathogenTestType> fieldTestTypes = Arrays.asList(pathogenTestsAnnotation.value());
					if (fieldTestTypes.stream().anyMatch(pathogenTestTypes::contains)) {
						pathogenTestMatches = true;
					}
				}
			}

			if (diseaseMatches && pathogenTestMatches) {
				matchingFieldNames.add(field.getName());
			}
		}

		return matchingFieldNames;
	}

	public static List<String> getFieldNamesWithMatchingDiseaseAndTestAnnotations(
		Class<?> clazz,
		Disease disease,
		PathogenTestType pathogenTestType) {

		List<Disease> diseases = disease != null ? Arrays.asList(disease) : null;
		List<PathogenTestType> pathogenTestTypes = pathogenTestType != null ? Arrays.asList(pathogenTestType) : null;

		return getFieldNamesWithMatchingDiseaseAndTestAnnotations(clazz, diseases, pathogenTestTypes);
	}

	public static List<String> getFieldNamesWithDiseases(Class<?> clazz, List<Disease> diseases) {
		return getFieldNamesWithMatchingDiseaseAndTestAnnotations(clazz, diseases, null);
	}

	public static List<String> getFieldNamesWithPathogenTestTypes(Class<?> clazz, List<PathogenTestType> pathogenTestTypes) {
		return getFieldNamesWithMatchingDiseaseAndTestAnnotations(clazz, null, pathogenTestTypes);
	}
}
