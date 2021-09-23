/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import android.os.Bundle;

import de.symeda.sormas.api.caze.Trimester;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.vaccination.VaccinationEntity;
import de.symeda.sormas.app.databinding.FragmentVaccinationReadLayoutBinding;

public class VaccinationReadFragment extends BaseReadFragment<FragmentVaccinationReadLayoutBinding, VaccinationEntity, VaccinationEntity> {

	private VaccinationEntity record;

	public static VaccinationReadFragment newInstance(VaccinationEntity activityRootData) {
		return newInstance(VaccinationReadFragment.class, null, activityRootData);
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentVaccinationReadLayoutBinding contentBinding) {

		contentBinding.setData(record);
		contentBinding.setTrimesterClass(Trimester.class);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.heading_vaccination);
	}

	@Override
	public VaccinationEntity getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_vaccination_read_layout;
	}
}
