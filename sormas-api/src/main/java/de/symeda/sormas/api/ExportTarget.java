package de.symeda.sormas.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines which {@link ExportType}s the annotated field will be used for. If an export type is
 * chosen that is not in this list, the field will not appear in the result.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExportTarget {

	ExportType[] exportTypes() default ExportType.CASE_SURVEILLANCE;
	
}
