package de.symeda.sormas.ui.utils;

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import com.vaadin.v7.data.util.converter.Converter;

class RadioOptionGroupConverter implements Converter<Object, Object> {

	Object oldValue;

	public RadioOptionGroupConverter(Object initialValue) {
		this.oldValue = initialValue;
	}

	@Override
	public Object convertToModel(Object ts, Class<? extends Object> aClass, Locale locale) throws ConversionException {

		Set newValue = (Set) ts;

		if (newValue != null && !newValue.isEmpty()) {
			switch (newValue.size()) {
			case 1:
				oldValue = newValue.iterator().next();
				break;
			case 2:
				oldValue = oldValue == null
					? newValue.stream().filter(v -> !Objects.equals(v, oldValue)).reduce((first, second) -> second).orElse(null)
					: newValue.stream().filter(v -> !Objects.equals(v, oldValue)).findFirst().get();
				break;

			default:
				throw new IllegalArgumentException(newValue.toString());
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
