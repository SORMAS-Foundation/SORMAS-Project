package de.symeda.auditlog.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that the class provided with this annotation should be audited.
 * </p>
 * Every instantiated object whose class is marked with the {@link Audited} annotation and all super classes with {@link Audited} will
 * be taken into account.
 * Should the depth of inheritance contain an element that is not marked with {@link Audited}, this element and all its attributes
 * will be skipped.
 * 
 * @author Oliver Milke
 * @since 08.04.2016
 */
@Target({
		ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audited {

}
