package de.symeda.sormas.api.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to opt-out fields which should not be included in the audit log although
 * {@link AuditInclude}.includeAllFields is set.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.FIELD,
	ElementType.METHOD })
public @interface AuditExclude {

}
