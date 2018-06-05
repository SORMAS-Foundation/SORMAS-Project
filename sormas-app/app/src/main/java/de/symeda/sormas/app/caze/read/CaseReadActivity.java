package de.symeda.sormas.app.caze.read;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 06/01/2018.
 */

public class CaseReadActivity  extends BaseReadActivity<Case> {

    public static final String TAG = CaseReadActivity.class.getSimpleName();

    private AsyncTask jobTask;
    private static final int MENU_INDEX_CASE_INFO = 0;
    private static final int MENU_INDEX_PATIENT_INFO = 1;
    private static final int MENU_INDEX_HOSPITALIZATION = 2;
    private static final int MENU_INDEX_SYMPTOMS = 3;
    private static final int MENU_INDEX_EPIDEMIOLOGICAL_DATA = 4;
    private static final int MENU_INDEX_CONTACTS = 5;
    private static final int MENU_INDEX_SAMPLES = 6;
    private static final int MENU_INDEX_TASKS = 7;

    private final int DATA_XML_PAGE_MENU = R.xml.data_form_page_case_menu; // "xml/data_read_page_case_menu.xml";

    //private InvestigationStatus filterStatus = null;
    private CaseClassification pageStatus = null;
    private String recordUuid = null;
    private BaseReadActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SaveFilterStatusState(outState, filterStatus);
        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
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
        //filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        pageStatus = (CaseClassification) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    protected Case getActivityRootData(String recordUuid) {
        return DatabaseHelper.getCaseDao().queryUuidWithEmbedded(recordUuid);
    }

    @Override
    protected Case getActivityRootDataIfRecordUuidNull() {
        return DatabaseHelper.getCaseDao().build(DatabaseHelper.getPersonDao().build());
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment(Case activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(
                    CaseReadActivity.this, recordUuid).setReadPageStatus(pageStatus);
            activeFragment = CaseReadFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseReadActivityFragment getNextFragment(LandingPageMenuItem menuItem, Case activityRootData) {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(
                CaseReadActivity.this, recordUuid).setReadPageStatus(pageStatus);

        try {
            if (menuItem.getKey() == MENU_INDEX_CASE_INFO) {
                activeFragment = CaseReadFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_PATIENT_INFO) {
                activeFragment = CaseReadPatientInfoFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_HOSPITALIZATION) {
                activeFragment = CaseReadHospitalizationFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_SYMPTOMS) {
                activeFragment = CaseReadSymptomsFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_EPIDEMIOLOGICAL_DATA) {
                activeFragment = CaseReadEpidemiologicalDataFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_CONTACTS) {
                activeFragment = CaseReadContactListFragment.newInstance(this, dataCapsule, activityRootData);
            }else if (menuItem.getKey() == MENU_INDEX_SAMPLES) {
                activeFragment = CaseReadSampleListFragment.newInstance(this, dataCapsule, activityRootData);
            } else if (menuItem.getKey() == MENU_INDEX_TASKS) {
                activeFragment = CaseReadTaskListFragment.newInstance(this, dataCapsule, activityRootData);
            }

            //processActionbarMenu();
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        }

        return activeFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_case);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleReadModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_case_read;
    }

    @Override
    public void gotoEditView() {
        if (activeFragment == null)
            return;

        Case record = getStoredActivityRootData();
        //Case record = itemIterator.next();

        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(CaseReadActivity.this,
                record.getUuid()).setEditPageStatus(record.getInvestigationStatus());
        CaseEditActivity.goToActivity(CaseReadActivity.this, dataCapsule);

        /*try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    showPreloader();
                    hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    Case record = DatabaseHelper.getCaseDao().queryUuid(recordUuid);

                    if (record == null) {
                        // build a new event for empty uuid
                        resultHolder.forItem().add(DatabaseHelper.getCaseDao().build());
                    } else {
                        resultHolder.forItem().add(record);
                    }
                }
            });
            jobTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    hidePreloader();
                    showFragmentView();

                    if (resultHolder == null)
                        return;

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
                    if (itemIterator.hasNext()) {
                        Case record = itemIterator.next();

                        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(CaseReadActivity.this,
                                record.getUuid()).setEditPageStatus(record.getInvestigationStatus());
                        CaseEditActivity.goToActivity(CaseReadActivity.this, dataCapsule);
                    }
                }
            });
        } catch (Exception ex) {
            hidePreloader();
            showFragmentView();
        }*/
    }

    public static void goToActivity(Context fromActivity, CaseFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, CaseReadActivity.class, dataCapsule);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (jobTask != null && !jobTask.isCancelled())
            jobTask.cancel(true);
    }
}
