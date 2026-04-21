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

import java.time.LocalDateTime;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateTimeField;

import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;

/**
 * Concrete {@link CustomizableFieldInput} for {@link de.symeda.sormas.api.customizablefield.CustomizableFieldType#DATE_TIME}.
 * <p>
 * Renders a Vaadin 8 {@link DateTimeField} (date + time picker, value type {@link LocalDateTime}).
 * The value is stored internally as an ISO-8601 date-time string ({@code yyyy-MM-ddTHH:mm})
 * consistent with the {@link String} contract of the base class. The display format follows
 * the locale configured in SORMAS via {@link DateHelper#getLocalDateTimeFormat}.
 * <p>
 * Value round-trip:
 * <ul>
 * <li><b>widget → string:</b> {@link LocalDateTime} truncated to minutes, formatted as
 * {@code yyyy-MM-ddTHH:mm} via {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME}</li>
 * <li><b>string → widget:</b> {@link LocalDateTime#parse(CharSequence)}</li>
 * </ul>
 */
public class CustomizableFieldInputDateTime extends CustomizableFieldInput<LocalDateTime> {

	private static final long serialVersionUID = 1L;

	private DateTimeField dateTimeField;
	/**
	 * Holds a value that was pushed via {@link #doSetValue(LocalDateTime)} before
	 * {@link #buildInputComponent()} had a chance to create the {@link DateTimeField}.
	 * Applied to the date-time field on first render.
	 */
	private LocalDateTime pendingValue;

	public CustomizableFieldInputDateTime(CustomizableFieldMetadataDto metadata) {
		super(metadata);
	}

	@Override
	protected ValueProvider<CustomizableFieldValueDto, LocalDateTime> getValueGetter() {
		return CustomizableFieldValueDto::getValueAsDateTime;
	}

	@Override
	protected Setter<CustomizableFieldValueDto, LocalDateTime> getValueSetter() {
		return CustomizableFieldValueDto::setValueAsDateTime;
	}

	public Class<LocalDateTime> getType() {
		return LocalDateTime.class;
	}

	@Override
	protected Component buildInputComponent() {
		dateTimeField = new DateTimeField();
		dateTimeField.setWidth(100, Unit.PERCENTAGE);
		dateTimeField.setDateFormat(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage()).toPattern());

		// Apply any value that arrived before the component was first rendered.
		if (pendingValue != null) {
			dateTimeField.setValue(pendingValue);
			pendingValue = null;
		}

		// Propagate user edits back to the field's internal value state.
		dateTimeField.addValueChangeListener(e -> setValue(e.getValue()));

		return dateTimeField;
	}

	/**
	 * Propagates {@code value} into the {@link DateTimeField}; {@code null} clears it.
	 * If the {@link DateTimeField} has not been created yet (component not yet rendered),
	 * the value is stored as a pending value and applied in {@link #buildInputComponent()}.
	 */
	@Override
	protected void applyValueToWidget(LocalDateTime value) {
		if (dateTimeField != null) {
			dateTimeField.setValue(value);
		} else {
			pendingValue = value;
		}
	}
}
