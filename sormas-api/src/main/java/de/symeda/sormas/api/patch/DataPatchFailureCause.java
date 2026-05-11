package de.symeda.sormas.api.patch;

/**
 * Reason a specific field couldn't be patched.
 */
public enum DataPatchFailureCause {

	/**
	 * Occurs if input tries to use multiple fields approach: CaseData.(symptoms.onsetDate|hospitalization.admissionDate).
	 */
	INVALID_MULTIPLE_FIELDS_FORMAT,

	/**
	 * Some fields require a particular handling and are handled as group, to trigger the handling some fields are mandatory.
	 */
	MISSING_MANDATORY_FIELD_FOR_GROUP,

	/**
	 * Alias cannot be mapped to a single physical path.
	 * Path aliases base on the "Field ID" field from the generated data dictionary can be used to shorten the physical path.
	 */
	FORBIDDEN_NON_UNIQUE_ALIAS,

	/**
	 * Some fields have only a specific list of allowed values, if not present and no fallback then fails. Examples:
	 * - {@link de.symeda.sormas.api.customizableenum.CustomizableEnum}
	 * - {@link Enum}
	 * - {@link de.symeda.sormas.api.ReferenceDto}
	 */
	NOT_PRESENT_IN_REFERENCE_DATA_LIST,

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
	 * Can occur if following patch config was set: {@link DataReplacementStrategy#IF_NOT_ALREADY_PRESENT}.
	 * Occurs only if the value is different of the current. No error if value stays the same.
	 */
	FORBIDDEN_VALUE_OVERRIDE,

	/**
	 * Example: Expected number but got "a".
	 */
	INVALID_VALUE_TYPE,

	/**
	 * A mapper is missing and must be implemented.
	 */
	UNSUPPORTED_TARGET_TYPE,

	INVALID_PATH_FORMAT,

	/**
	 * The path contains the {@code _duplicate_} marker, meaning the key appeared more than once in the input and this is the duplicate
	 * occurrence.
	 * Duplicate entries cannot be processed because their intended target is ambiguous.
	 */
	DUPLICATE_FIELD,

	/**
	 * This means there is a hole in the implementation.
	 */
	TECHNICAL

}
