package de.symeda.sormas.app.contact.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import java.util.Date;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.person.SelectOrCreatePersonDialog;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.validation.ContactValidator;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class ContactNewActivity extends BaseEditActivity<Contact> {

    public static final String TAG = ContactNewActivity.class.getSimpleName();

    private AsyncTask saveTask;
    private String caseUuid = null;

    @Override
    protected void onCreateInner(Bundle savedInstanceState) {
        super.onCreateInner(savedInstanceState);
        caseUuid = getCaseUuidArg(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveCaseUuidState(outState, caseUuid);
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
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Contact activityRootData) {
        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactNewActivity.this,
                getRootEntityUuid(), getPageStatus());
        return ContactNewFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_contact_new;
    }

    @Override
    public void replaceFragment(BaseEditFragment f) {
        super.replaceFragment(f);
        getActiveFragment().setLiveValidationDisabled(true);
    }

    @Override
    public void saveData() {
        final Contact contactToSave = getStoredRootEntity();

        ContactNewFragment fragment = (ContactNewFragment) getActiveFragment();

        if (fragment.isLiveValidationDisabled()) {
            fragment.disableLiveValidation(false);
        }

        try {
            ContactValidator.validateNewContact(getContext(), fragment.getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification((NotificationContext) getContext(), ERROR, e.getMessage());
            return;
        }

        SelectOrCreatePersonDialog.selectOrCreatePerson(contactToSave.getPerson(), new Consumer<Person>() {
            @Override
            public void accept(Person person) {
                contactToSave.setPerson(person);

                saveTask = new SavingAsyncTask(getRootView(), contactToSave) {
                    @Override
                    protected void onPreExecute() {
                        showPreloader();
                    }

                    @Override
                    protected void doInBackground(TaskResultHolder resultHolder) throws Exception {
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
                        super.onPostExecute(taskResult);
                        if (taskResult.getResultStatus().isSuccess()) {
                            finish();
                        }
                    }
                }.executeOnThreadPool();
            }
        });
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, ContactFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, ContactNewActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}
