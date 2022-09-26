package de.symeda.sormas.api.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark DTO fields which should be included in the audit log. It can be applied to fields
 * and, if need be, to methods returning a specifically crafted audit representation. The audit logger will pick up the
 * field if the containing class is annotated with {@link AuditedClass}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.FIELD,
	ElementType.METHOD })
public @interface AuditInclude {
}
