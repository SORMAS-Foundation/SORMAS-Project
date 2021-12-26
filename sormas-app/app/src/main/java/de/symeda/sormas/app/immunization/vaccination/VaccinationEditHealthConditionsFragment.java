/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.immunization.vaccination;

import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.app.BaseEditHealthConditionsFragment;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.vaccination.Vaccination;

public class VaccinationEditHealthConditionsFragment extends BaseEditHealthConditionsFragment<Vaccination> {

	public static final String TAG = VaccinationEditHealthConditionsFragment.class.getSimpleName();

	public static VaccinationEditHealthConditionsFragment newInstance(Vaccination activityRootData) {
		return newInstanceWithFieldCheckers(
			VaccinationEditHealthConditionsFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getImmunization().getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData().getHealthConditions();
	}
}
