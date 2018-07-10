package de.symeda.sormas.app.visit.read;

import android.content.Context;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.visit.VisitSection;
import de.symeda.sormas.app.visit.edit.VisitEditActivity;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;

public class VisitReadActivity extends BaseReadActivity<Visit> {

    public static final String TAG = VisitReadActivity.class.getSimpleName();

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_followup_menu;
    }

    @Override
    protected Visit queryRootData(String recordUuid) {
        return DatabaseHelper.getVisitDao().queryUuid(recordUuid);
    }

    @Override
    public VisitStatus getPageStatus() {
        return (VisitStatus) super.getPageStatus();
    }

    @Override
    protected BaseReadFragment buildReadFragment(LandingPageMenuItem menuItem, Visit activityRootData) {
        VisitFormNavigationCapsule dataCapsule = new VisitFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());

        VisitSection section = VisitSection.fromMenuKey(menuItem.getKey());
        BaseReadFragment fragment;
        switch (section) {
            case VISIT_INFO:
                fragment = VisitReadFragment.newInstance(dataCapsule, activityRootData);
                break;
            case SYMPTOMS:
                fragment = VisitReadSymptomsFragment.newInstance(dataCapsule, activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }
        return fragment;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_1_contact_visit_info;
    }

    public static void goToActivity(Context fromActivity, VisitFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, VisitReadActivity.class, dataCapsule);
    }

    @Override
    public void goToEditView() {
        VisitFormNavigationCapsule dataCapsule = new VisitFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());
        VisitEditActivity.goToActivity(VisitReadActivity.this, dataCapsule);
    }
}
