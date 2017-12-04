package de.symeda.auditlog.api.value.format;

import java.io.Serializable;
import java.util.Comparator;

import de.symeda.sormas.api.HasUuid;

/**
 * Supports the following elements in Collections (relevant for a reliable sorting):
 * <ol>
 * <li>{@link HasUuid}</li>
 * <li>{@link Comparable}</li>
 * </ol>
 * By default, {@link DefaultValueFormatter} will be used to format Collection elements.
 * 
 * @author Oliver Milke, Stefan Kock
 */
public class DefaultCollectionFormatter extends ComparatorCollectionFormatter<Object> {

	public DefaultCollectionFormatter() {
		super(new EntityFallbackComparator(), new DefaultValueFormatter());
	}

	public DefaultCollectionFormatter(ValueFormatter<Object> valueFormatter) {
		super(new EntityFallbackComparator(), valueFormatter);
	}

	/**
	 * Compares objects according to their natural order, with {@link HasUuid} being taken into account.
	 * 
	 * @author Oliver Milke
	 * @since 11.04.2016
	 */
	private static final class EntityFallbackComparator implements Comparator<Object>, Serializable {

		private static final long serialVersionUID = -3752241106639147382L;

		@Override
		@SuppressWarnings({
				"rawtypes",
				"unchecked" })
		public int compare(Object o1, Object o2) {

			if (o1 instanceof HasUuid && o2 instanceof HasUuid) {

				HasUuid uuidObject1 = (HasUuid) o1;
				HasUuid uuidObject2 = (HasUuid) o2;
				return uuidObject1.getUuid().compareTo(uuidObject2.getUuid());
			} else {

				// Caution: Will produce a ClassCastException for classes that are not Comparable.
				Comparator comparator = Comparator.naturalOrder();
				return comparator.compare(o1, o2);
			}
		}
	}
}
