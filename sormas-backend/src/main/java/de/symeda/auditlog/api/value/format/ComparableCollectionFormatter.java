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
 * 			Type of the {@code value} to format within the {@link Collection}.
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
	 * 			{@link ValueFormatter} to be used to format the individual values.
	 */
	public ComparableCollectionFormatter(ValueFormatter<V> valueFormatter) {

		super(ValueContainer.DEFAULT_NULL_STRING, valueFormatter);
	}

	/**
	 * @param nullString
	 *      	Placeholder for {@code null} values.
	 * @param valueFormatter
	 * 			{@link ValueFormatter} to be used to format the individual values.
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
