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

package de.symeda.sormas.backend.customizableenum;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Type;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.disease.DiseaseListConverter;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * Entity that stores one individual enum value for a supported customizable enum type. This primary use of this entity is to store the
 * values in the database, and to build the caches and corresponding {@link de.symeda.sormas.api.customizableenum.CustomizableEnumValueDto}
 * for data exchange. It is not meant to be referenced in other entities that contain fields that are using one of the customizable enum
 * types. Those fields should instead reference a specific extension of {@link de.symeda.sormas.api.customizableenum.CustomizableEnum} and
 * the corresponding extension of {@link CustomizableEnumConverter}.
 */
@Entity
public class CustomizableEnumValue extends AbstractDomainObject {

	private static final long serialVersionUID = -8438117516604287640L;

	public static final String TABLE_NAME = "customizableenumvalue";

	private CustomizableEnumType dataType;
	private String value;
	private String caption;
	private List<CustomizableEnumTranslation> translations;
	private List<Disease> diseases;
	private String description;
	private List<CustomizableEnumTranslation> descriptionTranslations;
	private Map<String, Object> properties;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public CustomizableEnumType getDataType() {
		return dataType;
	}

	public void setDataType(CustomizableEnumType dataType) {
		this.dataType = dataType;
	}

	@Column(nullable = false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(nullable = false)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Type(type = ModelConstants.HIBERNATE_TYPE_JSON)
	@Column(columnDefinition = ModelConstants.COLUMN_DEFINITION_JSON)
	public List<CustomizableEnumTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<CustomizableEnumTranslation> translations) {
		this.translations = translations;
	}

	@Column
	@Convert(converter = DiseaseListConverter.class)
	public List<Disease> getDiseases() {
		return diseases;
	}

	public void setDiseases(List<Disease> diseases) {
		this.diseases = diseases;
	}

	@Column(columnDefinition = "text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Type(type = ModelConstants.HIBERNATE_TYPE_JSON)
	@Column(columnDefinition = ModelConstants.COLUMN_DEFINITION_JSON)
	public List<CustomizableEnumTranslation> getDescriptionTranslations() {
		return descriptionTranslations;
	}

	public void setDescriptionTranslations(List<CustomizableEnumTranslation> descriptionTranslations) {
		this.descriptionTranslations = descriptionTranslations;
	}

	@Type(type = ModelConstants.HIBERNATE_TYPE_JSON)
	@Column(columnDefinition = ModelConstants.COLUMN_DEFINITION_JSON)
	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
}
