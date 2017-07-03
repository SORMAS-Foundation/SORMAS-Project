package de.symeda.auditlog.api.value.format;

import java.util.Collection;

import de.symeda.auditlog.api.value.ValueContainer;

/**
 * Formats Collections as String for the Auditlog.<br />
 * Should {@code null} values be present, {@link #getNullString()} will be used as placeholder.
 * 
 * @author Stefan Kock
 * @param <V>
 * 			Type of the {@code value} to format within the {@link Collection}.
 */
public interface CollectionFormatter<V> extends ValueFormatter<Collection<V>> {

	/**
	 * Standard character as first sign of a formatted {@link Collection}.
	 */
	char PREFIX = '[';

	/**
	 * Standard separator between values of a formatted {@link Collection}.
	 */
	String SEPARATOR = ";";

	/**
	 * Standard character as last character of a formatted {@link Collection}.
	 */
	char SUFFIX = ']';

	/**
	 * @return	Placeholder if a value of the {@link Collection} has been set to <code>null</code>.
	 */
	default String getNullString() {
		return ValueContainer.DEFAULT_NULL_STRING;
	}

	/**
	 * Formats the {@link Collection} as {@link String}.
	 * 
	 * @param valueCollection
	 * 			{@link Collection} to format.
	 * @return 	String format of the given {@link Collection}.
	 */
	@Override
	String format(Collection<V> valueCollection);
}
