package de.symeda.sormas.ui.utils;

import java.util.Date;

import com.vaadin.server.SerializableComparator;
import de.symeda.sormas.api.caze.CaseIndexDto;

public class VaadinSortComparatorUtils {

	private VaadinSortComparatorUtils() {
	}

	public static <T> SerializableComparator<T> comparator() {
		return new SerializableComparatorImpl<>();
	}

	public static final class SerializableComparatorImpl<T> implements SerializableComparator<T> {

		@Override
		public int compare(T o1, T o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}

			if (o1 instanceof Date || o2 instanceof Date) {
				throw new IllegalArgumentException("Unexpected Date type");
//				return ((Date) o1).compareTo((Date) o2);
			}

			if (o1 instanceof CaseIndexDto || o2 instanceof CaseIndexDto) {
				throw new IllegalArgumentException("Unexpected CaseIndexDto type");
			}

			throw new IllegalArgumentException(String.format("Unexpected Object type: %s, %s", o1, o2));

//			return o1.toString().compareTo(o2.toString());
		}
	}
}
