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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used to annotate which fields are visible for outbreak situations.
 * 
 * @author Maté Strysewske
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Outbreaks {

	public static final class OutbreaksConfiguration {

		private OutbreaksConfiguration() {
			// Hide Utility Class Constructor
		}

		private static final HashMap<Class<?>, List<String>> classConfigCache = new HashMap<Class<?>, List<String>>();

		public static boolean isDefined(Class<?> clazz, String propertyName) {

			if (!classConfigCache.containsKey(clazz)) {
				readClassConfig(clazz);
			}

			List<String> classConfig = classConfigCache.get(clazz);
			return classConfig.contains(propertyName);
		}

		private static synchronized void readClassConfig(Class<?> clazz) {

			List<String> classConfig = new ArrayList<String>();

			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(Outbreaks.class)) {
					classConfig.add(field.getName());
				}
			}

			// Entity class - needed because of the UUID
			for (Field field : clazz.getSuperclass().getDeclaredFields()) {
				if (field.isAnnotationPresent(Outbreaks.class)) {
					classConfig.add(field.getName());
				}
			}

			classConfigCache.put(clazz, classConfig);
		}
	}
}
