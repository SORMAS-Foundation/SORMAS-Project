package de.symeda.sormas.api.patch.partial_retrieval;

public enum PartialRetrievalFailureCause {
	/**
	 * Occurs if input tries to use multiple fields approach: CaseData.(symptoms.onsetDate|hospitalization.admissionDate).
	 */
	INVALID_MULTIPLE_FIELDS_FORMAT,

	/**
	 * Alias cannot be mapped to a single physical path.
	 * Path aliases base on the "Field ID" field from the generated data dictionary can be used to shorten the physical path.
	 */
	FORBIDDEN_NON_UNIQUE_ALIAS,

	/**
	 * Occurs the field is not supported by the disease / country / feature.
	 * Error message must be somewhat generic to specify the Data Dictionary should be checked.
	 */
	UNSUPPORTED_FIELD_FOR_DISEASE_OR_COUNTRY_OR_FEATURE,

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
	 * This means there is a hole in the implementation.
	 */
	TECHNICAL
}
