package de.symeda.auditlog.api.sample;

import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * Demo class for the exemplary formatting of a Boolean alien from {@link String#valueOf(boolean)}.
 * 
 * @author Oliver Milke
 * @since 08.04.2016
 */
public class DemoBooleanFormatter implements ValueFormatter<Boolean> {

	@Override
	public String format(Boolean value) {

		return "This is the proof that this Formatter is actually used because no other Formatter would use such an absurd Boolean -> String conversion.";
	}
}