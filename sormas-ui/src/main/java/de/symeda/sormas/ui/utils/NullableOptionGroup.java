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
package de.symeda.sormas.ui.utils;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

public class NullableOptionGroup extends OptionGroup {

	public NullableOptionGroup() {
		setup();
	}

	public NullableOptionGroup(String caption, Collection<?> options) {
		super(caption, options);
		setup();
	}

	public NullableOptionGroup(String caption, Container dataSource) {
		super(caption, dataSource);
		setup();
	}

	public NullableOptionGroup(String caption) {
		super(caption);
		setup();
	}

	/**
	 * Switches between the two modes of the option group:
	 * <ul>
	 * <li>not required: multi-select, so an option can be unchecked again; the value is a {@link Set} and
	 * {@link NullableOptionGroupConverter} turns it into the single model value</li>
	 * <li>required: single-select without a converter; the value is the model value itself</li>
	 * </ul>
	 * When switching to required, the converter is removed <b>before</b> the selection mode is changed. Vaadin's
	 * {@code AbstractField.setValue} converts a new value to the model and back; with the multi-select converter still set,
	 * that round trip turned the single value into a {@link Set} again (the converter returns
	 * {@code Collections.singleton(value)}), which then could not be committed without a converter.
	 * <p>
	 * Example: "Sample purpose" is loaded with EXTERNAL while not required, so its value is {@code [EXTERNAL]}; the
	 * sample form then calls {@code setRequired(true)} → the value is {@code EXTERNAL} and the commit saves EXTERNAL.
	 * Before this order, the value stayed {@code [EXTERNAL]} and the commit failed with "Unable to convert value of type
	 * java.util.Collections$SingletonSet to model type SamplePurpose".
	 */
	private void setup() {

		if (isRequired()) {
			setConverter((Converter<Object, ?>) null);
			super.setMultiSelect(false);
			// safety net: a value that is still a set in single-select mode can't be committed, keep its first option
			unwrapSetValue();
		} else {
			super.setMultiSelect(true);
			setConverter(new NullableOptionGroupConverter(getFirstValue(getValue())));
		}
		addStyleName("multiline");
	}

	/**
	 * Replaces a {@link Set} value by its first option while the option group is single-select (required). Does nothing
	 * for a value that is not a set, or while the field is read-only.
	 * <p>
	 * Example: single-select with value {@code [EXTERNAL]} → value {@code EXTERNAL}; value {@code EXTERNAL} or
	 * {@code null} → unchanged.
	 */
	private void unwrapSetValue() {

		Object value = getValue();
		if (value instanceof Set && !isReadOnly()) {
			super.setValue(getFirstValue(value));
		}
	}

	@Override
	public void setMultiSelect(boolean multiSelect) {
		throw new UnsupportedOperationException();
	}

	public Object getNullableValue() {
		final Object value = super.getValue();
		return ObjectUtils.isNotEmpty(value) ? getFirstValue(value) : null;
	}

	public void setNullableValue(Object value) {
		if (value != null) {
			super.setValue(java.util.Collections.singleton(value));
		} else {
			super.setValue(null);
		}
	}

	@Override
	public void setRequired(boolean required) {
		boolean readOnly = isReadOnly();
		setReadOnly(false);
		super.setRequired(required);
		setup();
		setReadOnly(readOnly);
	}

	/**
	 * Returns the first option of a set value, or the value itself if it is not a set (single-select mode).
	 * <p>
	 * Example: {@code [YES]} → {@code YES}; {@code []} → {@code null}; {@code EXTERNAL} → {@code EXTERNAL};
	 * {@code null} → {@code null}.
	 */
	private Object getFirstValue(Object value) {
		if (value instanceof Set) {
			return ((Set<?>) value).stream().findFirst().orElse(null);
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public void setInaccessible() {
		this.removeAllItems();
		Item item = this.addItem(1);

		if (item == null) {
			return;
		}

		if (item.getItemPropertyIds().isEmpty()) {
			return;
		}

		item.getItemProperty(item.getItemPropertyIds().stream().findFirst().get()).setValue(I18nProperties.getCaption(Captions.inaccessibleValue));

	}
}
