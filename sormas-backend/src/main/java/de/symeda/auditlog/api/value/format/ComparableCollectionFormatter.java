package de.symeda.auditlog.api.value.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Formatiert eine Collection von {@link Comparable}s als String.<br />
 * Die zuverlässige Sortierung wird durch die durch {@link Comparable} definierte natürliche Ordnung sichergestellt.<br />
 * <u>Achtung:</u> {@code null}-Values innerhalb der Collection werden nicht unterstützt.
 * 
 * @author Stefan Kock
 * @param <V>
 *            Typ des zu formatierenden {@code value} innerhalb der {@link Collection}.
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
	 *            Zur Formatierung der einzelnen Werte zu verwendende {@link ValueFormatter}.
	 */
	public ComparableCollectionFormatter(ValueFormatter<V> valueFormatter) {

		super(ValueContainer.DEFAULT_NULL_STRING, valueFormatter);
	}

	/**
	 * @param nullString
	 *            Platzhalter für {@code null}-Values.
	 * @param valueFormatter
	 *            Zur Formatierung der einzelnen Werte zu verwendende {@link ValueFormatter}.
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
