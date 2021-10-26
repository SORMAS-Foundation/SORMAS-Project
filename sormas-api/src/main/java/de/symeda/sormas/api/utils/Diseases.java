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
package de.symeda.sormas.api.utils;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;

/**
 * Used to annotate for which diseases a field or enum value is visible.
 * Use the code generated in sormas-api/tools/Fields.xlsx to get annotated members.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Diseases {

	Disease[] value() default {};

	/**
	 * If true, the field or enum value is supposed to be hidden for all diseases that are specified in
	 * {@link Diseases#value()}, and it will be visible for all other diseases.
	 */
	boolean hide() default false;

	final class DiseasesConfiguration {

		private DiseasesConfiguration() {
			// Hide Utility Class Constructor
		}

		private static final HashMap<Class<?>, HashMap<String, List<Disease>>> diseaseConfigCache = new HashMap<>();

		public static boolean isDefinedOrMissing(Class<?> clazz, String propertyName, Disease disease) {

			if (isMissing(clazz, propertyName)) {
				return true;
			}

			return isDefined(clazz, propertyName, disease);
		}

		public static boolean isMissing(Class<?> clazz, String propertyName) {

			if (!diseaseConfigCache.containsKey(clazz)) {
				readDiseaseConfig(clazz);
			}

			HashMap<String, List<Disease>> diseaseConfig = diseaseConfigCache.get(clazz);

			return !diseaseConfig.containsKey(propertyName);
		}

		public static boolean isDefined(Class<?> clazz, String propertyName, Disease disease) {

			if (!diseaseConfigCache.containsKey(clazz)) {
				readDiseaseConfig(clazz);
			}

			HashMap<String, List<Disease>> diseaseConfig = diseaseConfigCache.get(clazz);
			if (!diseaseConfig.containsKey(propertyName)) {
				// missing
				return false;
			}

			// defined?
			return diseaseConfig.get(propertyName).contains(disease);
		}

		private static synchronized void readDiseaseConfig(Class<?> clazz) {

			HashMap<String, List<Disease>> diseaseConfig = new HashMap<>();

			for (Field field : clazz.getDeclaredFields()) {

				Annotation[] annotations = field.getDeclaredAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation instanceof Diseases) {
						List<Disease> diseases;
						if (((Diseases) annotation).hide()) {
							List<Disease> diseasesList = Arrays.asList(((Diseases) annotation).value());
							diseases = Arrays.stream(Disease.values()).filter(d -> !diseasesList.contains(d)).collect(Collectors.toList());
							diseases.add(null);
						} else {
							diseases = Arrays.asList(((Diseases) annotation).value());
						}
						diseaseConfig.put(field.getName(), diseases);
						break;
					}
				}
			}

			diseaseConfigCache.put(clazz, diseaseConfig);
		}
	}
}
