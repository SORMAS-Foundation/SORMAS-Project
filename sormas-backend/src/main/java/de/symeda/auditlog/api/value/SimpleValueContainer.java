package de.symeda.auditlog.api.value;

import java.util.Date;

/**
 * Formatiert typische Java-Objekte und sammelt die für das Auditlog zu auditierende Attribute (Key-Value-Paare).
 * 
 * @author Stefan Kock
 */
public interface SimpleValueContainer extends ValueContainer {

	/**
	 * Speichert den zu auditierenden Boolean.
	 * 
	 * @param key
	 *            Identifier für Attribut der auditierten Entity.
	 * @param value
	 *            Wenn {@code null}, dann wird {@link #getNullString()} als Wert gespeichert.
	 */
	void put(String key, Boolean value);

	/**
	 * Speichert den zu auditierenden {@link Enum}.
	 * 
	 * @param key
	 *            Identifier für Attribut der auditierten Entity.
	 * @param value
	 *            Wenn {@code null}, dann wird {@link #getNullString()} als Wert gespeichert.
	 */
	void put(String key, Enum<?> value);

	/**
	 * Speichert die zu auditierende {@link Number}.
	 * 
	 * @param key
	 *            Identifier für Attribut der auditierten Entity.
	 * @param value
	 *            Wenn {@code null}, dann wird {@link #getNullString()} als Wert gespeichert.
	 */
	void put(String key, Number value);

	/**
	 * Speichert das zu auditierende {@link Date}.
	 * 
	 * @param key
	 *            Identifier für Attribut der auditierten Entity.
	 * @param date
	 *            Wenn {@code null}, dann wird {@link #getNullString()} als Wert gespeichert.
	 * @param datePattern
	 *            Regel zur Formatierung des übergebenen {@code date}.
	 */
	void put(String key, Date date, String datePattern);
}
