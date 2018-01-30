package de.symeda.sormas.ui.utils;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class StringToAnythingConverter<T> implements Converter<String, T> {
	
	private Class<T> modelType;
	
	public StringToAnythingConverter(Class<T> modelType) {
		this.modelType = modelType;
	}
	
	@Override
	public T convertToModel(String value, Class<? extends T> targetType, Locale locale) throws ConversionException {
		return null;
	}
	
	@Override
	public String convertToPresentation(T value, Class<? extends String> targetType, Locale locale) throws ConversionException {
		return value == null ? null : value.toString();
	}

	@Override
	public Class<T> getModelType() {
		return modelType;
	}
	
	public Class<String> getPresentationType() {
		return String.class;
	}
	
}
