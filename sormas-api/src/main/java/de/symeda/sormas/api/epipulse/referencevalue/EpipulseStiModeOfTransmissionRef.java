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

package de.symeda.sormas.api.epipulse.referencevalue;

import de.symeda.sormas.api.epidata.ProbableRouteOfTransmission;

/**
 * STI-group {@code ModeOfTransmission} mapping from
 * {@code epidata.probablerouteoftransmission}.
 *
 * <p>
 * CHLAM/GONO/LGV/SYPH share one four-code set, unlike most subject-code-specific transmission
 * sets.
 *
 * <p>
 * {@code UNKNOWN} is intentionally unmapped (blank), not {@code OTH}. {@code MSM} reflects the
 * recorded route classification, not inferred identity.
 */
public enum EpipulseStiModeOfTransmissionRef {

	HETERO(ProbableRouteOfTransmission.HETEROSEXUAL_CONTACT),
	MSM(ProbableRouteOfTransmission.MSM_HOMO_OR_BISEXUAL_MALE),
	MTCT(ProbableRouteOfTransmission.MOTHER_TO_CHILD_TRANSMISSION),
	OTH(ProbableRouteOfTransmission.OTHER);

	private final ProbableRouteOfTransmission route;

	EpipulseStiModeOfTransmissionRef(ProbableRouteOfTransmission route) {
		this.route = route;
	}

	public ProbableRouteOfTransmission getRoute() {
		return route;
	}

	/** Returns EpiPulse code for mapped route, otherwise {@code null}. */
	public static String codeFor(ProbableRouteOfTransmission route) {

		if (route == null) {
			return null;
		}

		for (EpipulseStiModeOfTransmissionRef ref : values()) {
			if (ref.route == route) {
				return ref.name();
			}
		}

		return null;
	}
}
