package de.symeda.auditlog.api.value.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Formatiert eine Collection von Enums als String.
 * Der übergebene {@link Comparator} stellt eine zuverlässige Sortierung sicher,
 * sodass Umsortierungen nicht als geänderter formatierter String erscheinen.
 * 
 * @author Stefan Kock
 * @param <V>
 *            Typ des zu formatierenden {@code value} innerhalb der {@link Collection}.
 */
public class ComparatorCollectionFormatter<V> extends AbstractCollectionFormatter<V> {

	private final Comparator<V> comparator;

	/**
	 * <code>nullString = {@link ValueContainer#DEFAULT_NULL_STRING}</code>
	 * 
	 * @param comparator
	 *            Zur stabilen Sortierung der Werte verwendete {@link Comparator}.
	 * @param valueFormatter
	 *            Zur Formatierung der einzelnen Werte zu verwendende {@link ValueFormatter}.
	 */
	public ComparatorCollectionFormatter(Comparator<V> comparator, ValueFormatter<V> valueFormatter) {

		this(ValueContainer.DEFAULT_NULL_STRING, comparator, valueFormatter);
	}

	/**
	 * @param nullString
	 *            Platzhalter für {@code null}-Values.
	 * @param comparator
	 *            Zur stabilen Sortierung der Werte verwendete {@link Comparator}.
	 * @param valueFormatter
	 *            Zur Formatierung der einzelnen Werte zu verwendende {@link ValueFormatter}.
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
