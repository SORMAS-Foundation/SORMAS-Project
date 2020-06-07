/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
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
	 * @return The saved attributes so far.
	 */
	SortedMap<String, String> getAttributes();

	/**
	 * @return Placeholder when a changed attribute is set to <code>null</code>.
	 */
	default String getNullString() {
		return DEFAULT_NULL_STRING;
	}

	/**
	 * Compares the state with an older version.
	 * 
	 * @param originalState
	 *            The original state of an entity.
	 * @return
	 *         <ul>
	 *         <li>Returns a list of attributes with values that differ from the original version.</li>
	 *         <li>Returns an empty map when both states are identical.</li>
	 *         </ul>
	 */
	SortedMap<String, String> compare(ValueContainer originalState);

	/**
	 * @return Returns a list of attributes with values that differ from the original version,
	 *         the way they have to be put out as changes/saved.
	 */
	SortedMap<String, String> getChanges();

	/**
	 * Saves the String to audit.
	 * 
	 * @param key
	 *            Identifier for attribute of the audited entity.
	 * @param value
	 *            If {@code null}, the {@link #getNullString()} will be saved as the value.
	 */
	void put(String key, String value);

	/**
	 * Saves the value to audit.
	 * 
	 * @param key
	 *            Identifier for attribute of the audited entity.
	 * @param value
	 *            If {@code null}, the {@link #getNullString()} will be saved as the value.
	 * @param valueFormatter
	 *            Formats the given {@code value} if it is not {@code null}.
	 */
	<V> void put(String key, V value, ValueFormatter<V> valueFormatter);
}
