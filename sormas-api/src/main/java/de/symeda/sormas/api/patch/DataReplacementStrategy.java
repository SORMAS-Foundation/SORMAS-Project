package de.symeda.sormas.api.patch;

public enum DataReplacementStrategy {
	/**
	 * No matter what the current value is, it will be replaced with the provided value.
	 */
	ALWAYS,

	/**
	 * New value will not be applied if there already is a value for the specified field.
	 */
	IF_NOT_ALREADY_PRESENT
}
