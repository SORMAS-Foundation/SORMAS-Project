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

import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectionSite;

/**
 * STI {@code SiteOfInfection} mapping (currently emitted for SYPH) from
 * {@code symptoms.syphilisinfectionsite}.
 *
 * <p>
 * {@code UNKNOWN} is intentionally unmapped (blank), not {@code OTH}. EpiPulse marks the variable
 * repeatable, but SORMAS currently stores one site per case.
 */
public enum EpipulseSiteOfInfectionRef {

	AR(SyphilisInfectionSite.ANORECTAL),
	GEN(SyphilisInfectionSite.GENITAL),
	PH(SyphilisInfectionSite.PHARYNGEAL),
	OTH(SyphilisInfectionSite.OTHER);

	private final SyphilisInfectionSite infectionSite;

	EpipulseSiteOfInfectionRef(SyphilisInfectionSite infectionSite) {
		this.infectionSite = infectionSite;
	}

	public SyphilisInfectionSite getInfectionSite() {
		return infectionSite;
	}

	/** Returns EpiPulse code for mapped infection site, otherwise {@code null}. */
	public static String codeFor(SyphilisInfectionSite infectionSite) {

		if (infectionSite == null) {
			return null;
		}

		for (EpipulseSiteOfInfectionRef ref : values()) {
			if (ref.infectionSite == infectionSite) {
				return ref.name();
			}
		}

		return null;
	}
}
