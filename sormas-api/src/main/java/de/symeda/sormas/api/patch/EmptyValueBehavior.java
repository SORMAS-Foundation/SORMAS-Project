package de.symeda.sormas.api.patch;

/**
 * Defines how Empty values: null or "" (empty string) should be taken into account during patch operation.
 */
public enum EmptyValueBehavior {
	IGNORE,
	REPLACE
}
