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

package de.symeda.sormas.ui.utils.components.customizablefield;

import java.util.Collections;
import java.util.List;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.customizablefield.CustomizableFieldCustomProperties;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;

/**
 * Concrete {@link CustomizableFieldInput} for {@link de.symeda.sormas.api.customizablefield.CustomizableFieldType#COMBOBOX}.
 * <p>
 * Renders a Vaadin v8 {@link ComboBox} populated with the string options defined in the field's
 * {@link CustomizableFieldMetadataDto#getCustomProperties() customProperties} map under the key
 * {@code "options"} (expected to be a {@link List} of {@link String}s). The selected value is
 * stored directly as a {@link String} in {@link CustomizableFieldValueDto#getValue()}.
 */
public class CustomizableFieldInputCombobox extends CustomizableFieldInput<String> {

    private static final long serialVersionUID = 1L;

    private ComboBox<String> comboBox;
    /**
     * Holds a value that was pushed via {@link #doSetValue(String)} before
     * {@link #buildInputComponent()} had a chance to create the {@link ComboBox}.
     * Applied to the combo box on first render.
     */
    private String pendingValue;

    public CustomizableFieldInputCombobox(CustomizableFieldMetadataDto metadata) {
        super(metadata);
    }

    @Override
    protected ValueProvider<CustomizableFieldValueDto, String> getValueGetter() {
        return CustomizableFieldValueDto::getValue;
    }

    @Override
    protected Setter<CustomizableFieldValueDto, String> getValueSetter() {
        return CustomizableFieldValueDto::setValue;
    }

    public Class<String> getType() {
        return String.class;
    }

    @Override
    protected Component buildInputComponent() {
        comboBox = new ComboBox<>();
        comboBox.setWidth(100, Unit.PERCENTAGE);
        comboBox.setItems(resolveOptions());
        comboBox.setEmptySelectionAllowed(true);

        // Apply any value that arrived before the component was first rendered.
        if (pendingValue != null) {
            comboBox.setValue(pendingValue);
            pendingValue = null;
        }

        // Propagate user edits back to the field's internal value state.
        comboBox.addValueChangeListener(e -> setValue(e.getValue()));

        return comboBox;
    }

    /**
     * Called by Vaadin when {@link #setValue(Object)} is invoked programmatically.
     * Pushes the value into the {@link ComboBox}; {@code null} clears the selection.
     * If the {@link ComboBox} has not been created yet (component not yet rendered),
     * the value is stored as a pending value and applied in {@link #buildInputComponent()}.
     */
    @Override
    protected void applyValueToWidget(String value) {
        if (comboBox != null) {
            comboBox.setValue(value);
        } else {
            pendingValue = value;
        }
    }

    /**
     * Reads the options list from {@link CustomizableFieldMetadataDto#getCustomProperties()}.
     * Returns an empty list when no custom properties are set or the options are absent.
     */
    private List<String> resolveOptions() {
        CustomizableFieldCustomProperties props = getFieldMetadata().getCustomProperties();
        if (props == null || props.getOptions() == null) {
            return Collections.emptyList();
        }
        return props.getOptions();
    }
}
