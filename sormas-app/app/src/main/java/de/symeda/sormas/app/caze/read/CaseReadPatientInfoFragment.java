package de.symeda.sormas.app.caze.read;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentCaseReadPatientInfoLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;

public class CaseReadPatientInfoFragment extends BaseReadActivityFragment<FragmentCaseReadPatientInfoLayoutBinding, Person, Case> {

    public static final String TAG = CaseReadPatientInfoFragment.class.getSimpleName();

    private AsyncTask onResumeTask;
    private Person record;

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case caze = getActivityRootData();

            if (caze != null) {
                if (caze.isUnreadOrChildUnread())
                    DatabaseHelper.getCaseDao().markAsRead(caze);

                if (caze.getPerson() == null) {
                    caze.setPerson(DatabaseHelper.getPersonDao().build());
                } else {
                    caze.setPerson(DatabaseHelper.getPersonDao().queryUuid(caze.getPerson().getUuid()));
                }
            }

            resultHolder.forItem().add(caze.getPerson());
            //resultHolder.forItem().add(caze);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (record == null)
                getActivity().finish();

            /*if (itemIterator.hasNext())
                caze = itemIterator.next();*/
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentCaseReadPatientInfoLayoutBinding contentBinding) {
        contentBinding.setData(record);
    }

    @Override
    public void onPageResume(FragmentCaseReadPatientInfoLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

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
                    } else {
                        caze.setPerson(DatabaseHelper.getPersonDao().queryUuid(caze.getPerson().getUuid()));
                    }
                }

                resultHolder.forItem().add(caze.getPerson());
                //resultHolder.forItem().add(caze);
            }
        };
        onResumeTask = executor.execute(new ITaskResultCallback() {
            @Override
            public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                //getBaseActivity().hidePreloader();
                //getBaseActivity().showFragmentView();

                if (resultHolder == null) {
                    return;
                }

                ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                if (itemIterator.hasNext())
                    record = itemIterator.next();

                    /*if (itemIterator.hasNext())
                        caze = itemIterator.next();*/

                if (record != null)
                    requestLayoutRebind();
                else {
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onAfterLayoutBinding(FragmentCaseReadPatientInfoLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentCaseReadPatientInfoLayoutBinding contentBinding, Person person) {

    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_patient_information);
    }

    @Override
    public Person getPrimaryData() {
        return record;
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_case_read_patient_info_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static CaseReadPatientInfoFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseReadPatientInfoFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
