package de.symeda.sormas.app.caze.read;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;

/**
 * Created by Orson on 08/01/2018.
 */

public class CaseReadFragment extends BaseReadActivityFragment<FragmentCaseReadLayoutBinding, Case, Case> {

    private AsyncTask onResumeTask;
    private String recordUuid = null;
    private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private Case record;
    private User user;

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
            }

            resultHolder.forItem().add(caze);
            resultHolder.forItem().add(ConfigProvider.getUser());
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (itemIterator.hasNext())
                user = itemIterator.next();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentCaseReadLayoutBinding contentBinding, Case aCase) {

    }

    @Override
    public void onPageResume(FragmentCaseReadLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
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
                    }

                    resultHolder.forItem().add(caze);
                    resultHolder.forItem().add(ConfigProvider.getUser());
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
                        record =  itemIterator.next();

                    if (itemIterator.hasNext())
                        user = itemIterator.next();

                    if (record != null)
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
        return r.getString(R.string.caption_case_information);
    }

    @Override
    public Case getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_layout;
    }

    public static CaseReadFragment newInstance(IActivityCommunicator activityCommunicator, CaseFormNavigationCapsule capsule, Case activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseReadFragment.class, capsule, activityRootData);
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
