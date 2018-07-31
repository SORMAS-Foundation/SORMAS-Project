package de.symeda.sormas.app.caze.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseSection;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.contact.edit.ContactNewActivity;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.person.edit.PersonEditFragment;
import de.symeda.sormas.app.sample.edit.SampleNewActivity;
import de.symeda.sormas.app.symptoms.SymptomsEditFragment;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.Consumer;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class CaseEditActivity extends BaseEditActivity<Case> {

    public static final String TAG = CaseEditActivity.class.getSimpleName();

    private AsyncTask saveTask;

    public static void startActivity(Context context, String recordUuid, CaseSection section) {
        BaseActivity.startActivity(context, CaseEditActivity.class, buildBundle(recordUuid, section));
    }

    public static Bundler buildBundle(String recordUuid, CaseSection section) {
        return BaseEditActivity.buildBundle(recordUuid, section.ordinal());
    }


    @Override
    protected Case queryRootEntity(String recordUuid) {
        return DatabaseHelper.getCaseDao().queryUuidWithEmbedded(recordUuid);
    }

    @Override
    protected Case buildRootEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CaseClassification getPageStatus() {
        return getStoredRootEntity() == null ? null : getStoredRootEntity().getCaseClassification();
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_case_menu;
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Case activityRootData) {
        CaseSection section = CaseSection.fromMenuKey(menuItem.getKey());
        BaseEditFragment fragment;
        switch (section) {

            case CASE_INFO:
                fragment = CaseEditFragment.newInstance(activityRootData);
                break;
            case PERSON_INFO:
                fragment = PersonEditFragment.newInstance(activityRootData);
                break;
            case HOSPITALIZATION:
                fragment = CaseEditHospitalizationFragment.newInstance(activityRootData);
                break;
            case SYMPTOMS:
                fragment = SymptomsEditFragment.newInstance(activityRootData);
                break;
            case EPIDEMIOLOGICAL_DATA:
                fragment = CaseEditEpidemiologicalDataFragment.newInstance(activityRootData);
                break;
            case CONTACTS:
                fragment = CaseEditContactListFragment.newInstance(activityRootData);
                break;
            case SAMPLES:
                fragment = CaseEditSampleListFragment.newInstance(activityRootData);
                break;
            case TASKS:
                fragment = CaseEditTaskListFragment.newInstance(activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }

        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_case);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_case_edit;
    }

    @Override
    public void saveData() {
        saveData(new Consumer<Case>() {
            @Override
            public void accept(Case parameter) {
                goToNextPage();
            }
        });
    }

    public void saveData(final Consumer<Case> successCallback) {
        final Case cazeToSave = getStoredRootEntity();

        try {
            FragmentValidator.validate(getContext(), getActiveFragment().getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification(this, ERROR, e.getMessage());
            return;
        }

        saveTask = new SavingAsyncTask(getRootView(), cazeToSave) {
            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            protected void doInBackground(TaskResultHolder resultHolder) throws DaoException {
                DatabaseHelper.getPersonDao().saveAndSnapshot(cazeToSave.getPerson());
                DatabaseHelper.getCaseDao().saveAndSnapshot(cazeToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();
                super.onPostExecute(taskResult);
                if (taskResult.getResultStatus().isSuccess()) {
                    successCallback.accept(cazeToSave);
                } else {
                    onResume(); // reload data
                }
            }
        }.executeOnThreadPool();
    }

    @Override
    public void goToNewView() {
        CaseSection activeSection = CaseSection.fromMenuKey(getActivePage().getKey());

        if (activeSection == CaseSection.CONTACTS) {
            ContactNewActivity.startActivity(getContext(), getRootUuid());
        } else if (activeSection == CaseSection.SAMPLES) {
            SampleNewActivity.startActivity(getContext(), getRootUuid());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }

}