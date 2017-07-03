package de.symeda.auditlog.api.value.format;

/**
 * Formats the given value as String.
 * 
 * @author Stefan Kock
 * @param <V>
 *      	Type of the {@code value} to format.
 */
public interface ValueFormatter<V> {

	/**
	 * Formats the given value as String.
	 * 
	 * @param value
	 *        	Value to format.
	 * @return 	String format of the given value.
	 */
	String format(V value);
}
