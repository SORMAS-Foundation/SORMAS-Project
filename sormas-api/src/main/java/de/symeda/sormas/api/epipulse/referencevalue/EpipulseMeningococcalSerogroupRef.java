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

import de.symeda.sormas.api.sample.SeroGroupSpecification;

/**
 * MENI {@code Serogroup} reference mapping.
 *
 * <p>
 * {@code Serogroup} value sets are subject-code specific; this enum covers meningococcus only.
 * Source values come from shared {@link SeroGroupSpecification}, so {@link #codeFor} also filters
 * out serogroups from other diseases.
 */
public enum EpipulseMeningococcalSerogroupRef {

	NEIMENI_A(SeroGroupSpecification.SEROGROUP_A),
	NEIMENI_B(SeroGroupSpecification.SEROGROUP_B),
	NEIMENI_C(SeroGroupSpecification.SEROGROUP_C),
	NEIMENI_W(SeroGroupSpecification.SEROGROUP_W),
	NEIMENI_X(SeroGroupSpecification.SEROGROUP_X),
	NEIMENI_Y(SeroGroupSpecification.SEROGROUP_Y),
	NEIMENI_Z(SeroGroupSpecification.SEROGROUP_Z),
	NEIMENI_29E(SeroGroupSpecification.SEROGROUP_29E),
	/** Combined Z/29E code has no dedicated SORMAS constant. */
	NEIMENI_Z_29E("NEIMENI_Z/29E", null),
	NEIMENI_NGA(SeroGroupSpecification.NOT_GROUPABLE),
	NEIMENI_OTH(SeroGroupSpecification.OTHER);

	private final String code;
	private final SeroGroupSpecification seroGroupSpecification;

	EpipulseMeningococcalSerogroupRef(SeroGroupSpecification seroGroupSpecification) {
		this.code = name();
		this.seroGroupSpecification = seroGroupSpecification;
	}

	EpipulseMeningococcalSerogroupRef(String code, SeroGroupSpecification seroGroupSpecification) {
		this.code = code;
		this.seroGroupSpecification = seroGroupSpecification;
	}

	/** CSV code expected by EpiPulse (usually enum name). */
	public String getCode() {
		return code;
	}

	public SeroGroupSpecification getSeroGroupSpecification() {
		return seroGroupSpecification;
	}

	/**
	 * Returns EpiPulse code for recorded serogroup, or {@code null} when absent/unmapped/not a
	 * MENI serogroup claim.
	 */
	public static String codeFor(SeroGroupSpecification seroGroupSpecification) {

		if (seroGroupSpecification == null) {
			return null;
		}

		for (EpipulseMeningococcalSerogroupRef ref : values()) {
			if (ref.seroGroupSpecification == seroGroupSpecification) {
				return ref.code;
			}
		}

		return null;
	}
}
