package de.symeda.sormas.app.caze.edit.sub;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditSampleInfoActivity extends BaseEditActivity<Sample> {

    private final int DATA_XML_PAGE_MENU = -1;

    private boolean showStatusFrame;
    private boolean showTitleBar;
    private boolean showPageMenu;

    private String recordUuid = null;
    private String caseUuid = null;
    private ShipmentStatus pageStatus = null;
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
        pageStatus = (ShipmentStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
        caseUuid = getCaseUuidArg(arguments);

        this.showStatusFrame = true;
        this.showTitleBar = true;
        this.showPageMenu = false;
    }

    @Override
    protected Sample getActivityRootData(String recordUuid) {
        Sample sample;
        if (caseUuid != null && !caseUuid.isEmpty()) {
            Case associatedCase = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
            sample = DatabaseHelper.getSampleDao().build(associatedCase);
        } else {
            sample = DatabaseHelper.getSampleDao().queryUuid(recordUuid);
        }

        return sample;
    }

    @Override
    protected Sample getActivityRootDataIfRecordUuidNull() {
        return null;
    }

    @Override
    public BaseEditActivityFragment getActiveEditFragment(Sample activityRootData) throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            SampleFormNavigationCapsule dataCapsule = (SampleFormNavigationCapsule)new SampleFormNavigationCapsule(
                    CaseEditSampleInfoActivity.this, recordUuid, pageStatus).setCaseUuid(caseUuid);
            activeFragment = CaseEditSampleInfoFragment.newInstance(this, dataCapsule, activityRootData);
        }

        return activeFragment;
    }

    @Override
    public boolean showStatusFrame() {
        return showStatusFrame;
    }

    @Override
    public boolean showTitleBar() {
        return showTitleBar;
    }

    @Override
    public boolean showPageMenu() {
        return showPageMenu;
    }

    @Override
    public Enum getPageStatus() {
        return pageStatus;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    protected BaseEditActivityFragment getNextFragment(LandingPageMenuItem menuItem, Sample activityRootData) {
        return null;
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
        return R.string.heading_level4_case_edit;
    }

    private void processActionbarMenu() {
        if (activeFragment == null)
            return;

        if (saveMenu != null)
            saveMenu.setVisible(activeFragment.showSaveAction());

        if (addMenu != null)
            addMenu.setVisible(activeFragment.showAddAction());
    }

    public static void goToActivity(Context fromActivity, SampleFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, CaseEditSampleInfoActivity.class, dataCapsule);
    }
}
