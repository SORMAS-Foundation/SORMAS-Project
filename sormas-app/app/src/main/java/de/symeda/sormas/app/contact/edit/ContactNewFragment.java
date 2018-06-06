package de.symeda.sormas.app.contact.edit;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.caze.edit.CaseNewFragment;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.SelectOrCreatePersonDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.ISaveableWithCallback;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentContactNewLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.util.TimeoutHelper;
import de.symeda.sormas.app.validation.ContactValidator;

/**
 * Created by Orson on 26/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactNewFragment extends BaseEditActivityFragment<FragmentContactNewLayoutBinding, Contact, Contact> implements ISaveableWithCallback {

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
//            resultHolder.forItem().add(contact.getCaze());
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
                relationshipList =  otherIterator.next();

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


        contentBinding.dtpDateOfLastContact.initialize(getFragmentManager());
    }

    @Override
    protected void updateUI(FragmentContactNewLayoutBinding contentBinding, Contact contact) {

    }

    @Override
    public void onPageResume(FragmentContactNewLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
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
                    Contact contact = getActivityRootData();

                    resultHolder.forItem().add(contact);
//                    resultHolder.forItem().add(contact.getCaze());
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

    public static ContactNewFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormNavigationCapsule capsule, Contact activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
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

    @Override
    public void save(final INotificationContext nContext, final Callback.IAction callback) {
        final Contact contactToSave = getActivityRootData();

        if (contactToSave == null)
            throw new IllegalArgumentException("contactToSave is null");

        final Person personToSave = contactToSave.getPerson();

        if (personToSave == null)
            throw new IllegalArgumentException("personToSave is null");

        ContactValidator.clearErrorsForNewContact(getContentBinding());
        if (!ContactValidator.validateNewContact(nContext, contactToSave, getContentBinding())) {
            return;
        }

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //TODO: Validation
                    /*ContactNewFragmentLayoutBinding binding = contactNewForm.getBinding();
                    ContactValidator.clearErrorsForNewContact(binding);
                    if (!ContactValidator.validateNewContact(contact, binding)) {
                        return true;
                    }*/
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
//                    List<Person> existingPersons = DatabaseHelper.getPersonDao().getAllByName(contactToSave.getPerson().getFirstName(), contactToSave.getPerson().getLastName());
//                    resultHolder.forList().add(existingPersons);
                }
            });
            saveContact = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    List<Person> existingPersons = new ArrayList<>();
                    ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

                    if (listIterator.hasNext())
                        existingPersons = listIterator.next();


                    if (existingPersons.size() > 0) {
                        final SelectOrCreatePersonDialog personDialog = new SelectOrCreatePersonDialog(AbstractSormasActivity.getActiveActivity(), contactToSave.getPerson(), existingPersons);
                        personDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                            @Override
                            public void onOkClick(View v, Object item, View viewRoot) {
                                personDialog.dismiss();

                                //Select
                                if (item instanceof Person) {
                                    contactToSave.setPerson((Person)item);
                                    savePersonAndContact(nContext, contactToSave, callback);
                                }

                            }
                        });

                        personDialog.setOnCreateClickListener(new TeboAlertDialogInterface.CreateOnClickListener() {
                            @Override
                            public void onCreateClick(View v, Object item, View viewRoot) {
                                personDialog.dismiss();

                                if (item instanceof Person) {
                                    contactToSave.setPerson((Person)item);
                                    savePersonAndContact(nContext, contactToSave, callback);
                                }
                            }
                        });

                        personDialog.setOnCancelClickListener(new TeboAlertDialogInterface.CancelOnClickListener() {

                            @Override
                            public void onCancelClick(View v, Object item, View viewRoot) {
                                personDialog.dismiss();
                            }
                        });

                        personDialog.show(null);
                    } else {
                        savePersonAndContact(nContext, contactToSave, callback);
                    }

                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
    }

    private void savePersonAndContact(final INotificationContext nContext, final Contact contactToSave, final Callback.IAction callback) {

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private String saveUnsuccessful;

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    getActivityCommunicator().showPreloader();
                    getActivityCommunicator().hideFragmentView();

                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_contact));

//                    if(contactToSave.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && contactToSave.getPerson().getAddress().isEmptyLocation()) {
//                        contactToSave.getPerson().getAddress().setRegion(contactToSave.getCaze().getRegion());
//                        contactToSave.getPerson().getAddress().setDistrict(contactToSave.getCaze().getDistrict());
//                        contactToSave.getPerson().getAddress().setCommunity(contactToSave.getCaze().getCommunity());
//                    }
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    try {
                        PersonDao personDao = DatabaseHelper.getPersonDao();
                        ContactDao contactDao = DatabaseHelper.getContactDao();
                        personDao.saveAndSnapshot(contactToSave.getPerson());
                        contactDao.saveAndSnapshot(contactToSave);
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save case", e);
                        resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                        ErrorReportingHelper.sendCaughtException(tracker, e, null, true);
                    }
                }
            });
            createPersonTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    getActivityCommunicator().hidePreloader();
                    getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, getContext(), new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    NotificationHelper.showNotification(nContext, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_contact)));
                                } else {
                                    NotificationHelper.showNotification(nContext, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_contact)));
                                }

                                TimeoutHelper.executeIn5Seconds(new Callback.IAction<AsyncTask>() {
                                    @Override
                                    public void call(AsyncTask result) {
                                        if (callback != null)
                                            callback.call(null);
                                    }
                                });

                            }
                        });
                    } else {
                        NotificationHelper.showNotification(nContext, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_contact)));
                        TimeoutHelper.executeIn5Seconds(new Callback.IAction<AsyncTask>() {
                            @Override
                            public void call(AsyncTask result) {
                                if (callback != null)
                                    callback.call(null);
                            }
                        });
                    }
                }
            });
        } catch (Exception ex) {
            getActivityCommunicator().hidePreloader();
            getActivityCommunicator().showFragmentView();
        }
    }
}
