package de.symeda.sormas.ui.utils;

import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

import de.symeda.sormas.api.utils.DateHelper;

@SuppressWarnings("serial")
public class DateConverter implements Converter<Date, Date> {
	 
		@Override
		public Date convertToModel(Date value, Class<? extends Date> targetType, Locale locale) throws ConversionException {
			return DateHelper.toCorrectCentury(value);
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