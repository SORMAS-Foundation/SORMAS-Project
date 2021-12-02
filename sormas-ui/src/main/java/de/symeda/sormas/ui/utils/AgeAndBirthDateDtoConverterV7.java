package de.symeda.sormas.ui.utils;

import java.util.Locale;

import com.vaadin.v7.data.util.converter.Converter;

import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.person.PersonHelper;

public class AgeAndBirthDateDtoConverterV7 implements Converter<String, AgeAndBirthDateDto> {

	private static final long serialVersionUID = 4515813046485937433L;

	@Override
	public AgeAndBirthDateDto convertToModel(String value, Class<? extends AgeAndBirthDateDto> targetType, Locale locale) throws ConversionException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String convertToPresentation(AgeAndBirthDateDto value, Class<? extends String> targetType, Locale locale) throws ConversionException {

		if (value == null) {
			return "";
		}

		return PersonHelper.getAgeAndBirthdateString(
			value.getAge(),
			value.getAgeType(),
			value.getDateOfBirthDD(),
			value.getDateOfBirthMM(),
			value.getDateOfBirthYYYY());
	}

	@Override
	public Class<AgeAndBirthDateDto> getModelType() {
		return AgeAndBirthDateDto.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
