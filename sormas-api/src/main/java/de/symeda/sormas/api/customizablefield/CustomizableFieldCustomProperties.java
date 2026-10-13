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

import java.io.Serializable;
import java.util.List;

/**
 * Typed representation of the {@code customProperties} JSON column on
 * {@link CustomizableFieldMetadataDto}.
 * <p>
 * Field-type-specific configuration lives here:
 * <ul>
 * <li><b>options</b> – selectable string values used by
 * {@link CustomizableFieldType#COMBOBOX},
 * {@link CustomizableFieldType#CHECKBOX_LIST}, and
 * {@link CustomizableFieldType#RADIO_BUTTON_LIST}.</li>
 * </ul>
 * Additional properties can be added here as the feature grows without
 * touching the database schema (the whole object is stored as a single
 * {@code jsonb} column).
 */
public class CustomizableFieldCustomProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The list of selectable option values for list-type fields
     * ({@code COMBOBOX}, {@code CHECKBOX_LIST}, {@code RADIO_BUTTON_LIST}).
     * Each entry is the raw stored string value (not a display label).
     */
    private List<String> options;

    public CustomizableFieldCustomProperties() {
        // Required for JSON deserialization.
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
