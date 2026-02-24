package de.symeda.sormas.api.patch;

public enum DataPatchFailureCause {

	/**
	 * Occurs if input tries to use multiple fields approach: CaseData.(symptoms.onsetDate|hospitalization.admissionDate).
	 */
	INVALID_MULTIPLE_FIELDS_FORMAT,

	/**
	 * Path does not start with the allowed prefixes: example: CaseData or Person.
	 */
	UNSUPPORTED_PREFIX,

	/**
	 * Invalid field name was provided that cannot be matched with an existing field.
	 */
	FIELD_DOES_NOT_EXIST,

	/**
	 * Some fields are not meant to be patched: per example technical fields like UUID.
	 */
	FORBIDDEN_FIELD,

	/**
	 * Can occur if following patch config was set: {@link DataReplacementStrategy#IF_NOT_ALREADY_PRESENT}.
	 * Occurs only if the value is different of the current. No error if value stays the same.
	 */
	FORBIDDEN_VALUE_OVERRIDE,

	/**
	 * Example: Expected number but got "a".
	 */
	INVALID_VALUE_TYPE,

	/**
	 * This means there is a hole in the implementation.
	 */
	TECHNICAL

}
