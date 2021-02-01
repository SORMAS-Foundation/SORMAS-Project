package de.symeda.sormas.api.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.symeda.sormas.api.CountryHelper;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HideForCountries {

	String[] countries() default {
		CountryHelper.COUNTRY_CODE_GERMANY };
}
