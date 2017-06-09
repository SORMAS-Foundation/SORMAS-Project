package de.symeda.auditlog.api.sample;

/**
 * Enum with adjusted toString method that does not return {@link Enum#name()}.

 * @author Stefan Kock
 */
public enum CustomEnum {

	VALUE,
	VALUE_1,
	VALUE_2,
	VALUE_3;

	@Override
	public String toString() {
		return "customToStringLabel";
	}
}