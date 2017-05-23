package de.symeda.auditlog.api.value.format.override;

import java.lang.reflect.Method;

import de.symeda.auditlog.api.value.format.ValueFormatter;

public interface OverrideDetector<T> {

	/**
	 * Liefert einen {@link ValueFormatter}, der angepasst ist auf das Entity-Property.
	 * 
	 * @param m
	 *            Die Methode, die das Entity-Property beschreibt.
	 * @return Liefert <code>null</code>, falls keine sinnvolle Ableitung gefunden werden kann. Liefert einen entsprechenden
	 *         {@link ValueFormatter}, falls eine Ableitung beispielsweise anhand der Annotationen möglich ist.
	 */
	ValueFormatter<T> override(Method m);

}