/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.environmentsample.read;

import static android.view.View.GONE;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;

import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.databinding.FragmentEnvironmentSampleReadLayoutBinding;

public class EnvironmentSampleReadFragment
	extends BaseReadFragment<FragmentEnvironmentSampleReadLayoutBinding, EnvironmentSample, EnvironmentSample> {

	private EnvironmentSample record;
	private final List<String> requestedPathogenTests = new ArrayList<>();

	public static EnvironmentSampleReadFragment newInstance(EnvironmentSample activityRootData) {
		return newInstance(EnvironmentSampleReadFragment.class, null, activityRootData);
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {

		record = getActivityRootData();

		for (Pathogen pathogen : record.getRequestedPathogenTests()) {
			requestedPathogenTests.add(pathogen.getCaption());
		}
	}

	@Override
	public void onLayoutBinding(FragmentEnvironmentSampleReadLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	public void onAfterLayoutBinding(FragmentEnvironmentSampleReadLayoutBinding contentBinding) {

		setFieldVisibilitiesAndAccesses(EnvironmentSampleDto.class, contentBinding.mainContent);

		if (!requestedPathogenTests.isEmpty()) {
			contentBinding.environmentSampleRequestedPathogenTestsTags.setTags(requestedPathogenTests);
			if (StringUtils.isEmpty(record.getOtherRequestedPathogenTests())) {
				contentBinding.environmentSampleOtherRequestedPathogenTests.setVisibility(GONE);
			}
		} else {
			contentBinding.environmentSampleRequestedPathogenTestsTags.setVisibility(GONE);
			contentBinding.environmentSampleOtherRequestedPathogenTests.setVisibility(GONE);
		}
		contentBinding.environmentSampleReportingUser.setPseudonymized(record.isPseudonymized());
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_environment_sample_information);
	}

	@Override
	public EnvironmentSample getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_environment_sample_read_layout;
	}

	@Override
	public boolean showEditAction() {
		return ConfigProvider.hasUserRight(UserRight.ENVIRONMENT_SAMPLE_EDIT);
	}

}
