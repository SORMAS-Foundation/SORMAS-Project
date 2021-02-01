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

package de.symeda.sormas.api.importexport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;

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

	private String dataDescription;

	private ImportColumn(String entityName, String columnName, String caption, String dataDescription) {
		this.entityName = entityName;
		this.columnName = columnName;
		this.caption = caption;
		this.dataDescription = dataDescription;
	}

	public ImportColumn(String columnName, String caption, String dataDescription) {
		this.columnName = columnName;
		this.caption = caption;
		this.dataDescription = dataDescription;
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

	public String getDataDescription() {
		return dataDescription;
	}

	public static ImportColumn from(Class<?> entityType, String columnName, Class<?> fieldType, char currentSeparator) {
		String entityName = DataHelper.getHumanClassName(entityType);
		String caption = computeCaption(entityName, columnName);
		String dataType = computeDataType(fieldType, currentSeparator);
		return new ImportColumn(entityName, columnName, caption, dataType);
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
	 * @param entityName
	 *            name of the entity from which the field is part of
	 * @param columnName
	 *            column name from the CSV
	 * @return list of captions for each column name.
	 */
	private static String computeCaption(String entityName, String columnName) {
		if (StringUtils.contains(columnName, ".")) {
			String[] parts = columnName.split("\\.");
			String fieldName = parts[parts.length - 1];
			return I18nProperties.getPrefixCaption(entityName, fieldName);
		} else {
			return I18nProperties.getPrefixCaption(entityName, columnName);
		}
	}

	/**
	 * Computes the data type accepted for a certain field type. For values which cannot be determined at start a placeholder will be used
	 * (ex: {@link ImportFacade#ACTIVE_DISEASES_PLACEHOLDER}).
	 *
	 * @param fieldType
	 *            type of a CSV field (column)
	 * @param currentSeparator
	 *            current CSV configured separator, used to identify a different one for joining lists
	 * @return a data type description, example or placeholder
	 */
	private static String computeDataType(Class<?> fieldType, char currentSeparator) {
		char separator = ImportExportUtils.getCSVSeparatorDifferentFromCurrent(currentSeparator);

		if (String.class.isAssignableFrom(fieldType)) {
			return I18nProperties.getString(Strings.text);
		} else if (Date.class.isAssignableFrom(fieldType)) {
			return I18nProperties.getString(Strings.date) + ": dd/MM/yyyy";
		} else if (FacilityReferenceDto.class.isAssignableFrom(fieldType)) {
			return I18nProperties.getString(Strings.infoFacilityCsvImport);
		} else if (ReferenceDto.class.isAssignableFrom(fieldType)) {
			return String.format(I18nProperties.getString(Strings.nameOf), DataHelper.getHumanClassCaption(fieldType));
		} else if (Disease.class.isAssignableFrom(fieldType)) {
			return ImportFacade.ACTIVE_DISEASES_PLACEHOLDER;
		} else if (fieldType.isEnum()) {
			List<String> enumNames = new ArrayList<>();
			for (Object enumConstant : fieldType.getEnumConstants()) {
				enumNames.add(((Enum<?>) enumConstant).name());
			}
			return StringUtils.join(enumNames, separator);
		} else if (Number.class.isAssignableFrom(fieldType)) {
			return I18nProperties.getString(Strings.number);
		} else {
			return "";
		}
	}
}
