package de.symeda.sormas.ui;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Warning by default Vaadin uses {@link Comparable#compareTo( Object)}
 * By default, enums are sorted using {@link Enum#ordinal()}, see {@link Enum#compareTo( Enum)} which is really a bad pick for sorting,
 * provides some utility methods to simplify this endeavor.
 * <p>
 * Could be further enhanced by using: Vaadin's SerializableComparator class, but
 */
public class EnumSortUtils {

	private EnumSortUtils() {
	}

	public static <T, U extends Enum<?>> Comparator<T> comparingOnEnumField(Function<? super T, ? extends U> keyExtractor) {
		return (o1, o2) -> enumNameComparator().compare(keyExtractor.apply(o1), keyExtractor.apply(o2));
	}

	public static <T extends Enum<?>> Comparator<T> enumNameComparator() {
		return Comparator.comparing(Enum::name, String::compareToIgnoreCase);
	}
}
