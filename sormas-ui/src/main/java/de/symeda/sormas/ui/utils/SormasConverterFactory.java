package de.symeda.sormas.ui.utils;

import java.util.Date;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

@SuppressWarnings("serial")
public class SormasConverterFactory extends DefaultConverterFactory {

	private static final DateConverter DATE_CONVERTER = new DateConverter();
	
	@Override
	protected Converter<Date, ?> createDateConverter(Class<?> sourceType) {
		if (Date.class == sourceType) {
			return DATE_CONVERTER;
		}
 
		return super.createDateConverter(sourceType);
	}
	
}
