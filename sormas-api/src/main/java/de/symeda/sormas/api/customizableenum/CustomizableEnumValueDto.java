/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.customizableenum;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

/**
 * Data Transfer Object corresponding to the CustomizableEnumValue entity, primarily used to exchange data with the mobile app.
 * This DTO is not intended to be used in the user interface as it is not explicitly internationalized and contains a lot of unprepared
 * information that is not needed there. The user interface should use classes extending {@link CustomizableEnum} instead.
 */
public class CustomizableEnumValueDto extends EntityDto {

	private static final long serialVersionUID = 4360662500289404985L;

	public static final String I18N_PREFIX = "CustomizableEnumValue";

	public static final String DATA_TYPE = "dataType";
	public static final String VALUE = "value";
	public static final String CAPTION = "caption";
	public static final String DESCRIPTION = "description";
	public static final String TRANSLATIONS = "translations";
	public static final String DISEASES = "diseases";

	@NotNull(message = Validations.required)
	private CustomizableEnumType dataType;
	@NotNull(message = Validations.required)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String value;
	@NotNull(message = Validations.required)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String caption;
	@Valid
	private List<CustomizableEnumTranslation> translations;
	private Set<Disease> diseases;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String description;
	@Valid
	private List<CustomizableEnumTranslation> descriptionTranslations;
	private Map<String, Object> properties;
	/**
	 * Whether the value is a default value provided by the software. Default values don't need a caption
	 * or translations because they can be translated via properties files.
	 */
	private boolean defaultValue;

	public static CustomizableEnumValueDto build() {
		CustomizableEnumValueDto dto = new CustomizableEnumValueDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}

	public CustomizableEnumType getDataType() {
		return dataType;
	}

	public void setDataType(CustomizableEnumType dataType) {
		this.dataType = dataType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public List<CustomizableEnumTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<CustomizableEnumTranslation> translations) {
		this.translations = translations;
	}

	public Set<Disease> getDiseases() {
		return diseases;
	}

	public void setDiseases(Set<Disease> diseases) {
		this.diseases = diseases;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<CustomizableEnumTranslation> getDescriptionTranslations() {
		return descriptionTranslations;
	}

	public void setDescriptionTranslations(List<CustomizableEnumTranslation> descriptionTranslations) {
		this.descriptionTranslations = descriptionTranslations;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
}
