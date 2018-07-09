package de.symeda.sormas.app.caze.read;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.component.dialog.SimpleDialog;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadEpidLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadEpidemiologicalDataFragment extends BaseReadFragment<FragmentCaseReadEpidLayoutBinding, EpiData, Case> {

    public static final String TAG = CaseReadEpidemiologicalDataFragment.class.getSimpleName();

    private AsyncTask onResumeTask;
    private EpiData record;
    private ObservableArrayList burials = new ObservableArrayList();
    private ObservableArrayList gatherings = new ObservableArrayList();
    private ObservableArrayList travels = new ObservableArrayList();

    private IEntryItemOnClickListener onBurialItemClickListener;
    private IEntryItemOnClickListener onSocialEventItemClickListener;
    private IEntryItemOnClickListener onTravelItemClickListener;

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case caze = getActivityRootData();

            if (caze != null) {
                if (caze.isUnreadOrChildUnread())
                    DatabaseHelper.getCaseDao().markAsRead(caze);

                if (caze.getPerson() == null) {
                    caze.setPerson(DatabaseHelper.getPersonDao().build());
                }

                //TODO: Do we really need to do this
                if (caze.getEpiData() != null)
                    caze.setEpiData(DatabaseHelper.getEpiDataDao().queryUuid(caze.getEpiData().getUuid()));
            }

            resultHolder.forItem().add(caze.getEpiData());
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

        Case caze = getActivityRootData();

        contentBinding.setData(record);
        contentBinding.setBurials(getBurialVisits());
        contentBinding.setSocialEvents(getGatherings());
        contentBinding.setTravels(getTravels());
        contentBinding.epiDataAnimalContactExposed.setTags(getAnimalContactYes());
        contentBinding.epiDataAnimalContactUnknownExposed.setTags(getAnimalContactUnknown());
        contentBinding.epiDataAnimalContactNotExposed.setTags(getAnimalContactNo());

        contentBinding.setBurialItemClickCallback(onBurialItemClickListener);
        contentBinding.setSocialEventItemClickCallback(onSocialEventItemClickListener);
        contentBinding.setTravelItemClickCallback(onTravelItemClickListener);

        setVisibilityByDisease(EpiDataDto.class, caze.getDisease(), contentBinding.mainContent);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadEpidLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentCaseReadEpidLayoutBinding contentBinding, EpiData epiData) {

    }

    @Override
    public void onPageResume(FragmentCaseReadEpidLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    //getBaseActivity().showPreloader();
                    //
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
                    Case caze = getActivityRootData();

                    if (caze != null) {
                        if (caze.isUnreadOrChildUnread())
                            DatabaseHelper.getCaseDao().markAsRead(caze);

                        if (caze.getPerson() == null) {
                            caze.setPerson(DatabaseHelper.getPersonDao().build());
                        }

                        //TODO: Do we really need to do this
                        if (caze.getEpiData() != null)
                            caze.setEpiData(DatabaseHelper.getEpiDataDao().queryUuid(caze.getEpiData().getUuid()));
                    }

                    resultHolder.forItem().add(caze.getEpiData());
                }
            };
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getBaseActivity().hidePreloader();
                    //getBaseActivity().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record = itemIterator.next();

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getBaseActivity().hidePreloader();
            //getBaseActivity().showFragmentView();
        }
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_epidemiological_information);
    }

    @Override
    public EpiData getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_epid_layout;
    }

    public static CaseReadEpidemiologicalDataFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseReadEpidemiologicalDataFragment.class, capsule, activityRootData);
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

    private List<String> getAnimalContactYes() {
        List<String> results = new ArrayList();
        results.add("Rodents or their excreta");
        results.add("Bats or their excreta");
        return results;
    }

    private List<String> getAnimalContactUnknown() {
        List<String> results = new ArrayList();
        results.add("Primates (monkeys)");
        results.add("Swine");
        return results;
    }

    private List<String> getAnimalContactNo() {
        List<String> results = new ArrayList();
        results.add("Poultry or wild birds");
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
