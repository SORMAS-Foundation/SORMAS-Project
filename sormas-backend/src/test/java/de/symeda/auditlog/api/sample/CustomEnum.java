package de.symeda.auditlog.api.sample;

/**
 * Enum mit angepasster toString-Methode, die nicht {@link Enum#name()} ausgibt.
 * 
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