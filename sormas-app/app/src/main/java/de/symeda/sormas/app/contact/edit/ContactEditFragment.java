package de.symeda.sormas.app.contact.edit;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;


public class ContactEditFragment extends BaseEditActivityFragment<FragmentContactEditLayoutBinding, Contact, Contact> {

    private AsyncTask onResumeTask;

    private Contact record;
    private Case associatedCase;
    private View.OnClickListener createCaseCallback;
    private View.OnClickListener openCaseCallback;
    private OnLinkClickListener openCaseLinkCallback;

    private List<Item> relationshipList;

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

            resultHolder.forOther().add(DataUtils.getEnumItems(ContactRelation.class, false));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

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
    public void onLayoutBinding(FragmentContactEditLayoutBinding contentBinding) {

        //FieldHelper.initSpinnerField(binding.contactContactClassification, ContactClassification.class);
        //FieldHelper.initSpinnerField(binding.contactContactStatus, ContactStatus.class);
        //FieldHelper.initSpinnerField(binding.contactRelationToCase, ContactRelation.class);

        updateBottonPanel();


        setVisibilityByDisease(ContactDto.class, record.getCaseDisease(), contentBinding.mainContent);

        //contentBinding.contactLastContactDate.makeFieldSoftRequired();
        //contentBinding.contactContactProximity.makeFieldSoftRequired();
        //contentBinding.contactRelationToCase.makeFieldSoftRequired();

        contentBinding.setData(record);
        contentBinding.setCaze(associatedCase);
        contentBinding.setContactProximityClass(ContactProximity.class);
        contentBinding.setCreateCaseCallback(createCaseCallback);
        contentBinding.setOpenCaseLinkCallback(openCaseLinkCallback);
        contentBinding.setOpenCaseCallback(openCaseCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactEditLayoutBinding contentBinding) {
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
    protected void updateUI(FragmentContactEditLayoutBinding contentBinding, Contact contact) {

    }

    @Override
    public void onPageResume(FragmentContactEditLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
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
        return R.layout.fragment_contact_edit_layout;
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
        createCaseCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext())
                        .setContactUuid(record.getUuid()).setPersonUuid(record.getPerson().getUuid());
                CaseNewActivity.goToActivity(getContext(), dataCapsule);
            }
        };
        openCaseCallback = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCase();
            }
        };

        openCaseLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                openCase();
            }
        };
    }

    private void openCase() {
        if (associatedCase != null) {
            CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                    associatedCase.getUuid(), associatedCase.getCaseClassification());
            CaseEditActivity.goToActivity(getActivity(), dataCapsule);
        }
    }

    private void updateBottonPanel() {
        if (associatedCase == null) {
            getContentBinding().btnOpenCase.setVisibility(View.GONE);
        } else {
            getContentBinding().btnCreateCase.setVisibility(View.GONE);
        }

        if (getContentBinding().btnOpenCase.getVisibility() == View.GONE && getContentBinding().btnCreateCase.getVisibility() == View.GONE) {
            getContentBinding().contactPageBottomCtrlPanel.setVisibility(View.GONE);
        }
    }

    public static ContactEditFragment newInstance(ContactFormNavigationCapsule capsule, Contact activityRootData) {
        return newInstance(ContactEditFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
