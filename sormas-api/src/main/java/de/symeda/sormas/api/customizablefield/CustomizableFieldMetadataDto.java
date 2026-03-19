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

package de.symeda.sormas.api.customizablefield;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.FieldConstraints;

/**
 * DTO for customizable field metadata.
 * Contains configuration for a custom field that can be added to entities.
 */
public class CustomizableFieldMetadataDto extends EntityDto {

    private static final long serialVersionUID = 1L;

    public static final String I18N_PREFIX = "CustomizableFieldMetadata";

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
    public static final String VISIBILITY_RESTRICTIONS = "visibilityRestrictions";
    public static final String CUSTOM_PROPERTIES = "customProperties";
    public static final String TRANSLATIONS = "translations";

    @NotBlank(message = Validations.required)
    @Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
    private String name;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
    private String description;

    @NotNull(message = Validations.required)
    private CustomizableFieldType fieldType;

    @NotNull(message = Validations.required)
    private CustomizableFieldContext contextClass;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL)
    private String uiGroup;

    private Integer uiLinePosition;
    private Float uiLineWeight;

    private boolean active = true;
    private boolean mandatory = false;
    private boolean readOnly = false;

    @Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT)
    private String defaultValue;

    private CustomizableFieldVisibilityRestrictions visibilityRestrictions;
    private CustomizableFieldCustomProperties customProperties;
    private Map<String, Map<String, String>> translations;

    public CustomizableFieldMetadataDto() {
        // Required for deserialization frameworks like Jackson and REST clients
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CustomizableFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(CustomizableFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public CustomizableFieldContext getContextClass() {
        return contextClass;
    }

    public void setContextClass(CustomizableFieldContext contextClass) {
        this.contextClass = contextClass;
    }

    public String getUiGroup() {
        return uiGroup;
    }

    public void setUiGroup(String uiGroup) {
        this.uiGroup = uiGroup;
    }

    public Integer getUiLinePosition() {
        return uiLinePosition;
    }

    public void setUiLinePosition(Integer uiLinePosition) {
        this.uiLinePosition = uiLinePosition;
    }

    public Float getUiLineWeight() {
        return uiLineWeight;
    }

    public void setUiLineWeight(Float uiLineWeight) {
        this.uiLineWeight = uiLineWeight;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public CustomizableFieldVisibilityRestrictions getVisibilityRestrictions() {
        return visibilityRestrictions;
    }

    public void setVisibilityRestrictions(CustomizableFieldVisibilityRestrictions visibilityRestrictions) {
        this.visibilityRestrictions = visibilityRestrictions;
    }

    public CustomizableFieldCustomProperties getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(CustomizableFieldCustomProperties customProperties) {
        this.customProperties = customProperties;
    }

    public Map<String, Map<String, String>> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, Map<String, String>> translations) {
        this.translations = translations;
    }
}
