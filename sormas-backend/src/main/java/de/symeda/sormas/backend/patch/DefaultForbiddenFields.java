package de.symeda.sormas.backend.patch;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * List it too big to keep directly within {@link PatchFieldHelper}.
 */
public class DefaultForbiddenFields {

	private DefaultForbiddenFields() {
	}

	private static final Set<String> DEFAULT_FORBIDDEN_FIELDS = buildDefaultForbiddenFieldsList();

	private static Set<String> buildDefaultForbiddenFieldsList() {
		Set<String> forbiddenFields = new HashSet<>();

		// TECHNICAL
		forbiddenFields.add(".uuid");
		forbiddenFields.add(".creationDate");
		forbiddenFields.add(".changeDate");
		forbiddenFields.add(".pseudonymized");
		forbiddenFields.add(".inJurisdiction");

		// lifecycle
		forbiddenFields.add(".deleted");
		forbiddenFields.add(".archived");
		forbiddenFields.add(".deletionReason");
		forbiddenFields.add(".otherDeletionReason");

		// users
		forbiddenFields.add(".reportingUser");
		forbiddenFields.add(".surveillanceOfficer");
		forbiddenFields.add(".classificationUser");
		forbiddenFields.add(".classifiedBy");
		forbiddenFields.add(".classificationDate");

		// references
		forbiddenFields.add("Immunization.relatedCase");
		forbiddenFields.add("Immunization.person");
		forbiddenFields.add("Vaccination.immunization");

		// PERSON
		forbiddenFields.add("Person.birthdate");
		forbiddenFields.add("Person.birthdateDD");
		forbiddenFields.add("Person.birthdateMM");
		forbiddenFields.add("Person.birthdateYYYY");

		return Collections.unmodifiableSet(forbiddenFields);
	}

	public static Set<String> getDefaultForbiddenFields() {
		return DEFAULT_FORBIDDEN_FIELDS;
	}
}
