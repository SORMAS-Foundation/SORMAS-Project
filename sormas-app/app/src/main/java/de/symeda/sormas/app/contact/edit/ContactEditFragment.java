package de.symeda.sormas.app.contact.edit;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.ISaveableWithCallback;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;

/**
 * Created by Orson on 12/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactEditFragment extends BaseEditActivityFragment<FragmentContactEditLayoutBinding, Contact, Contact> implements ISaveableWithCallback {

    private AsyncTask onResumeTask;
    private AsyncTask saveContact;
    private String recordUuid = null;
    private ContactClassification pageStatus = null;
    private Contact record;
    private Case associatedCase;
    private View.OnClickListener createCaseCallback;
    private View.OnClickListener openCaseCallback;
    private OnLinkClickListener openCaseLinkCallback;

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
                //getActivityCommunicator().showPreloader();
                //getActivityCommunicator().hideFragmentView();
            }

            @Override
            public void execute(TaskResultHolder resultHolder) {
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
                CaseFormNavigationCapsule dataCapsule = (CaseFormNavigationCapsule) new CaseFormNavigationCapsule(getContext())
                        .setContactUuid(recordUuid).setPersonUuid(record.getPerson().getUuid());
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
                    associatedCase.getUuid()).setReadPageStatus(associatedCase.getCaseClassification());
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

    public static ContactEditFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule, Contact activityRootData) {
        return newInstance(activityCommunicator, ContactEditFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);

        if (saveContact != null && !saveContact.isCancelled())
            saveContact.cancel(true);
    }

    @Override
    public void save(final NotificationContext nContext, final Callback.IAction callback) {
        final Contact contactToSave = getActivityRootData();

        if (contactToSave == null)
            throw new IllegalArgumentException("contactToSave is null");

        final Person personToSave = contactToSave.getPerson();

        if (personToSave == null)
            throw new IllegalArgumentException("personToSave is null");

        DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
            private ContactDao cDao;
            private PersonDao pDao;
            private String saveUnsuccessful;

            @Override
            public void onPreExecute() {
                cDao = DatabaseHelper.getContactDao();
                pDao = DatabaseHelper.getPersonDao();
                saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_contact));

                if (contactToSave.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && personToSave.getAddress().isEmptyLocation()) {
                    Case contactCase = DatabaseHelper.getCaseDao().queryUuidBasic(contactToSave.getCaseUuid());
                    if (contactCase != null) {
                        personToSave.getAddress().setRegion(contactCase.getRegion());
                        personToSave.getAddress().setDistrict(contactCase.getDistrict());
                        personToSave.getAddress().setCommunity(contactCase.getCommunity());
                    }
                }
            }

            @Override
            public void execute(TaskResultHolder resultHolder) {
                try {
                    if (personToSave != null)
                        pDao.saveAndSnapshot(personToSave);

                    if (contactToSave != null)
                        cDao.saveAndSnapshot(contactToSave);
                } catch (DaoException e) {
                    Log.e(getClass().getName(), "Error while trying to save contact", e);
                    resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                    ErrorReportingHelper.sendCaughtException(tracker, e, contactToSave, true);
                }
            }
        };
        saveContact = executor.execute(new ITaskResultCallback() {
            @Override
            public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                //getActivityCommunicator().hidePreloader();
                //getActivityCommunicator().showFragmentView();

                if (resultHolder == null) {
                    return;
                }

                if (!resultStatus.isSuccess()) {
                    NotificationHelper.showNotification(nContext, NotificationType.ERROR, resultStatus.getMessage());
                    return;
                } else {
                    NotificationHelper.showNotification(nContext, NotificationType.SUCCESS, "Contact " + DataHelper.getShortUuid(contactToSave.getUuid()) + " saved");
                }

                if (callback != null)
                    callback.call(null);
            }
        });
    }
}
