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
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.contact.ContactSection;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.person.edit.PersonEditFragment;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.visit.edit.VisitNewActivity;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class ContactEditActivity extends BaseEditActivity<Contact> {

    public static final String TAG = ContactEditActivity.class.getSimpleName();

    private AsyncTask saveTask;

    public static void startActivity(Context context, String rootUuid, ContactSection section) {
        BaseActivity.startActivity(context, ContactEditActivity.class, buildBundle(rootUuid, section));
    }

    public static Bundler buildBundle(String rootUuid, ContactSection section) {
        return buildBundle(rootUuid, section.ordinal());
    }

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
        return getStoredRootEntity() == null ? null : getStoredRootEntity().getContactClassification();
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Contact activityRootData) {
        ContactSection section = ContactSection.fromMenuKey(menuItem.getKey());
        BaseEditFragment fragment;
        switch (section) {
            case CONTACT_INFO:
                fragment = ContactEditFragment.newInstance(activityRootData);
                break;
            case PERSON_INFO:
                fragment = PersonEditFragment.newInstance(activityRootData);
                break;
            case VISITS:
                fragment = ContactEditVisitsListFragment.newInstance(activityRootData);
                break;
            case TASKS:
                fragment = ContactEditTaskListFragment.newInstance(activityRootData);
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
        final Contact contactToSave = getStoredRootEntity();

        try {
            FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification(this, ERROR, e.getMessage());
            return;
        }


        saveTask = new SavingAsyncTask(getRootView(), contactToSave) {

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                DatabaseHelper.getPersonDao().saveAndSnapshot(contactToSave.getPerson());
                DatabaseHelper.getContactDao().saveAndSnapshot(contactToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                super.onPostExecute(taskResult);

                if (taskResult.getResultStatus().isSuccess()) {
                    goToNextPage();
                }
            }
        }.executeOnThreadPool();
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_contact_edit;
    }

    @Override
    public void goToNewView() {
        ContactSection activeSection = ContactSection.fromMenuKey(getActivePage().getKey());
        switch (activeSection) {
            case VISITS:
                VisitNewActivity.startActivity(this, getRootUuid());
                break;
            default:
                throw new IllegalArgumentException(activeSection.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}