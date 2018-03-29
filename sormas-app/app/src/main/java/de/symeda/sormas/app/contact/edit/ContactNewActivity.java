package de.symeda.sormas.app.contact.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.component.dialog.SelectOrCreatePersonDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.ICallback;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.NavigationHelper;
import de.symeda.sormas.app.util.SyncCallback;
import de.symeda.sormas.app.util.TimeoutHelper;

/**
 * Created by Orson on 26/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactNewActivity extends BaseEditActivity<Contact> {

    public static final String TAG = ContactNewActivity.class.getSimpleName();

    private AsyncTask saveTask;
    private AsyncTask createPersonTask;
    private ContactClassification pageStatus = null;
    private String recordUuid = null;
    private String caseUuid = null;
    private BaseEditActivityFragment activeFragment = null;
    private MenuItem saveMenu = null;
    private MenuItem addMenu = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
        SaveCaseUuidState(outState, caseUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        pageStatus = (ContactClassification) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
        caseUuid = getCaseUuidArg(arguments);
    }

    @Override
    protected Contact getActivityRootData(String recordUuid) {
        Contact _contact = DatabaseHelper.getContactDao().queryUuid(recordUuid);
        Case _associatedCase;

        if (_contact == null)
            return null;

        if (caseUuid != null && !caseUuid.isEmpty()) {
            _associatedCase = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
        } else {
            _associatedCase = DatabaseHelper.getCaseDao().build();
        }

        _contact.setCaze(_associatedCase);
        _contact.setReportDateTime(new Date());
        _contact.setContactClassification(ContactClassification.UNCONFIRMED);
        _contact.setContactStatus(ContactStatus.ACTIVE);
        _contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
        _contact.setReportingUser(ConfigProvider.getUser());

        return _contact;
    }

    @Override
    protected Contact getActivityRootDataIfRecordUuidNull() {
        Case _associatedCase;
        Person _person = DatabaseHelper.getPersonDao().build();
        Contact _contact = DatabaseHelper.getContactDao().build();

        if (caseUuid != null && !caseUuid.isEmpty()) {
            _associatedCase = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
        } else {
            return null;
        }

        _contact.setCaze(_associatedCase);
        _contact.setPerson(_person);
        _contact.setReportDateTime(new Date());
        _contact.setContactClassification(ContactClassification.UNCONFIRMED);
        _contact.setContactStatus(ContactStatus.ACTIVE);
        _contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
        _contact.setReportingUser(ConfigProvider.getUser());

        return _contact;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment(Contact activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactNewActivity.this,
                    recordUuid, pageStatus);
            activeFragment = ContactNewFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_contact);

        return true;
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_action_menu, menu);

        saveMenu = menu.findItem(R.id.action_save);
        addMenu = menu.findItem(R.id.action_new);

        saveMenu.setTitle(R.string.action_save_contact);

        saveMenu.setVisible(true);
        addMenu.setVisible(false);

        processActionbarMenu();

        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavigationHelper.navigateUpFrom(this);
                return true;

            case R.id.action_save:
                saveData();
                return true;

            case R.id.option_menu_action_sync:
                //synchronizeChangedData();
                return true;

            case R.id.option_menu_action_markAllAsRead:
                /*CaseDao caseDao = DatabaseHelper.getCaseDao();
                PersonDao personDao = DatabaseHelper.getPersonDao();
                List<Case> cases = caseDao.queryForAll();
                for (Case caseToMark : cases) {
                    caseDao.markAsRead(caseToMark);
                }

                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof CasesListFragment) {
                        fragment.onResume();
                    }
                }*/
                return true;

            // Report problem button
            case R.id.action_report:
                /*UserReportDialog userReportDialog = new UserReportDialog(this, this.getClass().getSimpleName(), null);
                AlertDialog dialog = userReportDialog.create();
                dialog.show();*/

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_contact_new;
    }

    private void saveData() {
        if (activeFragment == null)
            return;

        final Contact contactToSave = getStoredActivityRootData();

        if (contactToSave == null)
            return;

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
                    List<Person> existingPersons = DatabaseHelper.getPersonDao().getAllByName(contactToSave.getPerson().getFirstName(), contactToSave.getPerson().getLastName());
                    resultHolder.forList().add(existingPersons);
                }
            });
            saveTask = executor.execute(new ITaskResultCallback() {
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
                                    savePersonAndContact(contactToSave);
                                }

                            }
                        });

                        personDialog.setOnCreateClickListener(new TeboAlertDialogInterface.CreateOnClickListener() {
                            @Override
                            public void onCreateClick(View v, Object item, View viewRoot) {
                                personDialog.dismiss();

                                if (item instanceof Person) {
                                    contactToSave.setPerson((Person)item);
                                    savePersonAndContact(contactToSave);
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
                        savePersonAndContact(contactToSave);
                    }

                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
    }

    private void savePersonAndContact(final Contact contactToSave) {

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private String saveUnsuccessful;

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    showPreloader();
                    hideFragmentView();

                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_create_error), getResources().getString(R.string.entity_contact));

                    if(contactToSave.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && contactToSave.getPerson().getAddress().isEmptyLocation()) {
                        contactToSave.getPerson().getAddress().setRegion(contactToSave.getCaze().getRegion());
                        contactToSave.getPerson().getAddress().setDistrict(contactToSave.getCaze().getDistrict());
                        contactToSave.getPerson().getAddress().setCommunity(contactToSave.getCaze().getCommunity());
                    }
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
                    hidePreloader();
                    showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, ContactNewActivity.this, new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    NotificationHelper.showNotification(ContactNewActivity.this, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_contact)));
                                } else {
                                    NotificationHelper.showNotification(ContactNewActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_contact)));
                                }

                                TimeoutHelper.executeIn5Seconds(new ICallback<AsyncTask>() {
                                    @Override
                                    public void result(AsyncTask result) {
                                        goToCaseContacts();
                                    }
                                });

                            }
                        });
                    } else {
                        NotificationHelper.showNotification(ContactNewActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_contact)));
                        TimeoutHelper.executeIn5Seconds(new ICallback<AsyncTask>() {
                            @Override
                            public void result(AsyncTask result) {
                                goToCaseContacts();
                            }
                        });
                    }
                }
            });
        } catch (Exception ex) {
            hidePreloader();
            showFragmentView();
        }

    }

    private void goToCaseContacts() {
        ContactNewActivity.this.finish();
        //NavigationHelper.navigateUpFrom(this);
        /*Contact record = getStoredActivityRootData();

        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(getContext(),
                record.getCaze().getUuid()).setEditPageStatus(record.getCaze().getInvestigationStatus());
        CaseEditActivity.goToActivity(ContactNewActivity.this, dataCapsule);*/
    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, ContactFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, ContactNewActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);

        if (createPersonTask != null && !createPersonTask.isCancelled())
            createPersonTask.cancel(true);
    }
}
