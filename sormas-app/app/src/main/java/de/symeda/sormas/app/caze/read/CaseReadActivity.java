package de.symeda.sormas.app.caze.read;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseSection;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public class CaseReadActivity extends BaseReadActivity<Case> {

    public static final String TAG = CaseReadActivity.class.getSimpleName();

    private BaseReadActivityFragment activeFragment = null;

    @Override
    public CaseClassification getPageStatus() {
        return (CaseClassification)super.getPageStatus();
    }

    @Override
    protected Case getActivityRootData(String recordUuid) {
        return DatabaseHelper.getCaseDao().queryUuidWithEmbedded(recordUuid);
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment(Case activityRootData) {
        if (activeFragment == null) {
            CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());
            activeFragment = CaseReadFragment.newInstance(dataCapsule, activityRootData);
        }
        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_case_menu;
    }

    @Override
    protected BaseReadActivityFragment getReadFragment(LandingPageMenuItem menuItem, Case activityRootData) {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());

        CaseSection section = CaseSection.fromMenuKey(menuItem.getKey());
        switch (section) {

            case CASE_INFO:
                activeFragment = CaseReadFragment.newInstance(dataCapsule, activityRootData);
                break;
            case PERSON_INFO:
                activeFragment = CaseReadPatientInfoFragment.newInstance(dataCapsule, activityRootData);
                break;
            case HOSPITALIZATION:
                activeFragment = CaseReadHospitalizationFragment.newInstance(dataCapsule, activityRootData);
                break;
            case SYMPTOMS:
                activeFragment = CaseReadSymptomsFragment.newInstance(dataCapsule, activityRootData);
                break;
            case EPIDEMIOLOGICAL_DATA:
                activeFragment = CaseReadEpidemiologicalDataFragment.newInstance(dataCapsule, activityRootData);
                break;
            case CONTACTS:
                activeFragment = CaseReadContactListFragment.newInstance(dataCapsule, activityRootData);
                break;
            case SAMPLES:
                activeFragment = CaseReadSampleListFragment.newInstance(dataCapsule, activityRootData);
                break;
            case TASKS:
                activeFragment = CaseReadTaskListFragment.newInstance(dataCapsule, activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
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
    public void goToEditView() {
        if (activeFragment == null)
            return;

        Case record = getStoredActivityRootEntity();

        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(this,
                record.getUuid(), record.getCaseClassification());
        CaseEditActivity.goToActivity(CaseReadActivity.this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, CaseFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, CaseReadActivity.class, dataCapsule);
    }
}
