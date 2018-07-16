package de.symeda.sormas.app.caze.read;

import android.content.Context;
import android.view.Menu;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.caze.CaseSection;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.person.read.PersonReadFragment;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.symptoms.SymptomsReadFragment;

public class CaseReadActivity extends BaseReadActivity<Case> {

    public static final String TAG = CaseReadActivity.class.getSimpleName();

    @Override
    public CaseClassification getPageStatus() {
        return (CaseClassification) super.getPageStatus();
    }

    @Override
    protected Case queryRootData(String recordUuid) {
        return DatabaseHelper.getCaseDao().queryUuidWithEmbedded(recordUuid);
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_case_menu;
    }

    @Override
    protected BaseReadFragment buildReadFragment(LandingPageMenuItem menuItem, Case activityRootData) {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());

        CaseSection section = CaseSection.fromMenuKey(menuItem.getKey());
        BaseReadFragment fragment;
        switch (section) {

            case CASE_INFO:
                fragment = CaseReadFragment.newInstance(dataCapsule, activityRootData);
                break;
            case PERSON_INFO:
                fragment = PersonReadFragment.newInstance(dataCapsule, activityRootData);
                break;
            case HOSPITALIZATION:
                fragment = CaseReadHospitalizationFragment.newInstance(dataCapsule, activityRootData);
                break;
            case SYMPTOMS:
                fragment = SymptomsReadFragment.newInstance(dataCapsule, activityRootData);
                break;
            case EPIDEMIOLOGICAL_DATA:
                fragment = CaseReadEpidemiologicalDataFragment.newInstance(dataCapsule, activityRootData);
                break;
            case CONTACTS:
                fragment = CaseReadContactListFragment.newInstance(dataCapsule, activityRootData);
                break;
            case SAMPLES:
                fragment = CaseReadSampleListFragment.newInstance(dataCapsule, activityRootData);
                break;
            case TASKS:
                fragment = CaseReadTaskListFragment.newInstance(dataCapsule, activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }

        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_case);
        return result;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_case_read;
    }

    @Override
    public void goToEditView() {
        CaseFormNavigationCapsule dataCapsule = new CaseFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());
        CaseEditActivity.goToActivity(CaseReadActivity.this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, CaseFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, CaseReadActivity.class, dataCapsule);
    }
}
