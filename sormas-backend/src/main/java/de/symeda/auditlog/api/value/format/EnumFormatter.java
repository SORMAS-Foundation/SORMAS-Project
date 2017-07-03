package de.symeda.auditlog.api.value.format;

import java.io.Serializable;

import de.symeda.auditlog.api.value.SimpleValueContainer;

/**
 * Formats Enums:
 * <ol>
 * <li>{@code null} -> {@link SimpleValueContainer.DEFAULT_NULL_STRING}</li>
 * <li>{@code value.name()}</li>
 * 
 * @author Oliver Milke
 */
public class EnumFormatter implements ValueFormatter<Enum<?>>, Serializable {

	private static final long serialVersionUID = 4528994776862288356L;

	@Override
	public String format(Enum<?> value) {

		if (value == null) {
			return SimpleValueContainer.DEFAULT_NULL_STRING;
		} else {
			return value.name();
		}
	}
}