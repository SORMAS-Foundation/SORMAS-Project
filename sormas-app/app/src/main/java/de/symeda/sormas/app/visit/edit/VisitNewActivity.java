package de.symeda.sormas.app.visit.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.visit.VisitSection;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class VisitNewActivity extends BaseEditActivity<Visit> {

    public static final String TAG = VisitNewActivity.class.getSimpleName();

    private AsyncTask saveTask;

    private String contactUuid = null;


    public static void startActivity(Context context, String contactUuid) {
        BaseEditActivity.startActivity(context, VisitEditActivity.class, buildBundle(contactUuid));
    }

    public static Bundler buildBundle(String contactUuid) {
        return buildBundle(null, 0).setContactUuid(contactUuid);
    }

    @Override
    protected void onCreateInner(Bundle savedInstanceState) {
        super.onCreateInner(savedInstanceState);
        contactUuid = new Bundler(savedInstanceState).getContactUuid();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        new Bundler(outState).setContactUuid(contactUuid);
    }

    @Override
    protected Visit queryRootEntity(String recordUuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visit buildRootEntity() {
        Visit visit = DatabaseHelper.getVisitDao().build(contactUuid);
        return visit;
    }

    @Override
    public VisitStatus getPageStatus() {
        return null;
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Visit activityRootData) {
        return VisitEditFragment.newInstance(activityRootData, contactUuid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_followup);
        return result;
    }

    @Override
    public void replaceFragment(BaseEditFragment f, boolean allowBackNavigation) {
        super.replaceFragment(f, allowBackNavigation);
        getActiveFragment().setLiveValidationDisabled(true);
    }

    @Override
    public void saveData() {
        final Visit visitToSave = getStoredRootEntity();
        VisitEditFragment fragment = (VisitEditFragment) getActiveFragment();

        if (fragment.isLiveValidationDisabled()) {
            fragment.disableLiveValidation(false);
        }

        try {
            FragmentValidator.validate(getContext(), fragment.getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification(this, ERROR, e.getMessage());
            return;
        }

        saveTask = new SavingAsyncTask(getRootView(), visitToSave) {

            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws Exception {
                DatabaseHelper.getVisitDao().saveAndSnapshot(visitToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();
                super.onPostExecute(taskResult);
                if (taskResult.getResultStatus().isSuccess()) {
                    if (visitToSave.getVisitStatus() == VisitStatus.COOPERATIVE) {
                        // enter symptoms
                        finish();
                        VisitEditActivity.startActivity(getContext(), visitToSave.getUuid(), contactUuid, VisitSection.SYMPTOMS);
                    } else {
                        finish(); // back to contact
                    }
                }
            }
        }.executeOnThreadPool();
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_1_contact_visit_info;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}