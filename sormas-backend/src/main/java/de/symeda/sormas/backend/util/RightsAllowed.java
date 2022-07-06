package de.symeda.sormas.backend.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mimics behaviour of @RightsAllowed to workaround performance problem.
 */
@Documented
@Retention(RUNTIME)
@Target({
	TYPE,
	METHOD })
public @interface RightsAllowed {

	/**
	 * List of rights that are permitted access.
	 */
	String[] value();
}
