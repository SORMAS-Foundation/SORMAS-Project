/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.sample;

/**
 * The kind(s) of result a pathogen-test method produces, taken from the "Value Type" column of the
 * approved test list. A method may combine several (e.g. RT-PCR is {@code QUALITATIVE} + {@code
 * NUMERIC}). The pathogen-test result form uses this to decide which result fields to show: the
 * qualitative Positive/Negative selector, a numeric value with unit, a free-text value, a smear
 * grade, an antibiotic susceptibility interpretation or a Western Blot interpretation.
 *
 * <p>
 * Declared per method via {@link ResultValueTypeRel} and read with
 * {@link PathogenTestType#getResultValueTypes(PathogenTestType)}. Methods without the annotation are
 * treated as {@code QUALITATIVE} only — the long-standing behaviour, so existing tests are unaffected.
 */
public enum ResultValueType {

	/** Positive / Negative / Indeterminate qualitative result (the classic {@code PathogenTestResultType}). */
	QUALITATIVE,
	/** A numeric value with an optional unit (Ct value, reciprocal titre, CFU/mL, cell count, …). */
	NUMERIC,
	/** A free-text value (organism name, sequence, lineage, morphology description, …). */
	TEXT,
	/** A detected/not-detected boolean, represented with the qualitative Positive/Negative selector. */
	BOOLEAN,
	/** A graded smear reading (Acid-Fast: negative / + / ++ / +++). */
	SMEAR_GRADE,
	/** A Western Blot band-pattern interpretation. */
	WESTERN_BLOT
}
