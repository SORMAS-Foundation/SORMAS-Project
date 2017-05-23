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
 * Legt fest, dass Attribute eines Entity durch das AuditLog auf Änderungen überprüft werden.
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
	 * Standard-Platzhalter für geänderte anonymisierte Attribute.
	 */
	String ANONYMIZING = "****";

	/**
	 * Legt den Formatter fest, durch den das inspizierte Attribut formatiert werden soll. Diese String-Repräsentation ist auch Basis für
	 * die Feststellung einer Änderung.
	 * 
	 * @return Default: {@link DefaultValueFormatter}, außer wenn {@link Date} mit {@link Temporal} annotiert ist, dann wird ein passender {@link UtilDateFormatter} verwendet.
	 */
	Class<? extends ValueFormatter<?>> value() default DefaultValueFormatter.class;

	/**
	 * Legt fest, ob der geänderte Wert tatsächlich ins AuditLog geschrieben werden darf. Passworte sollte nicht bspw. nicht im AuditLog
	 * erscheinen, auch wenn sie gehasht sind.
	 * <p/>
	 * Das dient dazu, um nachzuverfolgen, dass beispielsweise ein Benutzer sein Passwort geändert hat, ohne dass das konkrete Passwort im
	 * AuditLog gespeichert wird.
	 * 
	 * @return Default: {@code false}.
	 */
	boolean anonymous() default false;

	/**
	 * Mit dieser Zeichenkette erscheinen anonyme Einträge im AuditLog.
	 * 
	 * @return Default: {@value #ANONYMIZING}.
	 */
	String anonymizingString() default ANONYMIZING;

}
