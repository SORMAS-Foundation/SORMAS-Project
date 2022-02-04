/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.backend.info;

import java.lang.reflect.Field;

public class FieldData {

	private final Field field;
	private final Class<?> entityClass;
	private final String i18NPrefix;

	public FieldData(Field field, Class<?> entityClass, String i18NPrefix) {
		this.field = field;
		this.entityClass = entityClass;
		this.i18NPrefix = i18NPrefix;
	}

	public Field getField() {
		return field;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public String getI18NPrefix() {
		return i18NPrefix;
	}
}
