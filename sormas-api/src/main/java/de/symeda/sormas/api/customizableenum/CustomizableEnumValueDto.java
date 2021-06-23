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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;

/**
 * Data Transfer Object corresponding to the CustomizableEnumValue entity, primarily used to exchange data with the mobile app.
 * This DTO is not intended to be used in the user interface as it is not explicitly internationalized and contains a lot of unprepared
 * information that is not needed there. The user interface should use classes extending {@link CustomizableEnum} instead.
 */
public class CustomizableEnumValueDto extends EntityDto {

	private static final long serialVersionUID = 4360662500289404985L;

	private CustomizableEnumType dataType;
	private String value;
	private String caption;
	private List<CustomizableEnumTranslation> translations;
	private List<Disease> diseases;
	private String description;
	private List<CustomizableEnumTranslation> descriptionTranslations;
	private Map<String, Object> properties;

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

	public List<Disease> getDiseases() {
		return diseases;
	}

	public void setDiseases(List<Disease> diseases) {
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
}
