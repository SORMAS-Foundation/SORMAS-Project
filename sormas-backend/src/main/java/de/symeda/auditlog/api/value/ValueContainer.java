package de.symeda.auditlog.api.value;

import java.io.Serializable;
import java.util.SortedMap;

import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * Collects the attributes to be audited for the Auditlog (key-value pairs).
 * 
 * @author Oliver Milke, Stefan Kock
 */
public interface ValueContainer extends Serializable {

	/**
	 * Placeholder when a changed attribute is set to <code>null</code>.
	 */
	String DEFAULT_NULL_STRING = "[null]";

	/**
	 * @return	The saved attributes so far.
	 */
	SortedMap<String, String> getAttributes();

	/**
	 * @return	Placeholder when a changed attribute is set to <code>null</code>.
	 */
	default String getNullString() {
		return DEFAULT_NULL_STRING;
	}

	/**
	 * Compares the state with an older version.
	 * 
	 * @param originalState
	 * 			The original state of an entity.
	 * @return	<ul>
	 * 			<li>Returns a list of attributes with values that differ from the original version.</li>
	 * 			<li>Returns an empty map when both states are identical.</li>
	 * 			</ul>
	 */
	SortedMap<String, String> compare(ValueContainer originalState);

	/**
	 * @return	Returns a list of attributes with values that differ from the original version,
	 * 			the way they have to be put out as changes/saved.
	 */
	SortedMap<String, String> getChanges();

	/**
	 * Saves the String to audit.
	 * 
	 * @param key
	 * 			Identifier for attribute of the audited entity.
	 * @param value
	 * 			If {@code null}, the {@link #getNullString()} will be saved as the value.
	 */
	void put(String key, String value);
	
	/**
	 * Saves the value to audit.
	 * 
	 * @param key
	 * 			Identifier for attribute of the audited entity.
	 * @param value
	 * 			If {@code null}, the {@link #getNullString()} will be saved as the value.
	 * @param valueFormatter
	 * 			Formats the given {@code value} if it is not {@code null}.
	 */
	<V> void put(String key, V value, ValueFormatter<V> valueFormatter);
}
