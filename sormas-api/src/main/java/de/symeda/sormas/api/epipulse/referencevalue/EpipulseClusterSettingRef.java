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

import de.symeda.sormas.api.epidata.ClusterType;

/**
 * The {@code ClusterSetting} variable: the setting where a case's cluster occurred.
 *
 * <p>
 * Declared by MEAS, MUMP, and RUBE for these eight values, and by DIPH for the same eight plus
 * {@code DET} (migrant detention centre), which no {@link ClusterType} constant represents. DIPH
 * would need a SORMAS field before it could report DET. Currently emitted by MEAS.
 *
 * <p>
 * The enum constant name is the EpiPulse code, as elsewhere in this package.
 *
 * <p>
 * {@code ClusterType.NOT_APPLICABLE} and {@code ClusterType.UNKNOWN} deliberately map to nothing.
 * 
 */
public enum EpipulseClusterSettingRef {

	CHILDCARE(ClusterType.KINDERGARTEN_OR_CHILDCARE),
	FAM(ClusterType.FAMILY),
	MIL(ClusterType.MILITARY),
	NOS(ClusterType.NOSOCOMIAL),
	SCH(ClusterType.SCHOOL),
	SPORT(ClusterType.SPORTS_TEAM),
	UNI(ClusterType.UNIVERSITY),
	OTH(ClusterType.OTHER);

	private final ClusterType clusterType;

	EpipulseClusterSettingRef(ClusterType clusterType) {
		this.clusterType = clusterType;
	}

	public ClusterType getClusterType() {
		return clusterType;
	}

	/** The code for a recorded cluster setting, or {@code null} for one EpiPulse cannot express. */
	public static String codeFor(ClusterType clusterType) {

		if (clusterType == null) {
			return null;
		}

		for (EpipulseClusterSettingRef ref : values()) {
			if (ref.clusterType == clusterType) {
				return ref.name();
			}
		}

		return null;
	}
}
