package de.symeda.auditlog.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.Temporal;
import java.util.Date;

import de.symeda.auditlog.api.value.format.DefaultValueFormatter;
import de.symeda.auditlog.api.value.format.UtilDateFormatter;
import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * Determines that attributes of an entity should be checked for changes by the AuditLog.
 * 
 * @author Oliver Milke
 * @since 08.04.2016
 */
@Target({
		ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditedAttribute {

	Class<? extends ValueFormatter<?>> DEFAULT_FORMATTER = DefaultValueFormatter.class;

	/**
	 * Standard placeholder for changed anonymized attributes.
	 */
	String ANONYMIZING = "****";

	/**
	 * Determines the formatter the inspected attribute will be formatted with. This String representation is also the foundation for
	 * the discovery of a change.
	 * 
	 * @return Default: {@link DefaultValueFormatter}; if a {@link Date} is annotated with {@link Temporal}, a matching {@link UtilDateFormatter} will be used.
	 */
	Class<? extends ValueFormatter<?>> value() default DefaultValueFormatter.class;

	/**
	 * Determines whether the changed value may actually be written into the AuditLog. E.g. passwords should not appear in the AuditLog,
	 * even when they are hashed.
	 * <p/>
	 * This can be used to e.g. track when users have changed their passwords without saving the actual value in the AuditLog.
	 * 
	 * @return Default: {@code false}.
	 */
	boolean anonymous() default false;

	/**
	 * The values of anonymized entries will be replaced by this String in the AuditLog.
	 * 
	 * @return Default: {@value #ANONYMIZING}.
	 */
	String anonymizingString() default ANONYMIZING;

}
