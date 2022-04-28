package de.symeda.sormas.api.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;

@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.METHOD,
	ElementType.FIELD })
public @interface DependingOnFeatureType {

	FeatureType featureType();

	FeatureProperty[] properties() default {};

	boolean hide() default false;

	@interface FeatureProperty {

		FeatureTypeProperty property();

		String value() default "";
	}
}
