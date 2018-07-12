package de.symeda.sormas.app.contact.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.visit.edit.VisitNewActivity;

public class ContactEditActivity extends BaseEditActivity<Contact> {

    public static final String TAG = ContactEditActivity.class.getSimpleName();

    private AsyncTask saveTask;

    @Override
    protected Contact queryRootEntity(String recordUuid) {
        return DatabaseHelper.getContactDao().queryUuid(recordUuid);
    }

    @Override
    protected Contact buildRootEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_contact_menu;
    }

    @Override
    public ContactClassification getPageStatus() {
        return (ContactClassification)super.getPageStatus();
    }

    @Override
    protected BaseEditFragment buildEditFragment(LandingPageMenuItem menuItem, Contact activityRootData) {
        ContactFormNavigationCapsule dataCapsule = new ContactFormNavigationCapsule(ContactEditActivity.this,
                getRootEntityUuid(), getPageStatus());

        ContactSection section = ContactSection.fromMenuKey(menuItem.getKey());
        BaseEditFragment fragment;
        switch (section) {
            case CONTACT_INFO:
                fragment = ContactEditFragment.newInstance(dataCapsule, activityRootData);
                break;
            case PERSON_INFO:
                fragment = ContactEditPersonFragment.newInstance(dataCapsule, activityRootData);
                break;
            case VISITS:
                fragment = ContactEditVisitsListFragment.newInstance(dataCapsule, activityRootData);
                break;
            case TASKS:
                fragment = ContactEditTaskListFragment.newInstance(dataCapsule, activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_contact);

        return true;
    }

    @Override
    public void saveData() {

        ContactSection activeSection = ContactSection.fromMenuKey(getActiveMenuItem().getKey());

        if (activeSection == ContactSection.VISITS || activeSection == ContactSection.TASKS)
            return;

        final Contact contactToSave = getStoredRootEntity();

        saveTask = new SavingAsyncTask(getRootView(), contactToSave) {

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException, ValidationException {
                validateData(contactToSave);
                DatabaseHelper.getPersonDao().saveAndSnapshot(contactToSave.getPerson());
                DatabaseHelper.getContactDao().saveAndSnapshot(contactToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                super.onPostExecute(taskResult);

                if (taskResult.getResultStatus().isSuccess()) {
                    goToNextMenu();
                }
            }
        }.executeOnThreadPool();
    }

    private void validateData(Contact data) throws ValidationException {

        // TODO validation
//        PersonValidator.clearErrors(getContentBinding());
//        if (!PersonValidator.validatePersonData(nContext, personToSave, getContentBinding())) {
//            return;
//        }
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_contact_edit;
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, ContactFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, ContactEditActivity.class, dataCapsule);
    }

    @Override
    public void goToNewView() {
        ContactSection activeSection = ContactSection.fromMenuKey(getActiveMenuItem().getKey());
        if (activeSection == ContactSection.VISITS) {
            VisitNewActivity.goToActivity(this, getRootEntityUuid());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}