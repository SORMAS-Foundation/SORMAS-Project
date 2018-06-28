package de.symeda.sormas.app.contact.edit;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.edit.CaseNewFragment;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactNewLayoutBinding;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 26/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactNewFragment extends BaseEditActivityFragment<FragmentContactNewLayoutBinding, Contact, Contact> {

    public static final String TAG = CaseNewFragment.class.getSimpleName();

    private AsyncTask onResumeTask;
    private AsyncTask saveContact;
    private AsyncTask createPersonTask;
    private String recordUuid = null;
    private ContactClassification pageStatus = null;
    private Contact record;
    private Case associatedCase;
    private List<Item> relationshipList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null) ? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (ContactClassification) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_new_contact);
    }

    @Override
    public Contact getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Contact contact = getActivityRootData();

            resultHolder.forItem().add(contact);
            resultHolder.forOther().add(DataUtils.getEnumItems(ContactRelation.class, false));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (record == null)
                getActivity().finish();

            if (itemIterator.hasNext())
                associatedCase = itemIterator.next();

            if (otherIterator.hasNext())
                relationshipList = otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactNewLayoutBinding contentBinding) {
        //FieldHelper.initSpinnerField(binding.contactContactClassification, ContactClassification.class);
        //FieldHelper.initSpinnerField(binding.contactContactStatus, ContactStatus.class);
        //FieldHelper.initSpinnerField(binding.contactRelationToCase, ContactRelation.class);

        //contentBinding.contactLastContactDate.makeFieldSoftRequired();
        //contentBinding.contactContactProximity.makeFieldSoftRequired();
        //contentBinding.contactRelationToCase.makeFieldSoftRequired();

        contentBinding.setData(record);
        contentBinding.setContactProximityClass(ContactProximity.class);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactNewLayoutBinding contentBinding) {
        contentBinding.spnContactRelationship.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (relationshipList.size() > 0) ? DataUtils.addEmptyItem(relationshipList)
                        : relationshipList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });


        contentBinding.dtpDateOfLastContact.setFragmentManager(getFragmentManager());
    }

    @Override
    protected void updateUI(FragmentContactNewLayoutBinding contentBinding, Contact contact) {

    }

    @Override
    public void onPageResume(FragmentContactNewLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
            @Override
            public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                //getActivityCommunicator().showPreloader();
                //getActivityCommunicator().hideFragmentView();
            }

            @Override
            public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                Contact contact = getActivityRootData();

                resultHolder.forItem().add(contact);
            }
        });
        onResumeTask = executor.execute(new ITaskResultCallback() {
            @Override
            public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                //getActivityCommunicator().hidePreloader();
                //getActivityCommunicator().showFragmentView();

                if (resultHolder == null) {
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
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_new_layout;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public boolean showSaveAction() {
        return true;
    }

    @Override
    public boolean showAddAction() {
        return false;
    }

    private void setupCallback() {

    }

    public static ContactNewFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule, Contact activityRootData) {
        return newInstance(activityCommunicator, ContactNewFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);

        if (saveContact != null && !saveContact.isCancelled())
            saveContact.cancel(true);

        if (createPersonTask != null && !createPersonTask.isCancelled())
            createPersonTask.cancel(true);
    }

}
