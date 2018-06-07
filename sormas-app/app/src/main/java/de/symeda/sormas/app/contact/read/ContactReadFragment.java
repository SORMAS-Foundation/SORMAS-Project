package de.symeda.sormas.app.contact.read;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactReadLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;

/**
 * Created by Orson on 01/01/2018.
 */

public class ContactReadFragment extends BaseReadActivityFragment<FragmentContactReadLayoutBinding, Contact, Contact> {

    private AsyncTask onResumeTask;
    private String recordUuid = null;
    private ContactClassification pageStatus = null;
    private Contact record;
    private Case associatedCase;
    //private View.OnClickListener createCaseCallback;
    private OnLinkClickListener openCaseLinkCallback;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (ContactClassification) getPageStatusArg(arguments);
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Case _associatedCase = null;
            Contact contact = getActivityRootData();

            if (contact != null) {
                if (contact.isUnreadOrChildUnread())
                    DatabaseHelper.getContactDao().markAsRead(contact);

                _associatedCase = DatabaseHelper.getCaseDao().queryUuidBasic(contact.getCaseUuid());
            }

            resultHolder.forItem().add(contact);
            resultHolder.forItem().add(_associatedCase);
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (itemIterator.hasNext())
                associatedCase = itemIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactReadLayoutBinding contentBinding) {
        setVisibilityByDisease(ContactDto.class, record.getCaseDisease(), contentBinding.mainContent);

        //contentBinding.setCreateCaseCallback(createCaseCallback);
        contentBinding.setOpenCaseLinkCallback(openCaseLinkCallback);
        contentBinding.setData(record);
        contentBinding.setCaze(associatedCase);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactReadLayoutBinding contentBinding) {

    }

    @Override
    protected void updateUI(FragmentContactReadLayoutBinding contentBinding, Contact contact) {

    }

    @Override
    public void onPageResume(FragmentContactReadLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
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
                    Case _associatedCase = null;
                    Contact contact = getActivityRootData();

                    if (contact != null) {
                        if (contact.isUnreadOrChildUnread())
                            DatabaseHelper.getContactDao().markAsRead(contact);

                        _associatedCase = DatabaseHelper.getCaseDao().queryUuidBasic(contact.getCaseUuid());
                    }

                    resultHolder.forItem().add(contact);
                    resultHolder.forItem().add(_associatedCase);
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
                        associatedCase = itemIterator.next();

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
        return r.getString(R.string.caption_contact_information);
    }

    @Override
    public Contact getPrimaryData() {
        return record;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getReadLayout() {
        return R.layout.fragment_contact_read_layout;
    }

    private void setupCallback() {
        /*createCaseCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseFormNavigationCapsule dataCapsule = (CaseFormNavigationCapsule)new CaseFormNavigationCapsule(getContext(),
                        "").setContactUuid(recordUuid);
                CaseNewActivity.goToActivity(getContext(), dataCapsule);
            }
        };*/

        openCaseLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (associatedCase != null) {
                    CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                            associatedCase.getUuid()).setReadPageStatus(associatedCase.getCaseClassification());
                    CaseReadActivity.goToActivity(getActivity(), dataCapsule);
                }
            }
        };
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static ContactReadFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule, Contact activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactReadFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }

}
