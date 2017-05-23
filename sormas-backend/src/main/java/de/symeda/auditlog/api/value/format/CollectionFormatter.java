package de.symeda.auditlog.api.value.format;

import java.util.Collection;

import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Formatiert Collections als String für das Auditlog.<br />
 * Sollten {@code null}-Values vorhanden sein, dann wird {@link #getNullString()} als Platzhalter verwendet.
 * 
 * @author Stefan Kock
 * @param <V>
 *            Typ des zu formatierenden {@code value} innerhalb der {@link Collection}.
 */
public interface CollectionFormatter<V> extends ValueFormatter<Collection<V>> {

	/**
	 * Standard-Zeichen als erste Zeichen für eine formatierte {@link Collection}.
	 */
	char PREFIX = '[';

	/**
	 * Standard-Trennzeichen zwischen Werten einer formatierten {@link Collection}.
	 */
	String SEPARATOR = ";";

	/**
	 * Standard-Zeichen als letzte Zeichen für eine formatierte {@link Collection}.
	 */
	char SUFFIX = ']';

	/**
	 * @return Platzhalter, wenn ein Wert der {@link Collection} auf <code>null</code> gesetzt wurde.
	 */
	default String getNullString() {
		return ValueContainer.DEFAULT_NULL_STRING;
	}

	/**
	 * Formatiert die {@link Collection} als {@link String}.
	 * 
	 * @param valueCollection
	 *            Zu formatierende {@link Collection}.
	 * @return String-Formatierung der übergebenen {@link Collection}.
	 */
	@Override
	String format(Collection<V> valueCollection);
}
