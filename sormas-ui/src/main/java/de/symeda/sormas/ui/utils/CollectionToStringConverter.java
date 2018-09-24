package de.symeda.sormas.ui.utils;

import java.util.Collection;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * A converter that allows displaying a collection as a comma separated list of
 * strings.
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class CollectionToStringConverter implements
        Converter<String, Collection> {

    @Override
    public Collection convertToModel(String value,
            Class<? extends Collection> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        throw new UnsupportedOperationException(
                "Can only convert from Collection to String");
    }

    @Override
    public String convertToPresentation(Collection value,
            Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null)
            return "";
        StringBuilder b = new StringBuilder();
        for (Object o : value) {
            b.append(o.toString());
            b.append(", ");
        }
        return b.substring(0, b.length() - 2);

    }

    @Override
    public Class<Collection> getModelType() {
        return Collection.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
