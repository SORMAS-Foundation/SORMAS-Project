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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Formats a Collection of {@link Comparable}s as String.<br />
 * The reliable sorting is provided by the natural order defined by {@link Comparable}.<br />
 * <u>Caution:</u> {@code null} values within the Collection are not supported.
 * 
 * @author Stefan Kock
 * @param <V>
 *            Type of the {@code value} to format within the {@link Collection}.
 */
public class ComparableCollectionFormatter<V extends Comparable<V>> extends AbstractCollectionFormatter<V> {

	/**
	 * <ul>
	 * <li><code>nullString = {@link ValueContainer#DEFAULT_NULL_STRING}</code></li>
	 * <li><code>valueFormatter = value.toString()</code></li>
	 * <ul>
	 */
	public ComparableCollectionFormatter() {

		super(ValueContainer.DEFAULT_NULL_STRING, value -> value.toString());
	}

	/**
	 * <ul>
	 * <li><code>nullString = {@link ValueContainer#DEFAULT_NULL_STRING}</code></li>
	 * <ul>
	 * 
	 * @param valueFormatter
	 *            {@link ValueFormatter} to be used to format the individual values.
	 */
	public ComparableCollectionFormatter(ValueFormatter<V> valueFormatter) {

		super(ValueContainer.DEFAULT_NULL_STRING, valueFormatter);
	}

	/**
	 * @param nullString
	 *            Placeholder for {@code null} values.
	 * @param valueFormatter
	 *            {@link ValueFormatter} to be used to format the individual values.
	 */
	public ComparableCollectionFormatter(String nullString, ValueFormatter<V> valueFormatter) {

		super(nullString, valueFormatter);
	}

	@Override
	protected List<V> toSortedList(Collection<V> valueCollection) {

		ArrayList<V> sortedList = new ArrayList<>(valueCollection);
		Collections.sort(sortedList);

		return sortedList;
	}
}
