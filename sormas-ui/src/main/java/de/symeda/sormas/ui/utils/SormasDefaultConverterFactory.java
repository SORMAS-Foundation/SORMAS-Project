package de.symeda.sormas.ui.utils;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.data.util.converter.StringToEnumConverter;

@SuppressWarnings("serial")
public final class SormasDefaultConverterFactory extends DefaultConverterFactory {
	@Override
	protected Converter<String, ?> createStringConverter(Class<?> sourceType) {
		
		if (Enum.class.isAssignableFrom(sourceType)) {
	        return new StringToEnumConverter() {
	            @SuppressWarnings("rawtypes")
				@Override
	            public String convertToPresentation(Enum value,
	                    Class<? extends String> targetType, Locale locale)
	                    throws ConversionException {
	                if (value == null) {
	                    return null;
	                }

	                return SormasDefaultConverterFactory.enumToString(value, locale);
	            }
	        };
		}
		return super.createStringConverter(sourceType);
	}
	
    public static String enumToString(Enum<?> value, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        String enumString = value.toString();
        // we don't want to have this part of Vaadin magic
//        if (enumString.equals(value.name())) {
//            // FOO -> Foo
//            // FOO_BAR -> Foo bar
//            // _FOO -> _foo
//            String result = enumString.substring(0, 1).toUpperCase(locale);
//            result += enumString.substring(1).toLowerCase(locale).replace('_',
//                    ' ');
//            return result;
//        } else 
        {
            return enumString;
        }
    }
}