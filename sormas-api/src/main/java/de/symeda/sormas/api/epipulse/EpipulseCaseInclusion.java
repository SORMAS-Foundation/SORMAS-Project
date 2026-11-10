/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package de.symeda.sormas.api.epipulse;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseCaseClassificationRef;

/**
 * Which of a disease's cases a subject code notifies, by case classification.
 *
 * <p>
 * This is one of two filters used by {@code filtered_cases} (besides
 * {@link EpipulseCaseSubset}). It decides which classifications are notifiable for a subject code.
 * Subject codes without an entry here notify all cases of their disease.
 *
 * <p>
 * It also decides which codes the {@code CaseClassification} column may carry
 * ({@link #reports}), so a code is written only where it is also selected.
 */
public final class EpipulseCaseInclusion {

	/**
	 * Confirmed, probable and possible classification codes.
	 */
	private static final Set<EpipulseCaseClassificationRef> CONFIRMED_PROBABLE_POSSIBLE = Collections
		.unmodifiableSet(EnumSet.of(EpipulseCaseClassificationRef.CONF, EpipulseCaseClassificationRef.PROB, EpipulseCaseClassificationRef.POSS));

	/**
	 * Confirmed, probable, possible and discarded classification codes.
	 */
	private static final Set<EpipulseCaseClassificationRef> CONFIRMED_PROBABLE_POSSIBLE_DISCARDED = Collections.unmodifiableSet(
		EnumSet.of(
			EpipulseCaseClassificationRef.CONF,
			EpipulseCaseClassificationRef.PROB,
			EpipulseCaseClassificationRef.POSS,
			EpipulseCaseClassificationRef.DISCARDED));

	/**
	 * Confirmed classification code only.
	 */
	private static final Set<EpipulseCaseClassificationRef> CONFIRMED = Collections.unmodifiableSet(EnumSet.of(EpipulseCaseClassificationRef.CONF));

	/**
	 * Confirmed and probable classification codes.
	 */
	private static final Set<EpipulseCaseClassificationRef> CONFIRMED_PROBABLE =
		Collections.unmodifiableSet(EnumSet.of(EpipulseCaseClassificationRef.CONF, EpipulseCaseClassificationRef.PROB));

	private static final Map<EpipulseSubjectCode, Set<EpipulseCaseClassificationRef>> NOTIFIED_CODES = notifiedCodes();

	/**
	 * Subject codes whose rule also notifies a case not yet classified, with the
	 * {@code CaseClassification} column left blank. Only for a code whose EpiPulse description allows
	 * the column to be empty: SYPH's says it can "leave empty until case definitions are met".
	 */
	private static final Set<EpipulseSubjectCode> NOTIFIES_UNCLASSIFIED = Collections.unmodifiableSet(EnumSet.of(EpipulseSubjectCode.SYPH));

	private EpipulseCaseInclusion() {
	}

	private static Map<EpipulseSubjectCode, Set<EpipulseCaseClassificationRef>> notifiedCodes() {

		Map<EpipulseSubjectCode, Set<EpipulseCaseClassificationRef>> notified = new EnumMap<>(EpipulseSubjectCode.class);

		// TUBE has no CaseClassification column in metadata; rule comes from TB specification.
		notified.put(EpipulseSubjectCode.TUBE, CONFIRMED_PROBABLE_POSSIBLE);
		// SYPH's CaseClassification value set in metadata is CONF, PROB and POSS. EpiPulse expects
		// CONF at EU level, but accepts the other two until national case definitions catch up, and
		// an empty column for a case not yet classified (see NOTIFIES_UNCLASSIFIED).
		notified.put(EpipulseSubjectCode.SYPH, CONFIRMED_PROBABLE_POSSIBLE);
		// CONSYPH's value set is the same as SYPH's, but its description allows no empty column.
		notified.put(EpipulseSubjectCode.CONSYPH, CONFIRMED_PROBABLE_POSSIBLE);
		// DENGUE's CaseClassification value set in metadata is CONF and PROB only.
		notified.put(EpipulseSubjectCode.DENGUE, CONFIRMED_PROBABLE);
		// SALM's CaseClassification value set in metadata is CONF and PROB only.
		notified.put(EpipulseSubjectCode.SALM, CONFIRMED_PROBABLE);
		// SHIG's CaseClassification value set in metadata is CONF and PROB only.
		notified.put(EpipulseSubjectCode.SHIG, CONFIRMED_PROBABLE);
		// MALA's CaseClassification value set in metadata is CONF only.
		notified.put(EpipulseSubjectCode.MALA, CONFIRMED);
		// PNEU's CaseClassification value set in metadata is CONF only.
		notified.put(EpipulseSubjectCode.PNEU, CONFIRMED);
		// MEAS's CaseClassification value set in metadata is CONF, PROB, POSS and DISCARDED.
		notified.put(EpipulseSubjectCode.MEAS, CONFIRMED_PROBABLE_POSSIBLE_DISCARDED);
		// RUBE's CaseClassification value set in metadata is the same as MEAS's.
		notified.put(EpipulseSubjectCode.RUBE, CONFIRMED_PROBABLE_POSSIBLE_DISCARDED);

		return Collections.unmodifiableMap(notified);
	}

	/**
	 * The SORMAS classifications this subject code notifies.
	 *
	 * @return classifications to keep; empty means no narrowing for that subject code
	 */
	public static Set<CaseClassification> notifiedBy(EpipulseSubjectCode subjectCode) {

		Set<EpipulseCaseClassificationRef> codes = NOTIFIED_CODES.get(subjectCode);
		if (codes == null) {
			return Collections.emptySet();
		}

		Set<CaseClassification> classifications = EnumSet.copyOf(EpipulseCaseClassificationRef.caseClassificationsOf(codes));
		if (NOTIFIES_UNCLASSIFIED.contains(subjectCode)) {
			classifications.add(CaseClassification.NOT_CLASSIFIED);
		}

		return Collections.unmodifiableSet(classifications);
	}

	/**
	 * Whether this subject code writes the given code in its {@code CaseClassification} column.
	 */
	public static boolean reports(EpipulseSubjectCode subjectCode, EpipulseCaseClassificationRef code) {

		Set<EpipulseCaseClassificationRef> codes = subjectCode == null ? null : NOTIFIED_CODES.get(subjectCode);

		return (codes == null ? CONFIRMED_PROBABLE_POSSIBLE : codes).contains(code);
	}

	/**
	 * Whether this subject code applies a classification-based narrowing.
	 */
	public static boolean isNarrowed(EpipulseSubjectCode subjectCode) {
		return NOTIFIED_CODES.containsKey(subjectCode);
	}
}
