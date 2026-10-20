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

import de.symeda.sormas.api.exposure.TravelPurpose;

/**
 * MALA {@code PurposeOfTravel} mapping from {@code exposures.travelpurpose}.
 *
 * <p>
 * This is effectively a rename of {@link TravelPurpose} values. {@code UNKNOWN} is intentionally
 * unmapped (blank), not {@code OTH}.
 */
public enum EpipulsePurposeOfTravelRef {

	BUSINESS(TravelPurpose.BUSINESS),
	ENDTRAV(TravelPurpose.TRAVELER_FROM_ENDEMIC_COUNTRY),
	FAM(TravelPurpose.VISITING_FAMILY_FRIENDS),
	MIG(TravelPurpose.MIGRATION),
	MIL(TravelPurpose.MILITARY),
	MISSION(TravelPurpose.HUMANITARIAN_MISSION),
	OTH(TravelPurpose.OTHER),
	STUD(TravelPurpose.STUDENT),
	TOUR(TravelPurpose.TOURISM);

	private final TravelPurpose travelPurpose;

	EpipulsePurposeOfTravelRef(TravelPurpose travelPurpose) {
		this.travelPurpose = travelPurpose;
	}

	public TravelPurpose getTravelPurpose() {
		return travelPurpose;
	}

	/** Returns EpiPulse code for mapped purpose, otherwise {@code null}. */
	public static String codeFor(TravelPurpose travelPurpose) {

		if (travelPurpose == null) {
			return null;
		}

		for (EpipulsePurposeOfTravelRef ref : values()) {
			if (ref.travelPurpose == travelPurpose) {
				return ref.name();
			}
		}

		return null;
	}
}
