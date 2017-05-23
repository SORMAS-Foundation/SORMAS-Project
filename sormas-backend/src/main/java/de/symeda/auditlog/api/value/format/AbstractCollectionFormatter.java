package de.symeda.auditlog.api.value.format;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Formatiert eine Collection von Enums als String.<br />
 * Die Methode {@link #toSortedList(Collection)} stellt eine zuverlässige Sortierung sicher,
 * sodass eine Umsortierung nicht als geänderter formatierter String erscheint.<br />
 * Formatierung:
 * <ul>
 * <li><code>null</code>: <code>{@link #getNullString()}</code></li>
 * <li>0 Werte: <code>0 []</code></li>
 * <li>1 Wert: <code>1 [value1]</code></li>
 * <li>N Werte: <code>n [value1, ..., valueN]</code> (sortiert gemäß <code>{@link #toSortedList(Collection)}</code>)</li>
 * </ul>
 * 
 * @author Stefan Kock
 * @param <V>
 *            Typ des zu formatierenden {@code value} innerhalb der {@link Collection}.
 */
public abstract class AbstractCollectionFormatter<V> implements CollectionFormatter<V> {

	private final String nullString;
	private final ValueFormatter<V> valueFormatter;

	/**
	 * <code>nullString = {@link ValueContainer#DEFAULT_NULL_STRING}</code>
	 * 
	 * @param valueFormatter
	 *            Zur Formatierung der einzelnen Werte zu verwendende {@link ValueFormatter}.
	 */
	public AbstractCollectionFormatter(ValueFormatter<V> valueFormatter) {

		this(ValueContainer.DEFAULT_NULL_STRING, valueFormatter);
	}

	/**
	 * @param nullString
	 *            Platzhalter für {@code null}-Values.
	 * @param valueFormatter
	 *            Zur Formatierung der einzelnen Werte zu verwendende {@link ValueFormatter}.
	 */
	public AbstractCollectionFormatter(String nullString, ValueFormatter<V> valueFormatter) {

		this.nullString = nullString;
		this.valueFormatter = valueFormatter;
	}

	@Override
	public String getNullString() {
		return nullString;
	}

	@Override
	public String format(Collection<V> valueCollection) {

		final StringBuilder sb = new StringBuilder();

		sb.append(valueCollection.size());
		sb.append(" ");
		sb.append(PREFIX);

		Iterator<V> iterator = toSortedList(valueCollection).iterator();
		while (iterator.hasNext()) {
			V value = iterator.next();
			sb.append(value == null ? getNullString() : valueFormatter.format(value));
			if (iterator.hasNext()) {
				sb.append(SEPARATOR);
			}
		}

		sb.append(SUFFIX);

		return sb.toString();
	}

	/**
	 * Sortiert die zu formatierende {@link Collection} vor der Formatierung.
	 * Es wird eine zuverlässige Sortierung sichergestellt,
	 * sodass eine Umsortierung nicht als geänderter formatierter String erscheint.
	 * 
	 * @param valueCollection
	 *            Zu formatierende {@link Collection}.
	 * @return Sortierte Values.
	 */
	protected abstract List<V> toSortedList(Collection<V> valueCollection);
}
