package de.symeda.sormas.app.sample.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import java.util.Date;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.sample.ShipmentStatus;
import de.symeda.sormas.app.util.Bundler;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

public class SampleNewActivity extends BaseEditActivity<Sample> {

    public static final String TAG = SampleNewActivity.class.getSimpleName();

    private String caseUuid = null;

    private AsyncTask saveTask;

    public static void startActivity(Context context, String caseUuid) {
        BaseEditActivity.startActivity(context, SampleNewActivity.class, buildBundle(caseUuid));
    }

    public static Bundler buildBundle(String caseUuid) {
        return buildBundle(null, 0).setCaseUuid(caseUuid);
    }


    @Override
    public ShipmentStatus getPageStatus() {
        return null;
    }

    @Override
    protected void onCreateInner(Bundle savedInstanceState) {
        super.onCreateInner(savedInstanceState);
        caseUuid = new Bundler(savedInstanceState).getCaseUuid();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        new Bundler(outState).setCaseUuid(caseUuid);
    }

    @Override
    protected Sample queryRootEntity(String recordUuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Sample buildRootEntity() {
        // basic instead of reference, because we want to have at least the related person
        Case associatedCase = DatabaseHelper.getCaseDao().queryUuidBasic(caseUuid);
        return DatabaseHelper.getSampleDao().build(associatedCase);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_sample);
        return result;
    }

    @Override
    protected BaseEditFragment buildEditFragment(PageMenuItem menuItem, Sample activityRootData) {
        BaseEditFragment fragment = SampleNewFragment.newInstance(activityRootData);
        fragment.setLiveValidationDisabled(true);
        return fragment;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_sample_new;
    }


    @Override
    public void replaceFragment(BaseEditFragment f, boolean allowBackNavigation) {
        super.replaceFragment(f, allowBackNavigation);
    }

    @Override
    public void saveData() {
        final Sample sampleToSave = getStoredRootEntity();
        SampleNewFragment fragment = (SampleNewFragment) getActiveFragment();

        if (sampleToSave.getReportingUser() == null) {
            sampleToSave.setReportingUser(ConfigProvider.getUser());
        }
        if (sampleToSave.getReportDateTime() == null) {
            sampleToSave.setReportDateTime(new Date());
        }

        fragment.setLiveValidationDisabled(false);

        try {
            FragmentValidator.validate(getContext(), fragment.getContentBinding());
        } catch (ValidationException e) {
            NotificationHelper.showNotification(this, ERROR, e.getMessage());
            return;
        }

        saveTask = new SavingAsyncTask(getRootView(), sampleToSave) {

            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException, ValidationException {
                DatabaseHelper.getSampleDao().saveAndSnapshot(sampleToSave);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }
}
