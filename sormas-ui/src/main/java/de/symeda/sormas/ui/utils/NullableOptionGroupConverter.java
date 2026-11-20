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

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import com.vaadin.v7.data.util.converter.Converter;

class NullableOptionGroupConverter implements Converter<Object, Object> {

	private Object oldValue;

	public NullableOptionGroupConverter(Object initialValue) {
		this.oldValue = initialValue;
	}

	/**
	 * Converts the set of options checked in the browser to the single model value. The option group is a multi-select
	 * under the hood, so a click on a second option temporarily checks two options; the newly clicked one wins.
	 * <p>
	 * When the user clicks several options faster than the server answers, the browser sends more than two checked options
	 * at once (#14427), or a set that no longer contains the previous value. Every set with two or more options is handled
	 * the same way: the value is the last option in the set's iteration order that differs from the previous value; if
	 * there is none, the previous value is kept.
	 * <p>
	 * Example: previous value {@code UNKNOWN}, the browser sends {@code [UNKNOWN, NO, YES]} → returns {@code YES} (the
	 * last of {@code NO, YES}); the browser sends {@code [NO, YES]} → also {@code YES}. After the round trip only that
	 * option stays checked.
	 */
	@Override
	public Object convertToModel(Object ts, Class<? extends Object> aClass, Locale locale) throws ConversionException {

		if (!(ts instanceof Set)) {
			// null means nothing selected; a single non-set value is not expected from a multi-select option group,
			// but is kept as the value instead of failing with a ClassCastException
			oldValue = ts;
			return oldValue;
		}

		Set<?> newValue = (Set<?>) ts;

		if (!newValue.isEmpty()) {
			switch (newValue.size()) {
			case 1:
				oldValue = newValue.iterator().next();
				break;

			default:
				// two options: a regular click on another option; more options: rapid clicks before the server answered
				// (#14427). Keep a newly clicked one, or the previous value if nothing new is in the set
				Object previousValue = oldValue;
				// map to Object: orElse(previousValue) does not compile on a stream of the wildcard type of Set<?>
				oldValue = newValue.stream()
					.map(Object.class::cast)
					.filter(v -> !Objects.equals(v, previousValue))
					.reduce((first, second) -> second)
					.orElse(previousValue);
			}
		} else {
			oldValue = null;
		}
		return oldValue;
	}

	@Override
	public Object convertToPresentation(Object t, Class<? extends Object> aClass, Locale locale) throws ConversionException {
		if (t != null) {
			return Collections.singleton(t);
		} else {
			return null;
		}
	}

	@Override
	public Class<Object> getModelType() {
		return Object.class;
	}

	@Override
	public Class<Object> getPresentationType() {
		return Object.class;
	}
}
