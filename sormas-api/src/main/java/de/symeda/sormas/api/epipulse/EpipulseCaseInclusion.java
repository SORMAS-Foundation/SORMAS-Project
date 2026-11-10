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
 */
public final class EpipulseCaseInclusion {

	/**
	 * Confirmed, probable and possible classification codes.
	 */
	private static final Set<EpipulseCaseClassificationRef> CONFIRMED_PROBABLE_POSSIBLE = Collections
		.unmodifiableSet(EnumSet.of(EpipulseCaseClassificationRef.CONF, EpipulseCaseClassificationRef.PROB, EpipulseCaseClassificationRef.POSS));

	/**
	 * Confirmed and probable classification codes.
	 */
	private static final Set<EpipulseCaseClassificationRef> CONFIRMED_PROBABLE =
		Collections.unmodifiableSet(EnumSet.of(EpipulseCaseClassificationRef.CONF, EpipulseCaseClassificationRef.PROB));

	private static final Map<EpipulseSubjectCode, Set<EpipulseCaseClassificationRef>> NOTIFIED_CODES = notifiedCodes();

	private EpipulseCaseInclusion() {
	}

	private static Map<EpipulseSubjectCode, Set<EpipulseCaseClassificationRef>> notifiedCodes() {

		Map<EpipulseSubjectCode, Set<EpipulseCaseClassificationRef>> notified = new EnumMap<>(EpipulseSubjectCode.class);

		// TUBE has no CaseClassification column in metadata; rule comes from TB specification.
		notified.put(EpipulseSubjectCode.TUBE, CONFIRMED_PROBABLE_POSSIBLE);
		// DENGUE's CaseClassification value set in metadata is CONF and PROB only.
		notified.put(EpipulseSubjectCode.DENGUE, CONFIRMED_PROBABLE);

		return Collections.unmodifiableMap(notified);
	}

	/**
	 * The SORMAS classifications this subject code notifies.
	 *
	 * @return classifications to keep; empty means no narrowing for that subject code
	 */
	public static Set<CaseClassification> notifiedBy(EpipulseSubjectCode subjectCode) {

		Set<EpipulseCaseClassificationRef> codes = NOTIFIED_CODES.get(subjectCode);

		return codes == null ? Collections.emptySet() : EpipulseCaseClassificationRef.caseClassificationsOf(codes);
	}

	/**
	 * Whether this subject code applies a classification-based narrowing.
	 */
	public static boolean isNarrowed(EpipulseSubjectCode subjectCode) {
		return NOTIFIED_CODES.containsKey(subjectCode);
	}
}
