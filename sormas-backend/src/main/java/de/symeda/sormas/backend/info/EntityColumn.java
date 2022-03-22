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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.Required;
import de.symeda.sormas.api.utils.SensitiveData;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import de.symeda.sormas.api.i18n.I18nProperties;
import org.apache.commons.lang3.reflect.TypeUtils;

public enum EntityColumn {
    ENTITY(256 * 30, EntityColumn::getEntity, false),
	FIELD_ID(256 * 30, EntityColumn::getFieldId, false),
	FIELD(256 * 30, EntityColumn::getFieldName, false),
	TYPE(256 * 30, EntityColumn::getFieldType, true),
	DATA_PROTECTION(256 * 30, EntityColumn::getDataProtection, false),
	CAPTION(256 * 30, EntityColumn::getCaption, false),
	DESCRIPTION(256 * 60, EntityColumn::getDescription, true),
	REQUIRED(256 * 10, EntityColumn::getRequired, false),
	NEW_DISEASE(256 * 8, EntityColumn::getNewDisease, false),
	DISEASES(256 * 45, EntityColumn::getDiseases, true),
	OUTBREAKS(256 * 10, EntityColumn::getOutbreaks, false),
	IGNORED_COUNTRIES(256 * 20, EntityColumn::getIgnoredCountries, false),
	EXCLUSIVE_COUNTRIES(256 * 20, EntityColumn::getExclusiveCountries, false);

	private final int width;
	private final Function<FieldData, String> getValueFromField;
	private final boolean hasDefaultStyle;

	EntityColumn(int width, Function<FieldData, String> getValueFromField, boolean hasDefaultStyle) {
		this.width = width;
		this.getValueFromField = getValueFromField;
		this.hasDefaultStyle = hasDefaultStyle;
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

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	private static String getEntity(FieldData fieldData) {
        return DataHelper.getHumanClassName(fieldData.getEntityClass());
    }

	private static String getFieldId(FieldData fieldData) {
		return DataHelper.getHumanClassName(fieldData.getEntityClass()) + "." + fieldData.getField().getName();
	}

	private static String getFieldName(FieldData fieldData) {
		return fieldData.getField().getName();
	}

	private static String getFieldType(FieldData fieldData) {
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

	private static String getDataProtection(FieldData fieldData) {
		Field field = fieldData.getField();

		if (field.getAnnotation(PersonalData.class) != null) {
			return "personal";
		} else {
			if (field.getAnnotation(SensitiveData.class) != null) {
				return "sensitive";
			}
		}

		return null;
	}

	private static String getCaption(FieldData fieldData) {
		return I18nProperties.getPrefixCaption(fieldData.getI18NPrefix(), fieldData.getField().getName(), "");
	}

	private static String getDescription(FieldData fieldData) {
		return I18nProperties.getPrefixDescription(fieldData.getI18NPrefix(), fieldData.getField().getName(), "");
	}

	private static String getRequired(FieldData fieldData) {
		if (fieldData.getField().getAnnotation(Required.class) == null) {
			return null;
		}

		return Boolean.TRUE.toString();
	}

	private static String getNewDisease(FieldData fieldData) {
		return null;
	}

	private static String getDiseases(FieldData fieldData) {
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

	private static String getOutbreaks(FieldData fieldData) {
		if (fieldData.getField().getAnnotation(Outbreaks.class) == null) {
			return null;
		}

		return Boolean.TRUE.toString();
	}

	private static String getIgnoredCountries(FieldData fieldData) {
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

	private static String getExclusiveCountries(FieldData fieldData) {
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
}
