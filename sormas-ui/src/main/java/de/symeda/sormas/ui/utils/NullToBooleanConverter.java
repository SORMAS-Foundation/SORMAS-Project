package de.symeda.sormas.ui.utils;


import java.util.Locale;
import com.vaadin.v7.data.util.converter.Converter;

public class NullToBooleanConverter implements Converter<Boolean, Boolean> {


  @Override
  public Boolean convertToModel(Boolean o, Class<? extends Boolean> aClass, Locale locale)
      throws ConversionException {
    if(o == null){
      return false;
    }

    return o;
  }

  @Override
  public Boolean convertToPresentation(Boolean aBoolean, Class<? extends Boolean> aClass, Locale locale)
      throws ConversionException {
    return aBoolean;
  }

  @Override
  public Class<Boolean> getModelType() {
    return Boolean.class;
  }

  @Override
  public Class<Boolean> getPresentationType() {
    return Boolean.class;
  }
}
