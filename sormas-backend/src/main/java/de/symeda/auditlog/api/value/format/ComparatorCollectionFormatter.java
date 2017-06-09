package de.symeda.auditlog.api.value.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Formats a Collection of Enums as String.
 * The given {@link Comparator} provides a reliable sorting so that sorting it
 * again does not result in a changed formatted String.
 * 
 * @author Stefan Kock
 * @param <V>
 * 			Type of the {@code value} to format within the {@link Collection}.
 */
public class ComparatorCollectionFormatter<V> extends AbstractCollectionFormatter<V> {

	private final Comparator<V> comparator;

	/**
	 * <code>nullString = {@link ValueContainer#DEFAULT_NULL_STRING}</code>
	 * 
	 * @param comparator
	 * 			{@link Comparator} used for a reliable sorting of the values.
	 * @param valueFormatter
	 * 			{@link ValueFormatter} to be used to format the individual values.
	 */
	public ComparatorCollectionFormatter(Comparator<V> comparator, ValueFormatter<V> valueFormatter) {

		this(ValueContainer.DEFAULT_NULL_STRING, comparator, valueFormatter);
	}

	/**
	 * @param nullString
				Placeholder for {@code null} values.
	 * @param comparator
	 * 			{@link Comparator} used for a reliable sorting of the values.
	 * @param valueFormatter
	 * 			{@link ValueFormatter} to be used to format the individual values.
	 */
	public ComparatorCollectionFormatter(String nullString, Comparator<V> comparator, ValueFormatter<V> valueFormatter) {

		super(nullString, valueFormatter);
		this.comparator = comparator;
	}

	@Override
	protected List<V> toSortedList(Collection<V> valueCollection) {

		ArrayList<V> sortedList = new ArrayList<>(valueCollection);
		Collections.sort(sortedList, comparator);

		return sortedList;
	}
}
