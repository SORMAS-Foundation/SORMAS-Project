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

package de.symeda.sormas.app.pathogentest.edit;

import android.view.View;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.databinding.FragmentPathogenTestNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

import static android.view.View.GONE;

public class
PathogenTestNewFragment extends BaseEditFragment<FragmentPathogenTestNewLayoutBinding, PathogenTest, PathogenTest> {

    private PathogenTest record;

    // Enum lists

    private List<Facility> labList;
    private List<Item> testTypeList;
    private List<Item> diseaseList;
    private List<Item> testResultList;

    // Instance methods

    public static PathogenTestNewFragment newInstance(PathogenTest activityRootData) {
        return newInstance(PathogenTestNewFragment.class, null, activityRootData);
    }

    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_new_pathogen_test);
    }

    @Override
    public PathogenTest getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();
        testTypeList = DataUtils.getEnumItems(PathogenTestType.class, true);
        diseaseList = DataUtils.getEnumItems(Disease.class, true);
        testResultList = DataUtils.getEnumItems(PathogenTestResultType.class, true);
        labList = DatabaseHelper.getFacilityDao().getLaboratories(true);
    }

    @Override
    public void onLayoutBinding(FragmentPathogenTestNewLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentPathogenTestNewLayoutBinding contentBinding) {

        // Initialize ControlSpinnerFields
        contentBinding.pathogenTestTestType.initializeSpinner(testTypeList);
        contentBinding.pathogenTestTestedDisease.initializeSpinner(diseaseList);
        contentBinding.pathogenTestTestResult.initializeSpinner(testResultList);
        contentBinding.pathogenTestLab.initializeSpinner(DataUtils.toItems(labList));
//
//        // Initialize ControlDateFields
        contentBinding.pathogenTestTestDateTime.initializeDateTimeField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_pathogen_test_new_layout;
    }

}
