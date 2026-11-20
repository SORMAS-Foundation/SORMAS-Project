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

import de.symeda.sormas.api.symptoms.InfectionSite;

/**
 * TUBE {@code MajorSiteOfTB} and {@code MinorSiteOfTB} mapping from {@code symptoms.majorsite} and
 * {@code symptoms.minorsite}.
 *
 * <p>
 * One enum serves both variables. Minor site intentionally excludes {@code PULMONARY};
 * {@link #isMinorSite()} enforces that. {@code UROGEN} maps both
 * {@link InfectionSite#GENITO_URINARY} and {@link InfectionSite#UROGENITAL_SYSTEM}. Unknown/not
 * applicable values remain unmapped.
 */
public enum EpipulseTubeSiteOfTbRef {

	BONEOTH(true, InfectionSite.BONES_JOINTS_OTHER_THAN_VERTEBRAE),
	CNSOTH(true, InfectionSite.CNS_EXCEPT_MENINGES),
	DISSEM(true, InfectionSite.DISSEMINATED_FORM),
	EXTRAPULMNOTSPEC(true, InfectionSite.EXTRAPULMONARY_UNKNOWN_SITE),
	GASTROINTEST(true, InfectionSite.PERITONEUM_DIGESTIVE_TRACT),
	LYMPHEXTHOR(true, InfectionSite.EXTRA_THORACIC_LYMPH_NODES),
	LYMPHINTHOR(true, InfectionSite.INTRATHORACIC_LYMPH_NODES),
	MENING(true, InfectionSite.MENINGES),
	OTH(true, InfectionSite.OTHER),
	PLEURAL(true, InfectionSite.PLEURA),
	PULMONARY(false, InfectionSite.LUNG),
	SPINE(true, InfectionSite.VERTEBRAE),
	UROGEN(true, InfectionSite.GENITO_URINARY, InfectionSite.UROGENITAL_SYSTEM);

	private final boolean minorSite;
	private final InfectionSite[] infectionSites;

	EpipulseTubeSiteOfTbRef(boolean minorSite, InfectionSite... infectionSites) {
		this.minorSite = minorSite;
		this.infectionSites = infectionSites;
	}

	/** Whether this code is valid for {@code MinorSiteOfTB} as well as {@code MajorSiteOfTB}. */
	public boolean isMinorSite() {
		return minorSite;
	}

	/** Returns the EpiPulse code for a recorded major site, otherwise {@code null}. */
	public static String codeForMajorSite(InfectionSite infectionSite) {
		return codeFor(infectionSite, false);
	}

	/** Returns the EpiPulse code for a recorded minor site, otherwise {@code null}. */
	public static String codeForMinorSite(InfectionSite infectionSite) {
		return codeFor(infectionSite, true);
	}

	private static String codeFor(InfectionSite infectionSite, boolean asMinorSite) {

		if (infectionSite == null) {
			return null;
		}

		for (EpipulseTubeSiteOfTbRef ref : values()) {
			if (asMinorSite && !ref.minorSite) {
				continue;
			}
			for (InfectionSite candidate : ref.infectionSites) {
				if (candidate == infectionSite) {
					return ref.name();
				}
			}
		}

		return null;
	}
}
