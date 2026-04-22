/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.samples.diseasesection;

import de.symeda.sormas.api.Disease;

/**
 * Factory for component-based disease sections.
 */
public final class DiseaseSectionFactory {

	private DiseaseSectionFactory() {
	}

	public static AbstractDiseaseSectionComponent forDisease(Disease disease) {
		if (disease == null) {
			return new DefaultSectionComponent();
		}
		switch (disease) {
		case TUBERCULOSIS:
		case LATENT_TUBERCULOSIS:
			return new TuberculosisSectionComponent();
		case MEASLES:
			return new MeaslesSectionComponent();
		case CRYPTOSPORIDIOSIS:
			return new CryptosporidiosisSectionComponent();
		case INVASIVE_MENINGOCOCCAL_INFECTION:
			return new ImiSectionComponent();
		case INVASIVE_PNEUMOCOCCAL_INFECTION:
			return new IpiSectionComponent();
		case CSM:
			return new CsmSectionComponent();
		case DENGUE:
			return new DengueSectionComponent();
		case MALARIA:
			return new MalariaSectionComponent();
		default:
			return new DefaultSectionComponent();
		}
	}
}
