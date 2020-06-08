package de.symeda.sormas.ui.utils;

import java.util.Locale;

import com.vaadin.v7.data.util.converter.Converter;

import de.symeda.sormas.api.therapy.PeriodDto;

public class PeriodDtoConverter implements Converter<String, PeriodDto> {

	private static final long serialVersionUID = 4119205056759462799L;

	@Override
	public PeriodDto convertToModel(String value, Class<? extends PeriodDto> targetType, Locale locale) throws ConversionException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String convertToPresentation(PeriodDto value, Class<? extends String> targetType, Locale locale) throws ConversionException {
		return value == null ? "" : DateFormatHelper.buildPeriodString(value.getStart(), value.getEnd());
	}

	@Override
	public Class<PeriodDto> getModelType() {
		return PeriodDto.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
