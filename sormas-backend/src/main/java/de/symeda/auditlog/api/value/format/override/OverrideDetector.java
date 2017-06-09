package de.symeda.auditlog.api.value.format.override;

import java.lang.reflect.Method;

import de.symeda.auditlog.api.value.format.ValueFormatter;

public interface OverrideDetector<T> {

	/**
	 * Returns a {@link ValueFormatter} that is adjusted to the entity property.
	 * 
	 * @param m
	 * 			The method that describes the entity property.
	 * @return	Returns <code>null</code> if no reasonable derivation can be found. Returns a respective {@link ValueFormatter}
	 * 			if a derivation is possible, e.g. according to the annotations.
	 */
	ValueFormatter<T> override(Method m);

}