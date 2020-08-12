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

package de.symeda.sormas.app.sample.read;

import static android.view.View.GONE;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.AdditionalTest;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.databinding.FragmentSampleReadLayoutBinding;

public class SampleReadFragment extends BaseReadFragment<FragmentSampleReadLayoutBinding, Sample, Sample> {

	private Sample record;
	private Sample referredSample;
	private PathogenTest mostRecentTest;
	private AdditionalTest mostRecentAdditionalTests;
	private List<String> requestedPathogenTests = new ArrayList<>();
	private List<String> requestedAdditionalTests = new ArrayList<>();

	public static SampleReadFragment newInstance(Sample activityRootData) {
		return newInstance(SampleReadFragment.class, null, activityRootData);
	}

	private void setUpControlListeners(FragmentSampleReadLayoutBinding contentBinding) {
		if (!StringUtils.isEmpty(record.getReferredToUuid())) {
			contentBinding.sampleReferredToUuid.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					if (referredSample != null) {
						// Activity needs to be destroyed because it is only resumed, not created otherwise
						// and therefore the record uuid is not changed
						if (getActivity() != null) {
							getActivity().finish();
						}
						SampleReadActivity.startActivity(getActivity(), referredSample.getUuid());
					}
				}
			});
		}
	}

	private void setUpFieldVisibilities(FragmentSampleReadLayoutBinding contentBinding) {
		// Most recent test layout
		if (!record.isReceived() || record.getSpecimenCondition() != SpecimenCondition.ADEQUATE) {
			contentBinding.mostRecentTestLayout.setVisibility(GONE);
		} else {
			if (mostRecentTest != null) {
				contentBinding.noRecentTest.setVisibility(GONE);
			}
		}

		// Most recent additional tests layout
		if (ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
			if (!record.isReceived()
				|| record.getSpecimenCondition() != SpecimenCondition.ADEQUATE
				|| !record.getAdditionalTestingRequested()
				|| mostRecentAdditionalTests == null) {
				contentBinding.mostRecentAdditionalTestsLayout.setVisibility(GONE);
			} else {
				if (!mostRecentAdditionalTests.hasArterialVenousGasValue()) {
					contentBinding.mostRecentAdditionalTests.arterialVenousGasLayout.setVisibility(GONE);
				}
			}
		} else {
			contentBinding.mostRecentAdditionalTestsLayout.setVisibility(GONE);
		}
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
		mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(record);
		if (ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
			mostRecentAdditionalTests = DatabaseHelper.getAdditionalTestDao().queryMostRecentBySample(record);
		}
		if (!StringUtils.isEmpty(record.getReferredToUuid())) {
			referredSample = DatabaseHelper.getSampleDao().queryUuid(record.getReferredToUuid());
		} else {
			referredSample = null;
		}

		for (PathogenTestType pathogenTest : record.getRequestedPathogenTests()) {
			if (pathogenTest != PathogenTestType.OTHER) {
				requestedPathogenTests.add(pathogenTest.toString());
			}
		}

		if (ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
			for (AdditionalTestType additionalTest : record.getRequestedAdditionalTests()) {
				requestedAdditionalTests.add(additionalTest.toString());
			}
		}
	}

	@Override
	public void onLayoutBinding(FragmentSampleReadLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setPathogenTest(mostRecentTest);
		contentBinding.setAdditionalTest(mostRecentAdditionalTests);
		contentBinding.setReferredSample(referredSample);
	}

	@Override
	public void onAfterLayoutBinding(FragmentSampleReadLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(SampleDto.class, contentBinding.mainContent);
		setUpFieldVisibilities(contentBinding);

		if (!requestedPathogenTests.isEmpty()) {
			contentBinding.sampleRequestedPathogenTestsTags.setTags(requestedPathogenTests);
			if (StringUtils.isEmpty(record.getRequestedOtherPathogenTests())) {
				contentBinding.sampleRequestedOtherPathogenTests.setVisibility(GONE);
			}
		} else {
			contentBinding.sampleRequestedPathogenTestsTags.setVisibility(GONE);
			contentBinding.sampleRequestedOtherPathogenTests.setVisibility(GONE);
		}

		if (!ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
			contentBinding.additionalTestingLayout.setVisibility(GONE);
		} else {
			if (!requestedAdditionalTests.isEmpty()) {
				contentBinding.sampleRequestedAdditionalTestsTags.setTags(requestedAdditionalTests);
				if (StringUtils.isEmpty(record.getRequestedOtherAdditionalTests())) {
					contentBinding.sampleRequestedOtherAdditionalTests.setVisibility(GONE);
				}
			} else {
				contentBinding.sampleRequestedAdditionalTestsTags.setVisibility(GONE);
				contentBinding.sampleRequestedOtherAdditionalTests.setVisibility(GONE);
			}
		}

		if (requestedPathogenTests.isEmpty() && requestedAdditionalTests.isEmpty()) {
			contentBinding.pathogenTestingDivider.setVisibility(GONE);
		}
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_sample_information);
	}

	@Override
	public Sample getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_sample_read_layout;
	}
}
