package de.symeda.sormas.ui;

import java.util.Comparator;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

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
		return Comparator.nullsLast(Comparator.comparing(keyExtractor, enumNameComparator()));
	}

	public static <T extends Enum<?>> Comparator<T> enumNameComparator() {
		return Comparator.nullsLast(Comparator.comparing(Enum::name, StringUtils::compare));
	}
}
