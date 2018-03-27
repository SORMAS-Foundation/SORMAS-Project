package de.symeda.sormas.app.caze.read;

import android.app.AlertDialog;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.component.dialog.SimpleDialog;
import de.symeda.sormas.app.component.tagview.Tag;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadEpidLayoutBinding;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadEpidemiologicalDataFragment extends BaseReadActivityFragment<FragmentCaseReadEpidLayoutBinding, EpiData> {

    public static final String TAG = CaseReadEpidemiologicalDataFragment.class.getSimpleName();

    private String recordUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private EpiData record;
    private ObservableArrayList burials = new ObservableArrayList();
    private ObservableArrayList gatherings = new ObservableArrayList();
    private ObservableArrayList travels = new ObservableArrayList();

    private IEntryItemOnClickListener onBurialItemClickListener;
    private IEntryItemOnClickListener onSocialEventItemClickListener;
    private IEntryItemOnClickListener onTravelItemClickListener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            EpiData epiData = null;
            Case caze = DatabaseHelper.getCaseDao().queryUuid(recordUuid);
            if (caze != null)
                epiData = DatabaseHelper.getEpiDataDao().queryUuid(caze.getEpiData().getUuid());

            resultHolder.forItem().add(epiData);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadEpidLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setBurials(getBurialVisits());
        contentBinding.setSocialEvents(getGatherings());
        contentBinding.setTravels(getTravels());
        contentBinding.setAnimalContactYes(getAnimalContactYes());
        contentBinding.setAnimalContactUnknown(getAnimalContactUnknown());
        contentBinding.setAnimalContactNo(getAnimalContactNo());

        contentBinding.setBurialItemClickCallback(onBurialItemClickListener);
        contentBinding.setSocialEventItemClickCallback(onSocialEventItemClickListener);
        contentBinding.setTravelItemClickCallback(onTravelItemClickListener);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadEpidLayoutBinding contentBinding) {

    }

    @Override
    public void onPageResume(FragmentCaseReadEpidLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

    }

    @Override
    protected String getSubHeadingTitle() {
        return null;
    }

    @Override
    public EpiData getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_epid_layout;
    }

    public static CaseReadEpidemiologicalDataFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseReadEpidemiologicalDataFragment.class, capsule);
    }

    private ObservableArrayList getBurialVisits() {
        if (record != null && burials != null)
            burials.addAll(record.getBurials());

        //burials.add(MemoryDatabaseHelper.EPID_DATA_BURIAL.getBurials(1).get(0));
        return burials;
    }

    private ObservableArrayList getGatherings() {
        if (record != null && gatherings != null)
            gatherings.addAll(record.getGatherings());

        //gatherings.add(MemoryDatabaseHelper.EPID_DATA_GATHERING.getGatherings(1).get(0));
        return gatherings;
    }

    private ObservableArrayList getTravels() {
        if (record != null && travels != null)
            travels.addAll(record.getTravels());

        //travels.add(MemoryDatabaseHelper.EPID_DATA_TRAVEL.getTravels(1).get(0));
        return travels;
    }

    private List<Tag> getAnimalContactYes() {
        List<Tag> results = new ArrayList();
        results.add(new Tag("Rodents or their excreta"));
        results.add(new Tag("Bats or their excreta"));
        return results;
    }

    private List<Tag> getAnimalContactUnknown() {
        List<Tag> results = new ArrayList();
        results.add(new Tag("Primates (monkeys)"));
        results.add(new Tag("Swine"));
        return results;
    }

    private List<Tag> getAnimalContactNo() {
        List<Tag> results = new ArrayList();
        results.add(new Tag("Poultry or wild birds"));
        return results;
    }

    private void setupCallback() {

        onBurialItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_case_epid_burial_layout, item);
                AlertDialog dialog = simpleDialog.show();
            }
        };

        onSocialEventItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_case_epid_social_event_layout, item);
                AlertDialog dialog = simpleDialog.show();
            }
        };

        onTravelItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                SimpleDialog simpleDialog = new SimpleDialog(getContext(),
                        R.layout.dialog_case_epid_travel_layout, item);
                AlertDialog dialog = simpleDialog.show();
            }
        };
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }
}
