/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Broad grouping of laboratory test methods. When adding a pathogen test the user first picks a
 * category, which scopes the available {@link PathogenTestType} methods to those annotated with the
 * matching {@link PathogenTestCategoryRel}. The seven values come from the approved
 * "Categories" reference sheet of the laboratory specification.
 */
public enum PathogenTestCategory {

	MOLECULAR_ASSAYS,
	SEROLOGICAL_TESTS,
	ANTIGEN_DETECTION,
	CULTURE_AND_ISOLATION,
	MICROSCOPY_AND_STAINING,
	ANTIMICROBIAL_SUSCEPTIBILITY_TESTING,
	FUNCTIONAL_IMMUNE_ASSAYS;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
