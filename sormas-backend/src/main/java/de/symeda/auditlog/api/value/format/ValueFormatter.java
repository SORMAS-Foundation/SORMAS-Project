package de.symeda.auditlog.api.value.format;

/**
 * Formatiert den übergebenen Wert als String.
 * 
 * @author Stefan Kock
 * @param <V>
 *            Typ des zu formatierenden {@code value}.
 */
public interface ValueFormatter<V> {

	/**
	 * Formatiert den übergebenen Wert als {@link String}.
	 * 
	 * @param value
	 *            Zu formatierender Wert.
	 * @return String-Formatierung des übergebenen Werts.
	 */
	String format(V value);
}
