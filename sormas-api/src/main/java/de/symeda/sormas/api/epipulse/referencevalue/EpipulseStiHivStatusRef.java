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

import de.symeda.sormas.api.clinicalcourse.HivStatus;

/**
 * STI-group {@code HIVStatus} mapping from {@code healthconditions.hivstatus}.
 *
 * <p>
 * CHLAM/GONO/LGV/SYPH share this four-code set; other subject codes use different
 * {@code HIVStatus} value sets.
 *
 * <p>
 * {@code POSKNOWN}/{@code POSNEW} preserve the recorded distinction between known and newly
 * diagnosed positivity. Unknown is intentionally blank.
 */
public enum EpipulseStiHivStatusRef {

	NEG(HivStatus.NEGATIVE),
	POS(HivStatus.POSITIVE),
	POSKNOWN(HivStatus.KNOWN_POSITIVE),
	POSNEW(HivStatus.NEW_DIAGNOSIS);

	private final HivStatus hivStatus;

	EpipulseStiHivStatusRef(HivStatus hivStatus) {
		this.hivStatus = hivStatus;
	}

	public HivStatus getHivStatus() {
		return hivStatus;
	}

	/** Returns EpiPulse code for mapped HIV status, otherwise {@code null}. */
	public static String codeFor(HivStatus hivStatus) {

		if (hivStatus == null) {
			return null;
		}

		for (EpipulseStiHivStatusRef ref : values()) {
			if (ref.hivStatus == hivStatus) {
				return ref.name();
			}
		}

		return null;
	}
}
