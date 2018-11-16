package de.symeda.sormas.ui.utils;

import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("serial")
public class DateConverter implements Converter<Date, Date> {
	 
		@Override
		public Date convertToModel(Date value, Class<? extends Date> targetType, Locale locale) throws ConversionException {
			return TemporalCalculator.toCorrectCentury(value);
		}
	 
		@Override
		public Date convertToPresentation(Date value, Class<? extends Date> targetType, Locale locale) throws ConversionException {
			return value;
		}
	 
		@Override
		public Class<Date> getModelType() {
			return Date.class;
		}
	 
		@Override
		public Class<Date> getPresentationType() {
			return Date.class;
		}
}