package de.symeda.sormas.app.backend.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Explicitely used for person-location association because location is an embedded entity without a
 * parent accessor, but has a many-to-one relation with persons; this makes sure that the same logic
 * that would be applied to a field of the distinct parent accessor is also applied to the field
 * annotated with this annotation.
 */
@Target({
	ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinTableReference {
}
