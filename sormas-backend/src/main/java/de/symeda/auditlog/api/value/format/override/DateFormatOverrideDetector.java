package de.symeda.auditlog.api.value.format.override;

import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.Temporal;

import de.symeda.auditlog.api.value.format.UtilDateFormatter;
import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * @author Oliver Milke
 * @since 11.04.2016
 */
public class DateFormatOverrideDetector implements OverrideDetector<Date> {

	/**
	 * Liefert für ein Entity-Property einen angepassten {@link ValueFormatter}, sofern sich das aus der Methoden-Beschreibung ableiten
	 * lässt.
	 * 
	 * @param m
	 *            Die Methode, die das Property beschreibt.
	 * @return Liefert <code>null</code>, falls keine sinnvolle Ableitung festgestellt werden kann. Liefert einen ValueFormatter der
	 *         angepasst ist auf den Wert der {@link Temporal}-Annotation, falls vorhanden.
	 */
	@Override
	public ValueFormatter<Date> override(Method m) {

		if (!Date.class.isAssignableFrom(m.getReturnType())) {
			//keine sinnvolle Ableitung möglich
			return null;
		} else {
			//gründsätzlich möglich
			Temporal annotation = m.getAnnotation(Temporal.class);

			if (annotation == null) {
				return new UtilDateFormatter();
			} else {
				return new UtilDateFormatter(annotation.value());
			}
		}
	}
}
