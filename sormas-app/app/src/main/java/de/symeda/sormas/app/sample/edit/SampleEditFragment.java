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

package de.symeda.sormas.app.sample.edit;

import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.gms.common.api.CommonStatusCodes;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.AdditionalTest;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.barcode.BarcodeActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.sample.read.SampleReadActivity;
import de.symeda.sormas.app.util.DataUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.symeda.sormas.app.core.notification.NotificationType.WARNING;

public class SampleEditFragment extends BaseEditFragment<FragmentSampleEditLayoutBinding, Sample, Sample> {

    private Sample record;
    private Sample referredSample;
    private PathogenTest mostRecentTest;
    private AdditionalTest mostRecentAdditionalTests;

    // Enum lists

    private List<Item> sampleMaterialList;
    private List<Item> sampleSourceList;
    private List<Facility> labList;
    private List<Item> samplePurposeList;
    private List<String> requestedPathogenTests = new ArrayList<>();
    private List<String> requestedAdditionalTests = new ArrayList<>();

    public static SampleEditFragment newInstance(Sample activityRootData) {
        return newInstance(SampleEditFragment.class, null, activityRootData);
    }

    private void setUpControlListeners(FragmentSampleEditLayoutBinding contentBinding) {
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
                        SampleReadActivity.startActivity(getActivity(), record.getUuid());
                    }
                }
            });
        }
    }

    private void setUpFieldVisibilities(final FragmentSampleEditLayoutBinding contentBinding) {
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
            if (!record.isReceived() || record.getSpecimenCondition() != SpecimenCondition.ADEQUATE
                    || !record.getAdditionalTestingRequested() || mostRecentAdditionalTests == null) {
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
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_sample_information);
    }

    @Override
    public Sample getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();
        if (record.getId() != null) {
            mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(record);
            if (ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
                mostRecentAdditionalTests = DatabaseHelper.getAdditionalTestDao().queryMostRecentBySample(record);
            }
        }
        if (!StringUtils.isEmpty(record.getReferredToUuid())) {
            referredSample = DatabaseHelper.getSampleDao().queryUuid(record.getReferredToUuid());
        } else {
            referredSample = null;
        }

        sampleMaterialList = DataUtils.getEnumItems(SampleMaterial.class, true);
        sampleSourceList = DataUtils.getEnumItems(SampleSource.class, true);
        labList = DatabaseHelper.getFacilityDao().getActiveLaboratories(true);
        samplePurposeList = DataUtils.getEnumItems(SamplePurpose.class, true);

        for (PathogenTestType pathogenTest : record.getRequestedPathogenTests()) {
            requestedPathogenTests.clear();
            if (pathogenTest != PathogenTestType.OTHER) {
                requestedPathogenTests.add(pathogenTest.toString());
            }
        }
        if (ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
            requestedAdditionalTests.clear();
            for (AdditionalTestType additionalTest : record.getRequestedAdditionalTests()) {
                requestedAdditionalTests.add(additionalTest.toString());
            }
        }
    }

    @Override
    public void onLayoutBinding(FragmentSampleEditLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        contentBinding.setData(record);
        contentBinding.setPathogenTest(mostRecentTest);
        contentBinding.setAdditionalTest(mostRecentAdditionalTests);
        contentBinding.setReferredSample(referredSample);

        SampleValidator.initializeSampleValidation(contentBinding);

        contentBinding.setPathogenTestTypeClass(PathogenTestType.class);
        contentBinding.setAdditionalTestTypeClass(AdditionalTestType.class);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentSampleEditLayoutBinding contentBinding) {
        setUpFieldVisibilities(contentBinding);

        // Initialize ControlSpinnerFields
        contentBinding.sampleSampleMaterial.initializeSpinner(sampleMaterialList);
        contentBinding.sampleSampleSource.initializeSpinner(sampleSourceList);
        contentBinding.samplePurpose.setEnabled(referredSample == null || record.getSamplePurpose() != SamplePurpose.EXTERNAL);
        contentBinding.sampleLab.initializeSpinner(DataUtils.toItems(labList), field -> {
            Facility laboratory = (Facility) field.getValue();
            if (laboratory != null && laboratory.getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
                contentBinding.sampleLabDetails.setVisibility(View.VISIBLE);
            } else {
                contentBinding.sampleLabDetails.hideField(true);
            }
        });
        contentBinding.samplePurpose.initializeSpinner(samplePurposeList, field -> {
            SamplePurpose samplePurpose = (SamplePurpose) field.getValue();
            if (SamplePurpose.EXTERNAL == samplePurpose) {
                contentBinding.externalSampleFieldsLayout.setVisibility(VISIBLE);
                contentBinding.samplePathogenTestingRequested.setVisibility(
                        ConfigProvider.getUser().equals(record.getReportingUser()) ? VISIBLE : GONE);
                contentBinding.sampleAdditionalTestingRequested.setVisibility(
                        ConfigProvider.getUser().equals(record.getReportingUser()) ? VISIBLE : GONE);
            } else {
                contentBinding.externalSampleFieldsLayout.setVisibility(GONE);
                contentBinding.samplePathogenTestingRequested.setVisibility(GONE);
                contentBinding.sampleAdditionalTestingRequested.setVisibility(GONE);
            }
        });

        // Initialize ControlDateFields and ControlDateTimeFields
        contentBinding.sampleSampleDateTime.initializeDateTimeField(getFragmentManager());
        contentBinding.sampleShipmentDate.initializeDateField(getFragmentManager());

        // Initialize on clicks
        contentBinding.buttonScanFieldSampleId.setOnClickListener((View v)->{
            Intent intent = new Intent(getContext(), BarcodeActivity.class);
            startActivityForResult(intent, BarcodeActivity.RC_BARCODE_CAPTURE);
        });

        // Disable fields the user doesn't have access to - this involves almost all fields when
        // the user is not the one that originally reported the sample
        if (!ConfigProvider.getUser().equals(record.getReportingUser())) {
            contentBinding.sampleSampleDateTime.setEnabled(false);
            contentBinding.sampleSampleMaterial.setEnabled(false);
            contentBinding.sampleSampleMaterialText.setEnabled(false);
            contentBinding.sampleSampleSource.setEnabled(false);
            contentBinding.sampleLab.setEnabled(false);
            contentBinding.sampleLabDetails.setEnabled(false);
            contentBinding.sampleShipped.setEnabled(false);
            contentBinding.sampleShipmentDate.setEnabled(false);
            contentBinding.sampleShipmentDetails.setEnabled(false);
            contentBinding.samplePurpose.setEnabled(false);
            contentBinding.sampleReceived.setEnabled(false);
            contentBinding.sampleLabSampleID.setEnabled(false);
            contentBinding.samplePathogenTestingRequested.setVisibility(GONE);
            contentBinding.sampleRequestedPathogenTests.setVisibility(GONE);
            contentBinding.sampleAdditionalTestingRequested.setVisibility(GONE);
            contentBinding.sampleRequestedAdditionalTests.setVisibility(GONE);

            if (!requestedPathogenTests.isEmpty()) {
                contentBinding.sampleRequestedPathogenTestsTags.setTags(requestedPathogenTests);
                if (StringUtils.isEmpty(record.getRequestedOtherPathogenTests())) {
                    contentBinding.sampleRequestedOtherPathogenTests.setVisibility(GONE);
                }
            } else {
                contentBinding.sampleRequestedPathogenTestsTags.setVisibility(GONE);
                contentBinding.sampleRequestedOtherPathogenTests.setVisibility(GONE);
            }

            if (ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
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
        } else {
            contentBinding.sampleRequestedPathogenTestsTags.setVisibility(GONE);
            contentBinding.sampleRequestedPathogenTests.removeItem(PathogenTestType.OTHER);
            contentBinding.sampleRequestedAdditionalTestsTags.setVisibility(GONE);
        }

        if (!ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
            contentBinding.additionalTestingLayout.setVisibility(GONE);
        }
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_sample_edit_layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == BarcodeActivity.RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS && data != null) {
                getContentBinding().sampleFieldSampleID.setValue(data.getStringExtra(BarcodeActivity.BARCODE_RESULT));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
