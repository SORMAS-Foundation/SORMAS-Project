/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.info;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.reflect.TypeUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;

public class EntityColumns {

	private final EntityColumn ENTITY = new EntityColumn("ENTITY", 256 * 30, this::getEntity, false, false, false, true);
	private final EntityColumn FIELD_ID = new EntityColumn("FIELD_ID", 256 * 30, EntityColumns::getFieldId, false, true, true, true);
	private final EntityColumn FIELD = new EntityColumn("FIELD", 256 * 30, this::getFieldName, false, true, true, true);
	private final EntityColumn TYPE = new EntityColumn("TYPE", 256 * 30, this::getFieldType, true, true, true, true);
	private final EntityColumn DATA_PROTECTION = new EntityColumn("DATA_PROTECTION", 256 * 30, this::getDataProtection, false, true, true, true);
	private final EntityColumn CAPTION = new EntityColumn("CAPTION", 256 * 30, this::getCaption, false, true, true, true);
	private final EntityColumn DESCRIPTION = new EntityColumn("DESCRIPTION", 256 * 60, this::getDescription, true, true, true, true);
	private final EntityColumn REQUIRED = new EntityColumn("REQUIRED", 256 * 10, this::getNotNull, false, true, true, true);
	private final EntityColumn NEW_DISEASE = new EntityColumn("NEW_DISEASE", 256 * 8, this::getNewDisease, false, true, true, true);
	private final EntityColumn DISEASES = new EntityColumn("DISEASES", 256 * 45, this::getDiseases, true, true, true, true);
	private final EntityColumn OUTBREAKS = new EntityColumn("OUTBREAKS", 256 * 10, this::getOutbreaks, false, true, true, true);
	private final EntityColumn IGNORED_COUNTRIES =
		new EntityColumn("IGNORED_COUNTRIES", 256 * 20, this::getIgnoredCountries, false, true, false, false);
	private final EntityColumn EXCLUSIVE_COUNTRIES =
		new EntityColumn("EXCLUSIVE_COUNTRIES", 256 * 20, this::getExclusiveCountries, false, true, false, false);

	private final List<EntityColumn> entityColumns = List.of(
		ENTITY,
		FIELD_ID,
		FIELD,
		TYPE,
		DATA_PROTECTION,
		CAPTION,
		DESCRIPTION,
		REQUIRED,
		NEW_DISEASE,
		DISEASES,
		OUTBREAKS,
		IGNORED_COUNTRIES,
		EXCLUSIVE_COUNTRIES);

	private final String serverCountry;
	private final boolean isDataProtectionFlow;

	public EntityColumns(final String serverCountry, final boolean isDataProtectionFlow) {
		this.serverCountry = serverCountry;
		this.isDataProtectionFlow = isDataProtectionFlow;
	}

	public List<EntityColumn> getEntityColumns() {

		if (isDataProtectionFlow) {
			return entityColumns.stream().filter(EntityColumn::isDataProtectionColumn).collect(Collectors.toList());
		}

		return entityColumns.stream().filter(EntityColumn::isDataDictionaryColumn).collect(Collectors.toList());
	}

	public List<EntityColumn> getColumnsForAllFieldsSheet() {
		return entityColumns.stream().filter(EntityColumn::isColumnForAllFieldsSheet).collect(Collectors.toList());
	}

	private String getEntity(FieldData fieldData) {
		return DataHelper.getHumanClassName(fieldData.getEntityClass());
	}

	public static String getFieldId(FieldData fieldData) {
		return DataHelper.getHumanClassName(fieldData.getEntityClass()) + "." + fieldData.getField().getName();
	}

	private String getFieldName(FieldData fieldData) {
		return fieldData.getField().getName();
	}

	private String getFieldType(FieldData fieldData) {
		Class<?> fieldType = fieldData.getField().getType();
		if (fieldType.isEnum()) {
			// use enum type name - values are added below
			return fieldType.getSimpleName();
		} else if (EntityDto.class.isAssignableFrom(fieldType)) {
			return DataHelper.getHumanClassName(fieldType);
		} else if (ReferenceDto.class.isAssignableFrom(fieldType)) {
			return DataHelper.getHumanClassName(fieldType);
		} else if (String.class.isAssignableFrom(fieldType)) {
			return I18nProperties.getString(Strings.text);
		} else if (Date.class.isAssignableFrom(fieldType)) {
			return I18nProperties.getString(Captions.date);
		} else if (Number.class.isAssignableFrom(fieldType)) {
			return I18nProperties.getString(Strings.number);
		} else if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
			return Boolean.TRUE + ", " + Boolean.FALSE;
		} else if (Collection.class.isAssignableFrom(fieldType)) {
			return TypeUtils.getTypeArguments((ParameterizedType) fieldData.getField().getGenericType())
				.values()
				.stream()
				.findFirst()
				.map(type -> String.format(I18nProperties.getString(Strings.listOf), DataHelper.getHumanClassName((Class<?>) type)))
				.orElseGet(fieldType::getSimpleName);
		} else if (Map.class.isAssignableFrom(fieldType)) {
			Type[] generics = TypeUtils.getTypeArguments((ParameterizedType) fieldData.getField().getGenericType()).values().toArray(new Type[0]);
			if (generics.length != 2) {
				throw new IllegalStateException("Could not clearly determine key and value generics.");
			}
			return String.format(
				I18nProperties.getString(Strings.mapOf),
				DataHelper.getHumanClassName((Class<?>) generics[0]),
				DataHelper.getHumanClassName((Class<?>) generics[1]));
		}

		return fieldType.getSimpleName();
	}

	private String getDataProtection(FieldData fieldData) {
		Field field = fieldData.getField();

		if (field.getAnnotation(PersonalData.class) != null
			&& (!isDataProtectionFlow || !Arrays.asList(field.getAnnotation(PersonalData.class).excludeForCountries()).contains(serverCountry))) {
			return "personal";
		} else {
			if (field.getAnnotation(SensitiveData.class) != null
				&& (!isDataProtectionFlow
					|| !Arrays.asList(field.getAnnotation(SensitiveData.class).excludeForCountries()).contains(serverCountry))) {
				return "sensitive";
			}
		}

		return null;
	}

	private String getCaption(FieldData fieldData) {
		return I18nProperties.getPrefixCaption(fieldData.getI18NPrefix(), fieldData.getField().getName(), "");
	}

	private String getDescription(FieldData fieldData) {
		String fieldDescription = I18nProperties.getPrefixDescription(fieldData.getI18NPrefix(), fieldData.getField().getName(), "");

		if (isDataProtectionFlow) {
			return fieldDescription;
		}

		String[] excludedForCountries = null;

		Field field = fieldData.getField();
		if (field.getAnnotation(PersonalData.class) != null) {
			excludedForCountries = field.getAnnotation(PersonalData.class).excludeForCountries();

		} else if (field.getAnnotation(SensitiveData.class) != null) {
			excludedForCountries = field.getAnnotation(SensitiveData.class).excludeForCountries();

		}

		String description;
		if (excludedForCountries != null && excludedForCountries.length > 0) {
			description = fieldDescription + I18nProperties.getString(Strings.messageCountriesExcludedFromDataProtection) + " "
				+ Arrays.toString(excludedForCountries);
		} else {
			description = fieldDescription;
		}

		return description;
	}

	private String getNotNull(FieldData fieldData) {
		if (fieldData.getField().getAnnotation(NotNull.class) == null) {
			return null;
		}

		return Boolean.TRUE.toString();
	}

	private String getNewDisease(FieldData fieldData) {
		return null;
	}

	private String getDiseases(FieldData fieldData) {
		Diseases diseases = fieldData.getField().getAnnotation(Diseases.class);
		if (diseases == null) {
			return "All";
		} else {
			StringBuilder diseasesString = new StringBuilder();
			for (Disease disease : diseases.value()) {
				if (diseasesString.length() > 0)
					diseasesString.append(", ");
				diseasesString.append(disease.toShortString());
			}
			return diseasesString.toString();
		}
	}

	private String getOutbreaks(FieldData fieldData) {
		if (fieldData.getField().getAnnotation(Outbreaks.class) == null) {
			return null;
		}

		return Boolean.TRUE.toString();
	}

	private String getIgnoredCountries(FieldData fieldData) {
		HideForCountries hideForCountries = fieldData.getField().getAnnotation(HideForCountries.class);
		if (hideForCountries == null) {
			return null;
		}

		StringBuilder hideForCountriesString = new StringBuilder();
		for (String country : hideForCountries.countries()) {
			if (hideForCountriesString.length() > 0)
				hideForCountriesString.append(", ");
			hideForCountriesString.append(country);
		}

		return hideForCountriesString.toString();
	}

	private String getExclusiveCountries(FieldData fieldData) {
		HideForCountriesExcept hideForCountriesExcept = fieldData.getField().getAnnotation(HideForCountriesExcept.class);
		if (hideForCountriesExcept == null) {
			return null;
		}

		StringBuilder hideForCountriesExceptString = new StringBuilder();
		for (String exceptCountry : hideForCountriesExcept.countries()) {
			if (hideForCountriesExceptString.length() > 0)
				hideForCountriesExceptString.append(", ");
			hideForCountriesExceptString.append(exceptCountry);
		}

		return hideForCountriesExceptString.toString();
	}

	public class EntityColumn {

		private final String name;
		private final int width;
		private final Function<FieldData, String> getValueFromField;
		private final boolean hasDefaultStyle;
		private final boolean isDataDictionaryColumn;
		private final boolean isDataProtectionColumn;
		private final boolean isColumnForAllFieldsSheet;

		EntityColumn(
			String name,
			int width,
			Function<FieldData, String> getValueFromField,
			boolean hasDefaultStyle,
			boolean isDataDictionaryColumn,
			boolean isDataProtectionColumn,
			boolean isColumnForAllFieldsSheet) {

			this.name = name;
			this.width = width;
			this.getValueFromField = getValueFromField;
			this.hasDefaultStyle = hasDefaultStyle;
			this.isDataDictionaryColumn = isDataDictionaryColumn;
			this.isDataProtectionColumn = isDataProtectionColumn;
			this.isColumnForAllFieldsSheet = isColumnForAllFieldsSheet;
		}

		public int getWidth() {
			return width;
		}

		public String getGetValueFromField(FieldData fieldData) {
			return getValueFromField.apply(fieldData);
		}

		public boolean hasDefaultStyle() {
			return hasDefaultStyle;
		}

		public boolean isDataDictionaryColumn() {
			return isDataDictionaryColumn;
		}

		public boolean isDataProtectionColumn() {
			return isDataProtectionColumn;
		}

		public boolean isColumnForAllFieldsSheet() {
			return isColumnForAllFieldsSheet;
		}

		@Override
		public String toString() {
			return I18nProperties.getPrefixCaption("EntityColumn", name);
		}

	}
}
