package de.symeda.auditlog.api.value.format;

import java.util.Objects;

import de.symeda.auditlog.api.AuditedAttribute;
import de.symeda.auditlog.api.value.DefaultValueContainer;
import de.symeda.auditlog.api.value.SimpleValueContainer;
import de.symeda.auditlog.api.value.ValueContainer;
import de.symeda.sormas.api.HasUuid;

/**
 * Default formatter for {@link AuditedAttribute} that supports the following types:
 * <ol>
 * <li>{@link Enum} -> {@code value.name()}</li>
 * <li>{@link HasUuid} -> {@code value.getUuid()}</li>
 * <li>{@code null} -> {@link ValueContainer#DEFAULT_NULL_STRING}</li>
 * <li>Others -> {@code value.toString()}</li>
 * </ol>
 * <p/>
 * Implementation is compatible to the implementations in {@link DefaultValueContainer}.
 * 
 * @author Oliver Milke
 * @since 08.04.2016
 */
public class DefaultValueFormatter implements ValueFormatter<Object> {

	private final EnumFormatter enumFormatter = new EnumFormatter();

	@Override
	public String format(Object value) {

		if (value instanceof Enum) {
			return enumFormatter.format((Enum<?>) value);
		} else if (value instanceof HasUuid) {
			HasUuid uuidObject = (HasUuid) value;

			// never null because of instanceof
			return uuidObject.getUuid();
		} else {
			return Objects.toString(value, SimpleValueContainer.DEFAULT_NULL_STRING);
		}
	}
}
