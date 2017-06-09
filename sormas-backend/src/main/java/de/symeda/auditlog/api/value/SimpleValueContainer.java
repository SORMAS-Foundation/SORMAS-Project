package de.symeda.auditlog.api.value;

import java.util.Date;

/**
 * Formats typical Java objects and collects the attributes to be audited for the Auditlog (key-value pairs).
 * 
 * @author Stefan Kock
 */
public interface SimpleValueContainer extends ValueContainer {

	/**
	 * Saves the Boolean to audit.
	 * 
	 * @param key
	 * 			Identifier for attribute of the audited entity.
	 * @param value
	 * 			If {@code null}, the {@link #getNullString()} will be saved as the value.
	 */
	void put(String key, Boolean value);

	/**
	 * Saves the {@link Enum} to audit.
	 * 
	 * @param key
	 * 			Identifier for attribute of the audited entity.
	 * @param value
	 * 			If {@code null}, the {@link #getNullString()} will be saved as the value.
	 */
	void put(String key, Enum<?> value);

	/**
	 * Saves the {@link Number} to audit.
	 * 
	 * @param key
	 * 			Identifier for attribute of the audited entity.
	 * @param value
	 * 			If {@code null}, the {@link #getNullString()} will be saved as the value.
	 */
	void put(String key, Number value);

	/**
	 * Saves the {@link Date} to audit.
	 * 
	 * @param key
	 * 			Identifier for attribute of the audited entity.
	 * @param value
	 * 			If {@code null}, the {@link #getNullString()} will be saved as the value.
	 */
	void put(String key, Date date, String datePattern);
}
