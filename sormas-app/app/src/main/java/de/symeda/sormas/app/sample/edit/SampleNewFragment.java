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

import android.view.View;

import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.databinding.FragmentSampleNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

import static android.view.View.GONE;

public class
SampleNewFragment extends BaseEditFragment<FragmentSampleNewLayoutBinding, Sample, Sample> {

    private Sample record;

    // Enum lists

    private List<Item> sampleMaterialList;
    private List<Item> sampleSourceList;
    private List<Facility> labList;

    // Instance methods

    public static SampleNewFragment newInstance(Sample activityRootData) {
        return newInstance(SampleNewFragment.class, null, activityRootData);
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_new_sample);
    }

    @Override
    public Sample getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();

        sampleMaterialList = DataUtils.getEnumItems(SampleMaterial.class, true);
        sampleSourceList = DataUtils.getEnumItems(SampleSource.class, true);
        labList = DatabaseHelper.getFacilityDao().getLaboratories(true);
    }

    @Override
    public void onLayoutBinding(FragmentSampleNewLayoutBinding contentBinding) {
        contentBinding.setData(record);

        SampleValidator.initializeSampleValidation(contentBinding);

        contentBinding.setPathogenTestTypeClass(PathogenTestType.class);
        contentBinding.setAdditionalTestTypeClass(AdditionalTestType.class);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentSampleNewLayoutBinding contentBinding) {
        // Initialize ControlSpinnerFields
        contentBinding.sampleSampleMaterial.initializeSpinner(sampleMaterialList);
        contentBinding.sampleSampleSource.initializeSpinner(sampleSourceList);
        contentBinding.sampleLab.initializeSpinner(DataUtils.toItems(labList), new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Facility laboratory = (Facility) field.getValue();
                if (laboratory != null && laboratory.getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
                    contentBinding.sampleLabDetails.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.sampleLabDetails.hideField(true);
                }
            }
        });

        contentBinding.sampleRequestedPathogenTests.removeItem(PathogenTestType.OTHER);

        if (!ConfigProvider.hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
            contentBinding.additionalTestingLayout.setVisibility(GONE);
        }

        // Initialize ControlDateFields and ControlDateTimeFields
        contentBinding.sampleSampleDateTime.initializeDateTimeField(getFragmentManager());
        contentBinding.sampleShipmentDate.initializeDateField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_sample_new_layout;
    }

}
