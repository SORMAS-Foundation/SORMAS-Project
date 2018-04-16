package de.symeda.sormas.app.sample.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.MenuOptionsHelper;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Orson on 05/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SampleEditActivity extends BaseEditActivity<Sample> {

    private AsyncTask saveTask;
    private LandingPageMenuItem activeMenuItem = null;
    private boolean showStatusFrame = false;
    private boolean showTitleBar = true;
    private boolean showPageMenu = false;
    private final int DATA_XML_PAGE_MENU = -1;

    private ShipmentStatus pageStatus = null;
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
        //filterStatus = (ShipmentStatus) getFilterStatusArg(arguments);
        pageStatus = (ShipmentStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
        caseUuid = getCaseUuidArg(arguments);

        this.activeMenuItem = null;
        this.showStatusFrame = true;
        this.showTitleBar = true;
        this.showPageMenu = false;
    }

    @Override
    protected Sample getActivityRootData(String recordUuid) {
        return DatabaseHelper.getSampleDao().queryUuid(recordUuid);
    }

    @Override
    protected Sample getActivityRootDataIfRecordUuidNull() {
        return null;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment(Sample activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(
                    SampleEditActivity.this, recordUuid, pageStatus).setCaseUuid(caseUuid);
            activeFragment = SampleEditFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_sample);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleEditModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level4_sample_edit;
    }

    @Override
    public void saveData() {
        if (activeFragment == null)
            return;

        final Sample sampleToSave = getStoredActivityRootData();
        //SampleDao sampleDao = DatabaseHelper.getSampleDao();
        //Sample record = (Sample)activeFragment.getPrimaryData();

        if (sampleToSave == null)
            return;

        if (sampleToSave.getReportingUser() == null) {
            sampleToSave.setReportingUser(ConfigProvider.getUser());
        }
        if (sampleToSave.getReportDateTime() == null) {
            sampleToSave.setReportDateTime(new Date());
        }

        //TODO: Validation
        /*FragmentSampleEditLayoutBinding binding = (FragmentSampleEditLayoutBinding)activeFragment.getContentBinding();
        SampleValidator.clearErrorsForSampleData(binding);
        if (!SampleValidator.validateSampleData(record, binding)) {
            return true;
        }*/


        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                private String saveUnsuccessful;

                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();

                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_sample));
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    try {
                        SampleDao sampleDao = DatabaseHelper.getSampleDao();
                        sampleDao.saveAndSnapshot(sampleToSave);
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save sample", e);
                        resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                        ErrorReportingHelper.sendCaughtException(tracker, e, sampleToSave, true);
                    }
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

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification(SampleEditActivity.this, NotificationType.ERROR, resultStatus.getMessage());
                        return;
                    } else {
                        NotificationHelper.showNotification(SampleEditActivity.this, NotificationType.SUCCESS, "Sample " + DataHelper.getShortUuid(sampleToSave.getUuid()) + " saved");
                    }

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.ChangesOnly, SampleEditActivity.this, new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    NotificationHelper.showNotification(SampleEditActivity.this, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_sample)));
                                } else {
                                    NotificationHelper.showNotification(SampleEditActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_sample)));
                                }
                                finish();
                            }
                        });
                    } else {
                        NotificationHelper.showNotification(SampleEditActivity.this, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_sample)));
                        finish();
                    }

                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }
    }

    public static <TActivity extends AbstractSormasActivity> void
    goToActivity(Context fromActivity, SampleFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, SampleEditActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }

}
