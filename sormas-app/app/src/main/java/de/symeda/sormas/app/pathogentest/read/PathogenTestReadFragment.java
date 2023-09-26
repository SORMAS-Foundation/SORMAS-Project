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

package de.symeda.sormas.app.pathogentest.read;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.databinding.FragmentPathogenTestReadLayoutBinding;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PathogenTestReadFragment extends BaseReadFragment<FragmentPathogenTestReadLayoutBinding, PathogenTest, PathogenTest> {

	private PathogenTest record;

	public static PathogenTestReadFragment newInstance(PathogenTest activityRootData) {
		return newInstanceWithFieldCheckers(
			PathogenTestReadFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withCountry(ConfigProvider.getServerCountryCode()),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentPathogenTestReadLayoutBinding contentBinding) {
		contentBinding.setData(record);
		setFieldVisibilitiesAndAccesses(PathogenTestDto.class, contentBinding.mainContent);

		if ((PathogenTestType.PCR_RT_PCR == record.getTestType() && PathogenTestResultType.POSITIVE == record.getTestResult())
			|| PathogenTestType.CQ_VALUE_DETECTION.equals(record.getTestType())) {
			getContentBinding().pathogenTestCqValue.setVisibility(View.VISIBLE);
		} else {
			getContentBinding().pathogenTestCqValue.hideField(false);
		}

		if (PathogenTestType.PCR_RT_PCR == record.getTestType() && Disease.CORONAVIRUS == record.getTestedDisease()) {
			getContentBinding().pathogenTestPcrTestSpecification.setVisibility(View.VISIBLE);
		} else {
			getContentBinding().pathogenTestPcrTestSpecification.hideField(false);
		}

		if (isVisibleAllowed(CaseDataDto.class, contentBinding.pathogenTestTestedDiseaseVariant)) {
			contentBinding.pathogenTestTestedDiseaseVariant.setVisibility(record.getTestedDiseaseVariant() != null ? VISIBLE : GONE);
		}

		if(record.getSample() != null){
			contentBinding.pathogenTestTestedDiseaseLayout.setVisibility(VISIBLE);
			contentBinding.pathogenTestTestedPathogen.setVisibility(GONE);
		} else {
			contentBinding.pathogenTestTestedDiseaseLayout.setVisibility(GONE);
			contentBinding.pathogenTestTestedPathogen.setVisibility(VISIBLE);
		}
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.heading_pathogen_test_read);
	}

	@Override
	public PathogenTest getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_pathogen_test_read_layout;
	}
}
