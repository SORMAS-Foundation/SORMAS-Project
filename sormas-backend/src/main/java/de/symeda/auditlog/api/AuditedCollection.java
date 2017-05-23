package de.symeda.auditlog.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import javax.persistence.Embeddable;

import de.symeda.auditlog.api.value.format.CollectionFormatter;
import de.symeda.auditlog.api.value.format.DefaultCollectionFormatter;
import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * Legt für eine {@link Collection} eines Entity fest, dass seine Elemente durch das AuditLog auf Änderungen überprüft werden.
 * <p/>
 * Nicht anwendbar für Collection-Attribute innerhalb von {@link Embeddable}.
 * 
 * @author Oliver Milke
 * @since 11.04.2016
 */
@Target({
		ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditedCollection {

	/**
	 * Legt den Formatter für die Elemente der Collection fest, durch den diese formatiert werden soll.
	 * Diese String-Repräsentation ist auch Basis für die Feststellung einer Änderung.
	 * </p>
	 * Mögliche Verwendungen:
	 * <ol>
	 * <li>Default: {@link DefaultCollectionFormatter}.</li>
	 * <li>Anderer {@link CollectionFormatter}, der einen eigenen {@link ValueFormatter} zur Formatierung der Collection-Elemente enthält.
	 * </li>
	 * <li>Ein {@link ValueFormatter} zur Formatierung der Collection-Elemente. Die Sortierung wird durch {@link DefaultCollectionFormatter}
	 * sichergestellt.</li>
	 * </ol>
	 */
	Class<? extends ValueFormatter<?>> value() default DefaultCollectionFormatter.class;
}
