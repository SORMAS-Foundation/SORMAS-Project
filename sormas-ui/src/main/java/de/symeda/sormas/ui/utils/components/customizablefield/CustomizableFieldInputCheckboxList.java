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
import java.util.Set;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.customizablefield.CustomizableFieldCustomProperties;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;

/**
 * Concrete {@link CustomizableFieldInput} for
 * {@link de.symeda.sormas.api.customizablefield.CustomizableFieldType#CHECKBOX_LIST}.
 * <p>
 * Renders a Vaadin v8 {@link CheckBoxGroup} populated with the string options defined in the
 * field's {@link CustomizableFieldMetadataDto#getCustomProperties() customProperties} under
 * {@link CustomizableFieldCustomProperties#getOptions()}.
 * <p>
 * The selected set is serialised to/from the DTO's {@code value} field as a JSON array via
 * {@link CustomizableFieldValueDto#getValueAsStringSet()} and
 * {@link CustomizableFieldValueDto#setValueAsStringSet(Set)}.
 */
public class CustomizableFieldInputCheckboxList extends CustomizableFieldInput<Set<String>> {

    private static final long serialVersionUID = 1L;

    private CheckBoxGroup<String> checkBoxGroup;
    /**
     * Holds a value that was pushed via {@link #doSetValue(Set)} before
     * {@link #buildInputComponent()} had a chance to create the {@link CheckBoxGroup}.
     * Applied to the group on first render.
     */
    private Set<String> pendingValue;

    public CustomizableFieldInputCheckboxList(CustomizableFieldMetadataDto metadata) {
        super(metadata);
    }

    @Override
    protected ValueProvider<CustomizableFieldValueDto, Set<String>> getValueGetter() {
        return CustomizableFieldValueDto::getValueAsStringSet;
    }

    @Override
    protected Setter<CustomizableFieldValueDto, Set<String>> getValueSetter() {
        return CustomizableFieldValueDto::setValueAsStringSet;
    }

    @SuppressWarnings("unchecked")
    public Class<Set<String>> getType() {
        return (Class<Set<String>>) (Class<?>) Set.class;
    }

    @Override
    protected Component buildInputComponent() {
        checkBoxGroup = new CheckBoxGroup<>();
        checkBoxGroup.setItems(resolveOptions());

        // Apply any value that arrived before the component was first rendered.
        if (pendingValue != null) {
            checkBoxGroup.setValue(pendingValue);
            pendingValue = null;
        }

        // Propagate user edits back to the field's internal value state.
        checkBoxGroup.addValueChangeListener(e -> setValue(e.getValue()));

        return checkBoxGroup;
    }

    /**
     * Called by Vaadin when {@link #setValue(Object)} is invoked programmatically.
     * Pushes the value into the {@link CheckBoxGroup}; {@code null} clears the selection.
     * If the {@link CheckBoxGroup} has not been created yet (component not yet rendered),
     * the value is stored as a pending value and applied in {@link #buildInputComponent()}.
     */
    @Override
    protected void applyValueToWidget(Set<String> value) {
        if (checkBoxGroup != null) {
            checkBoxGroup.setValue(value != null ? value : Collections.emptySet());
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
