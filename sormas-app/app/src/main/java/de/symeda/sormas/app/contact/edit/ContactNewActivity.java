package de.symeda.sormas.app.contact.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.dialog.SelectOrCreatePersonDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;

public class ContactNewActivity extends BaseEditActivity<Contact> {

    public static final String TAG = ContactNewActivity.class.getSimpleName();

    private AsyncTask saveTask;
    private AsyncTask createPersonTask;
    private String caseUuid = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveCaseUuidState(outState, caseUuid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        caseUuid = getCaseUuidArg(savedInstanceState);
    }

    @Override
    protected Contact queryRootEntity(String recordUuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Contact buildRootEntity() {

        Person _person = DatabaseHelper.getPersonDao().build();
        Contact _contact = DatabaseHelper.getContactDao().build();

        // not null, because contact can only be created when the user has access to the case
        Case contactCase = DatabaseHelper.getCaseDao().queryUuidBasic(caseUuid);
        _contact.setCaseUuid(caseUuid);
        _contact.setCaseDisease(contactCase.getDisease());

        _contact.setPerson(_person);
        _contact.setReportDateTime(new Date());
        _contact.setContactClassification(ContactClassification.UNCONFIRMED);
        _contact.setContactStatus(ContactStatus.ACTIVE);
        _contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
        _contact.setReportingUser(ConfigProvider.getUser());

        return _contact;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_contact);
        return result;
    }

    @Override
    public ContactClassification getPageStatus() {
        return (ContactClassification) super.getPageStatus();
    }

    @Override
    protected BaseEditFragment buildEditFragment(LandingPageMenuItem menuItem, Contact activityRootData) {
        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactNewActivity.this,
                getRootEntityUuid(), getPageStatus());
        return ContactNewFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_contact_new;
    }

    @Override
    public void saveData() {

        final Contact contactToSave = getStoredRootEntity();

        if (contactToSave == null)
            return;

        DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                //TODO: Validation
                    /*ContactNewFragmentLayoutBinding binding = contactNewForm.getBinding();
                    ContactValidator.clearErrorsForNewContact(binding);
                    if (!ContactValidator.validateNewContact(contact, binding)) {
                        return true;
                    }*/
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) {
                List<PersonNameDto> existingPersons = DatabaseHelper.getPersonDao().getPersonNameDtos();
                List<Person> similarPersons = new ArrayList<>();
                for (PersonNameDto existingPerson : existingPersons) {
                    if (PersonHelper.areNamesSimilar(contactToSave.getPerson().getFirstName() + " " + contactToSave.getPerson().getLastName(),
                            existingPerson.getFirstName() + " " + existingPerson.getLastName())) {
                        Person person = DatabaseHelper.getPersonDao().queryForId(existingPerson.getId());
                        similarPersons.add(person);
                    }
                }
                resultHolder.forList().add(similarPersons);
            }
        };
        saveTask = executor.execute(new ITaskResultCallback() {
            @Override
            public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                //getBaseActivity().hidePreloader();
                //getBaseActivity().showFragmentView();

                if (resultHolder == null) {
                    return;
                }

                List<Person> existingPersons = new ArrayList<>();
                ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();

                if (listIterator.hasNext())
                    existingPersons = listIterator.next();


                if (existingPersons.size() > 0) {
                    final SelectOrCreatePersonDialog personDialog = new SelectOrCreatePersonDialog(BaseActivity.getActiveActivity(), contactToSave.getPerson(), existingPersons);
                    personDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                        @Override
                        public void onOkClick(View v, Object item, View viewRoot) {
                            personDialog.dismiss();

                            //Select
                            if (item instanceof Person) {
                                contactToSave.setPerson((Person) item);
                                savePersonAndContact(contactToSave);
                                goToCaseContacts();
                            }

                        }
                    });

                    personDialog.setOnCreateClickListener(new TeboAlertDialogInterface.CreateOnClickListener() {
                        @Override
                        public void onCreateClick(View v, Object item, View viewRoot) {
                            personDialog.dismiss();

                            if (item instanceof Person) {
                                contactToSave.setPerson((Person) item);
                                savePersonAndContact(contactToSave);
                                goToCaseContacts();
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
                    goToCaseContacts();
                }

            }
        });
    }

    private void savePersonAndContact(final Contact contactToSave) {

        createPersonTask = new DefaultAsyncTask(getContext(), contactToSave) {

            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException {

                if (contactToSave.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && contactToSave.getPerson().getAddress().isEmptyLocation()) {
                    Case contactCase = DatabaseHelper.getCaseDao().queryUuidBasic(contactToSave.getCaseUuid());
                    if (contactCase != null) {
                        contactToSave.getPerson().getAddress().setRegion(contactCase.getRegion());
                        contactToSave.getPerson().getAddress().setDistrict(contactCase.getDistrict());
                        contactToSave.getPerson().getAddress().setCommunity(contactCase.getCommunity());
                    }
                }

                DatabaseHelper.getPersonDao().saveAndSnapshot(contactToSave.getPerson());
                DatabaseHelper.getContactDao().saveAndSnapshot(contactToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {

                hidePreloader();

                if (taskResult.getResultStatus().isFailed()) {
                    NotificationHelper.showNotification(ContactNewActivity.this, NotificationType.ERROR,
                            String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_sample)));
                } else {
                    NotificationHelper.showNotification(ContactNewActivity.this, NotificationType.SUCCESS,
                            String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_sample)));

                    finish();
                }
            }
        }.executeOnThreadPool();
    }

    private void goToCaseContacts() {
        ContactNewActivity.this.finish();
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, ContactFormNavigationCapsule dataCapsule) {
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
