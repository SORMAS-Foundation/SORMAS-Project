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

import java.time.LocalDate;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;

import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.utils.DateFormatHelper;

/**
 * Concrete {@link CustomizableFieldInput} for {@link de.symeda.sormas.api.customizablefield.CustomizableFieldType#DATE}.
 * <p>
 * Renders a Vaadin 8 {@link DateField} (date-only picker, value type {@link LocalDate}).
 * The value is stored internally as an ISO date string ({@code yyyy-MM-dd}) consistent with
 * the {@link String} contract of the base class. The display format follows the locale
 * configured in SORMAS via {@link DateFormatHelper#getDateFormatPattern()}.
 * <p>
 * Value round-trip:
 * <ul>
 * <li><b>widget → string:</b> {@link LocalDate#toString()} ({@code yyyy-MM-dd})</li>
 * <li><b>string → widget:</b> {@link LocalDate#parse(CharSequence)}</li>
 * </ul>
 */
public class CustomizableFieldInputDate extends CustomizableFieldInput<LocalDate> {

	private static final long serialVersionUID = 1L;

	private DateField dateField;
	/**
	 * Holds a value that was pushed via {@link #doSetValue(LocalDate)} before
	 * {@link #buildInputComponent()} had a chance to create the {@link DateField}.
	 * Applied to the date field on first render.
	 */
	private LocalDate pendingValue;

	public CustomizableFieldInputDate(CustomizableFieldMetadataDto metadata) {
		super(metadata);
	}

	@Override
	protected ValueProvider<CustomizableFieldValueDto, LocalDate> getValueGetter() {
		return CustomizableFieldValueDto::getValueAsDate;
	}

	@Override
	protected Setter<CustomizableFieldValueDto, LocalDate> getValueSetter() {
		return CustomizableFieldValueDto::setValueAsDate;
	}

	public Class<LocalDate> getType() {
		return LocalDate.class;
	}

	@Override
	protected Component buildInputComponent() {
		dateField = new DateField();
		dateField.setWidth(100, Unit.PERCENTAGE);
		dateField.setDateFormat(DateFormatHelper.getDateFormatPattern());

		// Apply any value that arrived before the component was first rendered.
		if (pendingValue != null) {
			dateField.setValue(pendingValue);
			pendingValue = null;
		}

		// Propagate user edits back to the field's internal value state.
		dateField.addValueChangeListener(e -> setValue(e.getValue()));

		return dateField;
	}

	/**
	 * Propagates {@code value} into the {@link DateField}; {@code null} clears it.
	 * If the {@link DateField} has not been created yet (component not yet rendered),
	 * the value is stored as a pending value and applied in {@link #buildInputComponent()}.
	 */
	@Override
	protected void applyValueToWidget(LocalDate value) {
		if (dateField != null) {
			dateField.setValue(value);
		} else {
			pendingValue = value;
		}
	}
}
