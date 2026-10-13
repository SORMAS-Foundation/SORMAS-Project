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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * DTO for customizable field values.
 * Stores the actual value for a custom field for a specific entity instance.
 */
public class CustomizableFieldValueDto extends EntityDto {

    private static final long serialVersionUID = 1L;

    /** Shared mapper for {@link #getValueAsStringSet()} / {@link #setValueAsStringSet(Set)}. */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String I18N_PREFIX = "CustomizableFieldValue";

    public static final String CUSTOMIZABLE_FIELD_METADATA_UUID = "customizableFieldMetadataUuid";
    public static final String ENTITY_UUID = "entityUuid";
    public static final String CONTEXT_CLASS = "contextClass";
    public static final String VALUE = "value";

    @NotBlank(message = Validations.required)
    private String customizableFieldMetadataUuid;

    @NotBlank(message = Validations.required)
    private String entityUuid;

    @NotNull(message = Validations.required)
    private CustomizableFieldContext contextClass;

    private String value;

    public CustomizableFieldValueDto() {
    }

    public String getCustomizableFieldMetadataUuid() {
        return customizableFieldMetadataUuid;
    }

    public void setCustomizableFieldMetadataUuid(String customizableFieldMetadataUuid) {
        this.customizableFieldMetadataUuid = customizableFieldMetadataUuid;
    }

    public String getEntityUuid() {
        return entityUuid;
    }

    public void setEntityUuid(String entityUuid) {
        this.entityUuid = entityUuid;
    }

    public CustomizableFieldContext getContextClass() {
        return contextClass;
    }

    public void setContextClass(CustomizableFieldContext contextClass) {
        this.contextClass = contextClass;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // -------------------------------------------------------------------------
    // Typed accessors – parse from the raw ISO string stored in {@link #value}.
    // Setters serialise back to the canonical ISO representation.
    // All getters return {@code null} when the value is absent or unparseable.
    // -------------------------------------------------------------------------

    /**
     * Parses {@link #value} as an ISO date ({@code yyyy-MM-dd}).
     *
     * @return the date, or {@code null} if the value is absent or not a valid ISO date
     */
    public LocalDate getValueAsDate() {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Stores a {@link LocalDate} as an ISO date string ({@code yyyy-MM-dd}).
     * Passing {@code null} clears the value.
     */
    public void setValueAsDate(LocalDate date) {
        this.value = date != null ? date.toString() : null;
    }

    /**
     * Parses {@link #value} as an ISO date-time ({@code yyyy-MM-ddTHH:mm} or longer).
     *
     * @return the date-time, or {@code null} if the value is absent or not a valid ISO date-time
     */
    public LocalDateTime getValueAsDateTime() {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Stores a {@link LocalDateTime} as an ISO date-time string, truncated to minutes
     * ({@code yyyy-MM-ddTHH:mm}).
     * Passing {@code null} clears the value.
     */
    public void setValueAsDateTime(LocalDateTime dateTime) {
        this.value = dateTime != null ? dateTime.withSecond(0).withNano(0).toString() : null;
    }

    /**
     * Parses {@link #value} as a whole number.
     *
     * @return the integer value, or {@code null} if the value is absent or not a valid integer
     */
    public Integer getValueAsInteger() {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Stores an {@link Integer} as a plain decimal string.
     * Passing {@code null} clears the value.
     */
    public void setValueAsInteger(Integer number) {
        this.value = number != null ? number.toString() : null;
    }

    /**
     * Parses {@link #value} as a decimal number.
     *
     * @return the decimal value, or {@code null} if the value is absent or not a valid number
     */
    public BigDecimal getValueAsDecimal() {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Stores a {@link BigDecimal} as a plain decimal string.
     * Passing {@code null} clears the value.
     */
    public void setValueAsDecimal(BigDecimal decimal) {
        this.value = decimal != null ? decimal.toPlainString() : null;
    }

    /**
     * Parses {@link #value} as a boolean ({@code "true"} / {@code "false"}, case-insensitive).
     *
     * @return {@link Boolean#TRUE} if the stored value equals {@code "true"} (ignoring case),
     *         {@link Boolean#FALSE} if it equals {@code "false"} (ignoring case),
     *         or {@code null} if the value is absent or does not match either token
     */
    public Boolean getValueAsBoolean() {
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (Boolean.TRUE.toString().equalsIgnoreCase(value.trim())) {
            return Boolean.TRUE;
        }
        if (Boolean.FALSE.toString().equalsIgnoreCase(value.trim())) {
            return Boolean.FALSE;
        }
        return null;
    }

    /**
     * Stores a {@link Boolean} as {@code "true"} or {@code "false"}.
     * Passing {@code null} clears the value.
     */
    public void setValueAsBoolean(Boolean bool) {
        this.value = bool != null ? bool.toString() : null;
    }

    /**
     * Parses {@link #value} as a {@link YesNoUnknown} enum constant (case-insensitive name).
     *
     * @return the matching constant, or {@code null} if the value is absent or not a valid name
     */
    public YesNoUnknown getValueAsYesNoUnknown() {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return YesNoUnknown.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Stores a {@link YesNoUnknown} constant as its {@link Enum#name()} ({@code "YES"}, {@code "NO"} or {@code "UNKNOWN"}).
     * Passing {@code null} clears the value.
     */
    public void setValueAsYesNoUnknown(YesNoUnknown yesNoUnknown) {
        this.value = yesNoUnknown != null ? yesNoUnknown.name() : null;
    }

    /**
     * Parses {@link #value} as a JSON array of strings, as used by
     * {@link de.symeda.sormas.api.customizablefield.CustomizableFieldType#CHECKBOX_LIST}.
     *
     * @return a mutable {@link LinkedHashSet} of the stored strings (preserving insertion order),
     *         or an empty set if the value is absent or cannot be parsed
     */
    public Set<String> getValueAsStringSet() {
        if (value == null || value.isEmpty()) {
            return new LinkedHashSet<>();
        }
        try {
            return MAPPER.readValue(value, new TypeReference<LinkedHashSet<String>>() {
            });
        } catch (IOException e) {
            return new LinkedHashSet<>();
        }
    }

    /**
     * Serialises a set of strings to a JSON array and stores it in {@link #value}.
     * Passing {@code null} or an empty set clears the value.
     */
    public void setValueAsStringSet(Set<String> set) {
        if (set == null || set.isEmpty()) {
            this.value = null;
            return;
        }
        try {
            this.value = MAPPER.writeValueAsString(set);
        } catch (JsonProcessingException e) {
            this.value = null;
        }
    }
}
