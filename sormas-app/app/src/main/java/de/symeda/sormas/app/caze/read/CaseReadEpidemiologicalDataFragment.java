package de.symeda.sormas.app.caze.read;

import android.app.AlertDialog;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.component.dialog.SimpleDialog;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseReadEpidLayoutBinding;

public class CaseReadEpidemiologicalDataFragment extends BaseReadFragment<FragmentCaseReadEpidLayoutBinding, EpiData, Case> {

    public static final String TAG = CaseReadEpidemiologicalDataFragment.class.getSimpleName();

    private EpiData record;
    private ObservableArrayList burials = new ObservableArrayList();
    private ObservableArrayList gatherings = new ObservableArrayList();
    private ObservableArrayList travels = new ObservableArrayList();

    private IEntryItemOnClickListener onBurialItemClickListener;
    private IEntryItemOnClickListener onGatheringItemClickListener;
    private IEntryItemOnClickListener onTravelItemClickListener;

    public static CaseReadEpidemiologicalDataFragment newInstance(Case activityRootData) {
        return newInstance(CaseReadEpidemiologicalDataFragment.class, null, activityRootData);
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        Case caze = getActivityRootData();
        record = caze.getEpiData();
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadEpidLayoutBinding contentBinding) {

        setupCallback();

        contentBinding.setData(record);
        contentBinding.setBurials(getBurialVisits());
        contentBinding.setGatherings(getGatherings());
        contentBinding.setTravels(getTravels());

        contentBinding.setBurialItemClickCallback(onBurialItemClickListener);
        contentBinding.setGatheringItemClickCallback(onGatheringItemClickListener);
        contentBinding.setTravelItemClickCallback(onTravelItemClickListener);

        setVisibilityByDisease(EpiDataDto.class, getActivityRootData().getDisease(), contentBinding.mainContent);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_epidemiological_information);
    }

    @Override
    public EpiData getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_epid_layout;
    }

    private ObservableArrayList getBurialVisits() {
        if (record != null && burials != null)
            burials.addAll(record.getBurials());
        return burials;
    }

    private ObservableArrayList getGatherings() {
        if (record != null && gatherings != null)
            gatherings.addAll(record.getGatherings());
        return gatherings;
    }

    private ObservableArrayList getTravels() {
        if (record != null && travels != null)
            travels.addAll(record.getTravels());
        return travels;
    }

    private void setupCallback() {

        onBurialItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_case_epid_burial_read_layout, item);
                AlertDialog dialog = simpleDialog.show();
            }
        };

        onGatheringItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_case_epid_gathering_read_layout, item);
                AlertDialog dialog = simpleDialog.show();
            }
        };

        onTravelItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_case_epid_travel_read_layout, item);
                AlertDialog dialog = simpleDialog.show();
            }
        };
    }
}
