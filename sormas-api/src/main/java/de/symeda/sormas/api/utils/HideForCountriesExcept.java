package de.symeda.sormas.api.utils;

import de.symeda.sormas.api.CountryHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.FIELD,
	ElementType.METHOD })
public @interface HideForCountriesExcept {

	String[] countries() default {
			CountryHelper.COUNTRY_CODE_GERMANY };
}
