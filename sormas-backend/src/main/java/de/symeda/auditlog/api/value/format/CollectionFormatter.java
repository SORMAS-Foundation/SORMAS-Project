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
package de.symeda.auditlog.api.value.format;

import java.util.Collection;

import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Formats Collections as String for the Auditlog.<br />
 * Should {@code null} values be present, {@link #getNullString()} will be used as placeholder.
 * 
 * @author Stefan Kock
 * @param <V>
 *            Type of the {@code value} to format within the {@link Collection}.
 */
public interface CollectionFormatter<V> extends ValueFormatter<Collection<V>> {

	/**
	 * Standard character as first sign of a formatted {@link Collection}.
	 */
	char PREFIX = '[';

	/**
	 * Standard separator between values of a formatted {@link Collection}.
	 */
	String SEPARATOR = ";";

	/**
	 * Standard character as last character of a formatted {@link Collection}.
	 */
	char SUFFIX = ']';

	/**
	 * @return Placeholder if a value of the {@link Collection} has been set to <code>null</code>.
	 */
	default String getNullString() {
		return ValueContainer.DEFAULT_NULL_STRING;
	}

	/**
	 * Formats the {@link Collection} as {@link String}.
	 * 
	 * @param valueCollection
	 *            {@link Collection} to format.
	 * @return String format of the given {@link Collection}.
	 */
	@Override
	String format(Collection<V> valueCollection);
}
