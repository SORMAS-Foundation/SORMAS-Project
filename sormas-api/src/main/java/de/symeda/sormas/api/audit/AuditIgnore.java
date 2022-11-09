package de.symeda.sormas.api.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark classes and methods in classes which should be excluded
 * from auditing (e.g., b/c of unnecessary noise).
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.TYPE,
	ElementType.METHOD })
public @interface AuditIgnore {
}
