package de.symeda.sormas.app.sample.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.ISaveable;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.MenuOptionsHelper;

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

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
        saveCaseUuidState(outState, caseUuid);
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
    public BaseEditActivityFragment getActiveEditFragment(Sample activityRootData) {
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

        ISaveable fragment = (ISaveable)activeFragment;

        if (fragment != null)
            fragment.save(this);
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
