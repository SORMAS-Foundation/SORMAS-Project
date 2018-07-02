package de.symeda.sormas.app.contact.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.shared.ContactFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public class ContactEditActivity extends BaseEditActivity<Contact> {

    public static final String TAG = ContactEditActivity.class.getSimpleName();

    private AsyncTask saveTask;

    @Override
    protected Contact queryRootEntity(String recordUuid) {
        return DatabaseHelper.getContactDao().queryUuid(recordUuid);
    }

    @Override
    protected Contact buildRootEntity() {
        Person _person = DatabaseHelper.getPersonDao().build();
        Contact _contact = DatabaseHelper.getContactDao().build();

        _contact.setPerson(_person);
        _contact.setReportDateTime(new Date());
        _contact.setContactClassification(ContactClassification.UNCONFIRMED);
        _contact.setContactStatus(ContactStatus.ACTIVE);
        _contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
        _contact.setReportingUser(ConfigProvider.getUser());

        return _contact;
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

        // TODO validation
//        PersonValidator.clearErrors(getContentBinding());
//        if (!PersonValidator.validatePersonData(nContext, personToSave, getContentBinding())) {
//            return;
//        }

        saveTask = new DefaultAsyncTask(getContext(), contactToSave) {

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                DatabaseHelper.getPersonDao().saveAndSnapshot(contactToSave.getPerson());
                DatabaseHelper.getContactDao().saveAndSnapshot(contactToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {

                if (taskResult.getResultStatus().isFailed()) {
                    NotificationHelper.showNotification(ContactEditActivity.this, NotificationType.ERROR,
                            String.format(getResources().getString(R.string.snackbar_save_error), contactToSave.getEntityName()));
                } else {
                    NotificationHelper.showNotification(ContactEditActivity.this, NotificationType.SUCCESS,
                            String.format(getResources().getString(R.string.snackbar_save_success), contactToSave.getEntityName()));

                    goToNextMenu();
                }

            }
        }.executeOnThreadPool();
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
            // TODO #651
//            VisitFormNavigationCapsule dataCapsule = new VisitFormNavigationCapsule(getContext(),
//                    VisitStatus.COOPERATIVE).setContactUuid(recordUuid);
//            VisitNewActivity.goToActivity(this, dataCapsule);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}