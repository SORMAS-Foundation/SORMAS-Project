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

import java.util.Arrays;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Concrete {@link CustomizableFieldInput} for
 * {@link de.symeda.sormas.api.customizablefield.CustomizableFieldType#YES_NO_UNKNOWN}.
 * <p>
 * Renders a Vaadin v8 {@link ComboBox} populated with all {@link YesNoUnknown} constants.
 * The selected value is serialised to/from the DTO's {@code value} field as the enum name
 * ({@code "YES"}, {@code "NO"}, {@code "UNKNOWN"}) via
 * {@link CustomizableFieldValueDto#getValueAsYesNoUnknown()} and
 * {@link CustomizableFieldValueDto#setValueAsYesNoUnknown(YesNoUnknown)}.
 */
public class CustomizableFieldInputYesNoUnknown extends CustomizableFieldInput<YesNoUnknown> {

	private static final long serialVersionUID = 1L;

	private ComboBox<YesNoUnknown> comboBox;
	/**
	 * Holds a value that was pushed via {@link #doSetValue(YesNoUnknown)} before
	 * {@link #buildInputComponent()} had a chance to create the {@link ComboBox}.
	 * Applied to the combo box on first render.
	 */
	private YesNoUnknown pendingValue;

	public CustomizableFieldInputYesNoUnknown(CustomizableFieldMetadataDto metadata) {
		super(metadata);
	}

	@Override
	protected ValueProvider<CustomizableFieldValueDto, YesNoUnknown> getValueGetter() {
		return CustomizableFieldValueDto::getValueAsYesNoUnknown;
	}

	@Override
	protected Setter<CustomizableFieldValueDto, YesNoUnknown> getValueSetter() {
		return CustomizableFieldValueDto::setValueAsYesNoUnknown;
	}

	public Class<YesNoUnknown> getType() {
		return YesNoUnknown.class;
	}

	@Override
	protected Component buildInputComponent() {
		comboBox = new ComboBox<>();
		comboBox.setWidth(100, Unit.PERCENTAGE);
		comboBox.setItems(Arrays.asList(YesNoUnknown.values()));
		comboBox.setItemCaptionGenerator(YesNoUnknown::toString);
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
	protected void applyValueToWidget(YesNoUnknown value) {
		if (comboBox != null) {
			comboBox.setValue(value);
		} else {
			pendingValue = value;
		}
	}
}
