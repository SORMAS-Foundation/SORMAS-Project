package de.symeda.auditlog.api.value;

import java.io.Serializable;
import java.util.SortedMap;

import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * Sammelt die für das Auditlog zu auditierende Attribute (Key-Value-Paare).
 * 
 * @author Oliver Milke, Stefan Kock
 */
public interface ValueContainer extends Serializable {

	/**
	 * Platzhalter, wenn ein geändertes Attribut auf <code>null</code> gesetzt wurde.
	 */
	String DEFAULT_NULL_STRING = "[null]";

	/**
	 * @return Die bisher gespeicherten Attribute.
	 */
	SortedMap<String, String> getAttributes();

	/**
	 * @return Platzhalter, wenn ein geändertes Attribut auf <code>null</code> gesetzt wurde.
	 */
	default String getNullString() {
		return DEFAULT_NULL_STRING;
	}

	/**
	 * Vergleicht den Zustand mit einer älteren Version.
	 * 
	 * @param originalState
	 *            Der Original-Zustand eines Entities.
	 * @return <ul>
	 *         <li>Liefert eine Liste von Attributen, deren Werte abweichen von der ursprünglichen Version.</li>
	 *         <li>Liefert eine leere Map, wenn beide Zustände identisch sind.</li>
	 *         </ul>
	 */
	SortedMap<String, String> compare(ValueContainer originalState);

	/**
	 * @return Liefert eine Liste von Attributen, deren Werte abweichen von der ursprünglichen Version,
	 *         wie sie als Änderung ausgegeben/gespeichert werden sollen.
	 */
	SortedMap<String, String> getChanges();

	/**
	 * Speichert den zu auditierenden String.
	 * 
	 * @param key
	 *            Identifier für Attribut der auditierten Entity.
	 * @param value
	 *            Wenn {@code null}, dann wird {@link #getNullString()} als Wert gespeichert.
	 */
	void put(String key, String value);

	/**
	 * Speichert den zu auditierenden Wert.
	 * 
	 * @param key
	 *            Identifier für Attribut der auditierten Entity.
	 * @param value
	 *            Wenn {@code null}, dann wird {@link #getNullString()} als Wert gespeichert.
	 * @param valueFormatter
	 *            Formatiert den übergebenen {@code value}, wenn er nicht {@code null} ist.
	 */
	<V> void put(String key, V value, ValueFormatter<V> valueFormatter);
}
