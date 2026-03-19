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
import com.vaadin.ui.Component;
import com.vaadin.ui.RadioButtonGroup;

import de.symeda.sormas.api.customizablefield.CustomizableFieldCustomProperties;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;

/**
 * Concrete {@link CustomizableFieldInput} for
 * {@link de.symeda.sormas.api.customizablefield.CustomizableFieldType#RADIO_BUTTON_LIST}.
 * <p>
 * Renders a Vaadin v8 {@link RadioButtonGroup} populated with the string options defined in the
 * field's {@link CustomizableFieldMetadataDto#getCustomProperties() customProperties} under
 * {@link CustomizableFieldCustomProperties#getOptions()}.
 * <p>
 * The selected value is stored directly as a {@link String} in
 * {@link CustomizableFieldValueDto#getValue()}.
 */
public class CustomizableFieldInputRadioButtonList extends CustomizableFieldInput<String> {

    private static final long serialVersionUID = 1L;

    private RadioButtonGroup<String> radioButtonGroup;
    /**
     * Holds a value that was pushed via {@link #doSetValue(String)} before
     * {@link #buildInputComponent()} had a chance to create the {@link RadioButtonGroup}.
     * Applied to the group on first render.
     */
    private String pendingValue;

    public CustomizableFieldInputRadioButtonList(CustomizableFieldMetadataDto metadata) {
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
        radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.setItems(resolveOptions());

        // Apply any value that arrived before the component was first rendered.
        if (pendingValue != null) {
            radioButtonGroup.setValue(pendingValue);
            pendingValue = null;
        }

        // Propagate user edits back to the field's internal value state.
        radioButtonGroup.addValueChangeListener(e -> setValue(e.getValue()));

        return radioButtonGroup;
    }

    /**
     * Called by Vaadin when {@link #setValue(Object)} is invoked programmatically.
     * Pushes the value into the {@link RadioButtonGroup}; {@code null} clears the selection.
     * If the {@link RadioButtonGroup} has not been created yet (component not yet rendered),
     * the value is stored as a pending value and applied in {@link #buildInputComponent()}.
     */
    @Override
    protected void applyValueToWidget(String value) {
        if (radioButtonGroup != null) {
            radioButtonGroup.setValue(value);
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
