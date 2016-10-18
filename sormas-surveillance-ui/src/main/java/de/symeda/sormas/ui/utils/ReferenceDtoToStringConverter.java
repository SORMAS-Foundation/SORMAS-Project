package de.symeda.sormas.ui.utils;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

import de.symeda.sormas.api.ReferenceDto;

@SuppressWarnings("serial")
public final class ReferenceDtoToStringConverter implements Converter<String, ReferenceDto> {
	@Override
	public ReferenceDto convertToModel(String value, Class<? extends ReferenceDto> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
        throw new UnsupportedOperationException(
                "Can only convert from ReferenceDto to String");
	}

	@Override
	public String convertToPresentation(ReferenceDto value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null) {
			return "";
		}
		return value.getUuid();
	}

	@Override
	public Class<ReferenceDto> getModelType() {
		return ReferenceDto.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}