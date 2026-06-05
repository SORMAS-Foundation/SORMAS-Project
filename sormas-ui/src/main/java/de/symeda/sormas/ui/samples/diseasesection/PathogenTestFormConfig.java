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

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;

/**
 * Immutable snapshot of country-specific feature flags for PathogenTestForm.
 * Computed once at form construction time so section classes never call
 * FacadeProvider directly for country checks.
 */
public class PathogenTestFormConfig {

	public final boolean isLuxembourg;
	public final boolean resultRequired;

	private PathogenTestFormConfig(boolean isLuxembourg, boolean resultRequired) {
		this.isLuxembourg = isLuxembourg;
		this.resultRequired = resultRequired;
	}

	public static PathogenTestFormConfig fromCurrentConfig() {
		return new PathogenTestFormConfig(
			FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG),
			FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.PATHOGEN_TEST_RESULT_REQUIRED));
	}
}
