package de.symeda.sormas.api.patch;

public enum DataPatchFailureCause {
	/**
	 * Invalid field name was provided that cannot be matched with an existing field.
	 */
	FIELD_DOES_NOT_EXIST,

	/**
	 * Might occur if following patch config was used: {@link DataReplacementType#IF_NOT_ALREADY_PRESENT}.
	 */
	CURRENT_VALUE_NOT_OVERRIDDEN,

	/**
	 * Example: Expected number but got "a".
	 */
	INVALID_VALUE_TYPE,

	/**
	 * This means there is a hole in the implementation.
	 */
	TECHNICAL

}
