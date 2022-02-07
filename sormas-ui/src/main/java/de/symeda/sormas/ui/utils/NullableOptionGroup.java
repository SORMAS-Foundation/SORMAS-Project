/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.OptionGroup;

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

	private void setup() {
		super.setMultiSelect(!isRequired());
		if (isRequired()) {
			setConverter((Converter<Object, ?>) null);
		} else {
			setConverter(new NullableOptionGroupConverter(getFirstValue((Set) getValue())));
		}
		addStyleName("multiline");
	}

	@Override
	public void setMultiSelect(boolean multiSelect) {
		throw new UnsupportedOperationException();
	}

	public Object getNullableValue() {
		final Object value = super.getValue();
		return ObjectUtils.isNotEmpty(value) ? getFirstValue((Set) value) : null;
	}

	@Override
	public void setRequired(boolean required) {
		boolean readOnly = isReadOnly();
		setReadOnly(false);
		super.setRequired(required);
		setup();
		setReadOnly(readOnly);
	}

	private Object getFirstValue(Set value) {
		return value.stream().findFirst().orElse(null);
	}
}
