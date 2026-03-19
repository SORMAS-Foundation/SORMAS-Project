/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.customizablefield;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.backend.common.DeletableAdo;

/**
 * Entity class for customizable field metadata.
 * Stores the configuration for custom fields that can be added to entities.
 */
@Entity(name = "customizablefieldmetadata")
@TypeDefs({
	@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) })
public class CustomizableFieldMetadata extends DeletableAdo {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "customizablefieldmetadata";

	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String FIELD_TYPE = "fieldType";
	public static final String CONTEXT_CLASS = "contextClass";
	public static final String UI_GROUP = "uiGroup";
	public static final String UI_LINE_POSITION = "uiLinePosition";
	public static final String UI_LINE_WEIGHT = "uiLineWeight";
	public static final String ACTIVE = "active";
	public static final String MANDATORY = "mandatory";
	public static final String READ_ONLY = "readOnly";
	public static final String DEFAULT_VALUE = "defaultValue";

	private String name;
	private String description;
	private CustomizableFieldType fieldType;
	private CustomizableFieldContext contextClass;
	private String uiGroup;
	private Integer uiLinePosition;
	private Float uiLineWeight;
	private boolean active = true;
	private boolean mandatory = false;
	private boolean readOnly = false;
	private String defaultValue;

	// JSON-serialized complex properties
	private String visibilityRestrictions;
	private String customProperties;
	private String translations;

	@Column(length = 512, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(columnDefinition = "text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public CustomizableFieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(CustomizableFieldType fieldType) {
		this.fieldType = fieldType;
	}

	@Column(length = 256, nullable = false)
	@Convert(converter = CustomizableFieldContextConverter.class)
	public CustomizableFieldContext getContextClass() {
		return contextClass;
	}

	public void setContextClass(CustomizableFieldContext contextClass) {
		this.contextClass = contextClass;
	}

	@Column(length = 256)
	public String getUiGroup() {
		return uiGroup;
	}

	public void setUiGroup(String uiGroup) {
		this.uiGroup = uiGroup;
	}

	@Column
	public Integer getUiLinePosition() {
		return uiLinePosition;
	}

	public void setUiLinePosition(Integer uiLinePosition) {
		this.uiLinePosition = uiLinePosition;
	}

	@Column
	public Float getUiLineWeight() {
		return uiLineWeight;
	}

	public void setUiLineWeight(Float uiLineWeight) {
		this.uiLineWeight = uiLineWeight;
	}

	@Column(nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column
	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@Column
	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Column(columnDefinition = "text")
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	// JSON-serialized properties
	@Column(columnDefinition = "jsonb")
	@Type(type = "jsonb")
	public String getVisibilityRestrictions() {
		return visibilityRestrictions;
	}

	public void setVisibilityRestrictions(String visibilityRestrictions) {
		this.visibilityRestrictions = visibilityRestrictions;
	}

	@Column(columnDefinition = "jsonb")
	@Type(type = "jsonb")
	public String getCustomProperties() {
		return customProperties;
	}

	public void setCustomProperties(String customProperties) {
		this.customProperties = customProperties;
	}

	@Column(columnDefinition = "jsonb")
	@Type(type = "jsonb")
	public String getTranslations() {
		return translations;
	}

	public void setTranslations(String translations) {
		this.translations = translations;
	}
}
