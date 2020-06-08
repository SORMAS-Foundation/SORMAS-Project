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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Used to annotate on which other field a certain field is dependant
 * 
 * @author Martin Wahnschaffe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DependantOn {

	String value();

	public static final class DependencyConfiguration {

		private DependencyConfiguration() {
			// Hide Utility Class Constructor
		}

		// TODO thread safety, etc.?!
		private static final HashMap<Class<?>, HashMap<String, String>> parentsCache = new HashMap<>();
		private static final HashMap<Class<?>, HashMap<String, List<String>>> childrenCache = new HashMap<>();

		public static String getParent(Class<?> clazz, String propertyName) {

			if (!parentsCache.containsKey(clazz)) {
				readDependencyConfig(clazz);
			}

			HashMap<String, String> parentsMap = parentsCache.get(clazz);
			if (parentsMap.containsKey(propertyName)) {
				return parentsMap.get(propertyName);
			}

			return null;
		}

		public static List<String> getChildren(Class<?> clazz, String propertyName) {

			if (!childrenCache.containsKey(clazz)) {
				readDependencyConfig(clazz);
			}

			HashMap<String, List<String>> childrenMap = childrenCache.get(clazz);
			if (childrenMap.containsKey(propertyName)) {
				return childrenMap.get(propertyName);
			}

			return Collections.emptyList();
		}

		private static synchronized void readDependencyConfig(Class<?> clazz) {

			HashMap<String, String> parentsMap = new HashMap<>();
			HashMap<String, List<String>> childrenMap = new HashMap<>();

			for (Field field : clazz.getDeclaredFields()) {

				Annotation[] annotations = field.getDeclaredAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation instanceof DependantOn) {
						String parent = ((DependantOn) annotation).value();
						parentsMap.put(field.getName(), parent);
						if (!childrenMap.containsKey(parent)) {
							childrenMap.put(parent, new ArrayList<String>());
						}
						childrenMap.get(parent).add(field.getName());
						break;
					}
				}
			}

			// TODO read only lists
			parentsCache.put(clazz, parentsMap);
			childrenCache.put(clazz, childrenMap);
		}
	}
}
