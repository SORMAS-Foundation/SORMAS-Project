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

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;

/**
 * Concrete {@link CustomizableFieldInput} for {@link de.symeda.sormas.api.customizablefield.CustomizableFieldType#TEXT}
 * (and, by extension, any single-line free-text field type).
 * <p>
 * Renders a Vaadin v8 {@link TextField}. User edits are propagated to the parent field's
 * internal state via a value-change listener; programmatic changes pushed through
 * {@link #setValue(Object)} are reflected in the {@link TextField} via {@link #doSetValue(String)}.
 */
public class CustomizableFieldInputText extends CustomizableFieldInput<String> {

	private static final long serialVersionUID = 1L;

	private TextField textField;
	/**
	 * Holds a value that was pushed via {@link #doSetValue(String)} before {@link #buildInputComponent()}
	 * had a chance to create the {@link TextField}. Applied to the text field on first render.
	 */
	private String pendingValue;

	public CustomizableFieldInputText(CustomizableFieldMetadataDto metadata) {
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
		textField = new TextField();
		textField.setWidth(100, Unit.PERCENTAGE);

		// Apply any value that arrived before the component was first rendered.
		if (pendingValue != null) {
			textField.setValue(pendingValue);
			pendingValue = null;
		}

		// Propagate user edits back to the field's internal value state.
		textField.addValueChangeListener(e -> setValue(e.getValue()));

		return textField;
	}

	/**
	 * Called by Vaadin when {@link #setValue(Object)} is invoked programmatically.
	 * Pushes the new value into the {@link TextField}, converting {@code null} to an
	 * empty string because {@code TextField.setValue(null)} throws {@link NullPointerException}.
	 * If the {@link TextField} has not been created yet (component not yet rendered),
	 * the value is stored as a pending value and applied in {@link #buildInputComponent()}.
	 */
	@Override
	protected void applyValueToWidget(String value) {
		if (textField != null) {
			textField.setValue(value != null ? value : "");
		} else {
			pendingValue = value;
		}
	}
}
