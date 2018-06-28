package de.symeda.sormas.app.caze.read;

import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadHospitalizationLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadHospitalizationFragment extends BaseReadActivityFragment<FragmentCaseReadHospitalizationLayoutBinding, Hospitalization, Case> {

    public static final String TAG = CaseReadHospitalizationFragment.class.getSimpleName();
    private AsyncTask onResumeTask;

    private String recordUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private Hospitalization record;
    private Case caze;
    private Case caseRecord;
    private ObservableArrayList preHospitalizations = new ObservableArrayList();

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
            Case caze = getActivityRootData();

            if (caze != null) {
                if (caze.isUnreadOrChildUnread())
                    DatabaseHelper.getCaseDao().markAsRead(caze);

                if (caze.getPerson() == null) {
                    caze.setPerson(DatabaseHelper.getPersonDao().build());
                }

                //TODO: Do we really need to do this
                if (caze.getHospitalization() != null)
                    caze.setHospitalization(DatabaseHelper.getHospitalizationDao().queryUuid(caze.getHospitalization().getUuid()));
            }

            resultHolder.forItem().add(caze.getHospitalization());
            resultHolder.forItem().add(caze);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            //TODO: Orson - Use recordUuid (Verify this todo)
            if (itemIterator.hasNext())
                caseRecord =  itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadHospitalizationLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setCaze(caseRecord);
        contentBinding.setHospitalizations(getHospitalizations());
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadHospitalizationLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentCaseReadHospitalizationLayoutBinding contentBinding, Hospitalization hospitalization) {

    }

    @Override
    public void onPageResume(FragmentCaseReadHospitalizationLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    Case caze = getActivityRootData();

                    if (caze != null) {
                        if (caze.isUnreadOrChildUnread())
                            DatabaseHelper.getCaseDao().markAsRead(caze);

                        if (caze.getPerson() == null) {
                            caze.setPerson(DatabaseHelper.getPersonDao().build());
                        }

                        //TODO: Do we really need to do this
                        if (caze.getHospitalization() != null)
                            caze.setHospitalization(DatabaseHelper.getHospitalizationDao().queryUuid(caze.getHospitalization().getUuid()));
                    }

                    resultHolder.forItem().add(caze.getHospitalization());
                    resultHolder.forItem().add(caze);
                }
            });
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record = itemIterator.next();

                    if (itemIterator.hasNext())
                        caze = itemIterator.next();

                    if (record != null && caze != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_hospitalization_information);
    }

    @Override
    public Hospitalization getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_hospitalization_layout;
    }

    public static CaseReadHospitalizationFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(activityCommunicator, CaseReadHospitalizationFragment.class, capsule, activityRootData);
    }

    private ObservableArrayList getHospitalizations() {
        if (record != null && preHospitalizations != null)
            preHospitalizations.addAll(record.getPreviousHospitalizations());

        //preHospitalizations.addAll(MemoryDatabaseHelper.PREVIOUS_HOSPITALIZATION.getHospitalizations(2));
        return preHospitalizations;
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
