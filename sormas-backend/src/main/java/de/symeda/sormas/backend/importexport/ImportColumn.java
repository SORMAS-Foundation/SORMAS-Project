/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.backend.importexport;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * Detailed import template column entity with information like entity name, column name, caption and data type.
 *
 * @author Alex Vidrean
 * @since 18-Sep-20
 */
public class ImportColumn {

	private String entityName;

	private String columnName;

	private String caption;

	private ImportColumn(String entityName, String columnName, String caption) {
		this.entityName = entityName;
		this.columnName = columnName;
		this.caption = caption;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getCaption() {
		return caption;
	}

	public static ImportColumn from(Class<?> entityType, String columnName) {
		String entityName = DataHelper.getHumanClassName(entityType);
		String caption = calculateCaption(entityName, columnName);
		return new ImportColumn(entityName, columnName, caption);
	}

	/**
	 * Computes the captions based on the entity name and column name.
	 * <p/>
	 * Column name is composed (ex: <code>person.firstName</code> or <code>person.address.city</code>)
	 * -> Split it in parts and use the last part as field name
	 * -> Combine the entity name with the field name
	 * <p/>
	 * Column name is simple (ex: <code>diseaseDetails</code>) -> Combine the entity name with the column name
	 * <p/>
	 * The result is a list with the same length as the <code>columnNames</code>
	 *
	 * @param entityName name of the entity from which the field is part of
	 * @param columnName column name from the CSV
	 * @return list of captions for each column name.
	 */
	private static String calculateCaption(String entityName, String columnName) {
		if (StringUtils.contains(columnName, ".")) {
			String[] parts = columnName.split("\\.");
			String fieldName = parts[parts.length - 1];
			return I18nProperties.getPrefixCaption(entityName, fieldName);
		} else {
			return I18nProperties.getPrefixCaption(entityName, columnName);
		}
	}
}
