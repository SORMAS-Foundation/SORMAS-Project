package de.symeda.auditlog.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker-Annotation, dass die mit dieser Annotation versehene Klasse auditiert werden soll.
 * </p>
 * Jedes instanzierte Objekt, bei dem die Klasse mit {@link Audited} annotiert ist und alle Oberklassen mit {@link Audited} werden in der
 * Auditierung berücksichtigt.
 * Sollte in der Vererbungshierarchie ein Element vorhanden sein, dass nicht mit {@link Audited} annotiert ist, dann dies (und damit alle
 * seine Attribute) übersprungen.
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
