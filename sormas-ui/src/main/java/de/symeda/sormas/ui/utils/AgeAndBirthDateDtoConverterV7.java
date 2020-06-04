package de.symeda.sormas.ui.utils;

import com.vaadin.v7.data.util.converter.Converter;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonHelper;

import java.util.Locale;

public class AgeAndBirthDateDtoConverterV7 implements Converter<String, AgeAndBirthDateDto> {

	@Override
	public AgeAndBirthDateDto convertToModel(String value, Class<? extends AgeAndBirthDateDto> targetType, Locale locale) throws ConversionException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String convertToPresentation(AgeAndBirthDateDto value, Class<? extends String> targetType, Locale locale) throws ConversionException {
		if (value == null) {
			return "";
		}

		Language userLanguage = I18nProperties.getUserLanguage();

		return PersonHelper.getAgeAndBirthdateString(value.getAge(), value.getAgeType(), value.getBirthdateDD(), value.getBirthdateMM(), value.getBirthdateYYYY(), userLanguage);
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
