package de.symeda.sormas.api.utils;

import de.symeda.sormas.api.feature.FeatureType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.METHOD,
	ElementType.FIELD })
public @interface DependingOnFeatureType {

	FeatureType featureType();

}
