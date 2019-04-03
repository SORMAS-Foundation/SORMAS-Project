/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.sample.read;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.AdditionalTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.databinding.FragmentSampleReadLayoutBinding;
import de.symeda.sormas.app.databinding.RowAdditionalTestLayoutBinding;

import static android.view.View.GONE;
import static android.view.View.inflate;

public class SampleReadFragment extends BaseReadFragment<FragmentSampleReadLayoutBinding, Sample, Sample> {

    private Sample record;
    private Sample referredSample;
    private PathogenTest mostRecentTest;
    private AdditionalTest mostRecentAdditionalTests;
    private List<String> requestedPathogenTests;
    private List<String> requestedAdditionalTests;

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
        if (!record.isReceived() || record.getSpecimenCondition() != SpecimenCondition.ADEQUATE
                || !record.getAdditionalTestingRequested() || mostRecentAdditionalTests == null) {
            contentBinding.mostRecentAdditionalTestsLayout.setVisibility(GONE);
        } else {
            if (!mostRecentAdditionalTests.hasArterialVenousGasValue()) {
                contentBinding.mostRecentAdditionalTests.arterialVenousGasLayout.setVisibility(GONE);
            }
        }

        // Lab details
        if (!record.getLab().getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
            contentBinding.sampleLabDetails.setVisibility(GONE);
        }
    }

    // Overrides

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(record);
        mostRecentAdditionalTests = DatabaseHelper.getAdditionalTestDao().queryMostRecentBySample(record);
        if (!StringUtils.isEmpty(record.getReferredToUuid())) {
            referredSample = DatabaseHelper.getSampleDao().queryUuid(record.getReferredToUuid());
        } else {
            referredSample = null;
        }

        requestedPathogenTests = new ArrayList<>();
        for (PathogenTestType pathogenTest : record.getRequestedPathogenTests()) {
            if (pathogenTest != PathogenTestType.OTHER) {
                requestedPathogenTests.add(pathogenTest.toString());
            }
        }
        requestedAdditionalTests = new ArrayList<>();
        for (AdditionalTestType additionalTest : record.getRequestedAdditionalTests()) {
            requestedAdditionalTests.add(additionalTest.toString());
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

        if (!requestedAdditionalTests.isEmpty()) {
            contentBinding.sampleRequestedAdditionalTestsTags.setTags(requestedAdditionalTests);
            if (StringUtils.isEmpty(record.getRequestedOtherAdditionalTests())) {
                contentBinding.sampleRequestedOtherAdditionalTests.setVisibility(GONE);
            }
        } else {
            contentBinding.sampleRequestedAdditionalTestsTags.setVisibility(GONE);
            contentBinding.sampleRequestedOtherAdditionalTests.setVisibility(GONE);
        }

        if (requestedPathogenTests.isEmpty() && requestedAdditionalTests.isEmpty()) {
            contentBinding.pathogenTestingDivider.setVisibility(GONE);
        }

        if (!Boolean.TRUE.equals(record.getPathogenTestingRequested())) {
            contentBinding.samplePathogenTestResult.setVisibility(GONE);
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
