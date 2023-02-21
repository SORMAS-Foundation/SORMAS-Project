package de.symeda.sormas.api.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark DTO as auditable. This annotation being present causes the log line to contain at
 * least the class name. If more information is required, the fields to be included in the log line can be marked with
 * {@link AuditIncludeProperty}. If `includeAllFields` is set to true, all fields will be included in the log line.
 * In this case, {@Link AuditExclude} annotation can be used to opt-out fields which should not be included.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AuditedClass {
    public boolean includeAllFields() default false;
}
