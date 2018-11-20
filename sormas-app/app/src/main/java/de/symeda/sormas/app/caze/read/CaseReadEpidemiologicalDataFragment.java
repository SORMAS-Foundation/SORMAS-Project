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

package de.symeda.sormas.app.caze.read;

import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.component.dialog.InfoDialog;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseReadEpidLayoutBinding;

public class CaseReadEpidemiologicalDataFragment extends BaseReadFragment<FragmentCaseReadEpidLayoutBinding, EpiData, Case> {

    public static final String TAG = CaseReadEpidemiologicalDataFragment.class.getSimpleName();

    private EpiData record;

    private IEntryItemOnClickListener onBurialItemClickListener;
    private IEntryItemOnClickListener onGatheringItemClickListener;
    private IEntryItemOnClickListener onTravelItemClickListener;

    // Static methods

    public static CaseReadEpidemiologicalDataFragment newInstance(Case activityRootData) {
        return newInstance(CaseReadEpidemiologicalDataFragment.class, null, activityRootData);
    }

    // Instance methods

    private void setUpControlListeners() {
        onBurialItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                InfoDialog infoDialog = new InfoDialog(getContext(),
                        R.layout.dialog_case_epid_burial_read_layout, item);
                infoDialog.show();
            }
        };

        onGatheringItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                InfoDialog infoDialog = new InfoDialog(getContext(),
                        R.layout.dialog_case_epid_gathering_read_layout, item);
                infoDialog.show();
            }
        };

        onTravelItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                InfoDialog infoDialog = new InfoDialog(getContext(),
                        R.layout.dialog_case_epid_travel_read_layout, item);
                infoDialog.show();
            }
        };
    }

    // Overrides

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Case caze = getActivityRootData();
        record = caze.getEpiData();
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadEpidLayoutBinding contentBinding) {
        setUpControlListeners();

        ObservableArrayList<EpiDataBurial> burials = new ObservableArrayList<>();
        burials.addAll(record.getBurials());
        ObservableArrayList<EpiDataTravel> travels = new ObservableArrayList<>();
        travels.addAll(record.getTravels());
        ObservableArrayList<EpiDataGathering> gatherings = new ObservableArrayList<>();
        gatherings.addAll(record.getGatherings());

        contentBinding.setData(record);
        contentBinding.setBurialList(burials);
        contentBinding.setGatheringList(gatherings);
        contentBinding.setTravelList(travels);
        contentBinding.setBurialItemClickCallback(onBurialItemClickListener);
        contentBinding.setGatheringItemClickCallback(onGatheringItemClickListener);
        contentBinding.setTravelItemClickCallback(onTravelItemClickListener);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadEpidLayoutBinding contentBinding) {
        setVisibilityByDisease(EpiDataDto.class, getActivityRootData().getDisease(), contentBinding.mainContent);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_case_epidemiological_data);
    }

    @Override
    public EpiData getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_epid_layout;
    }

}
