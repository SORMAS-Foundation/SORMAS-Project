package de.symeda.sormas.app.visit.read;

import android.content.Context;
import android.os.Bundle;

import java.util.List;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.symptoms.SymptomsReadFragment;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.visit.VisitSection;
import de.symeda.sormas.app.visit.edit.VisitEditActivity;

public class VisitReadActivity extends BaseReadActivity<Visit> {

    public static final String TAG = VisitReadActivity.class.getSimpleName();

    private String contactUuid = null;

    public static void startActivity(Context context, String rootUuid, String contactUuid, VisitSection section) {
        BaseReadActivity.startActivity(context, VisitReadActivity.class, buildBundle(rootUuid, contactUuid, section));
    }

    public static Bundler buildBundle(String rootUuid, String contactUuid, VisitSection section) {
        return buildBundle(rootUuid, section).setContactUuid(contactUuid);
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
    public List<PageMenuItem> getPageMenuData() {
        return PageMenuItem.fromEnum(VisitSection.values(), getContext());
    }

    @Override
    protected Visit queryRootData(String recordUuid) {
        return DatabaseHelper.getVisitDao().queryUuid(recordUuid);
    }

    @Override
    public VisitStatus getPageStatus() {
        return getStoredRootEntity() == null ? null : getStoredRootEntity().getVisitStatus();
    }

    @Override
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Visit activityRootData) {
        VisitSection section = VisitSection.fromOrdinal(menuItem.getKey());
        BaseReadFragment fragment;
        switch (section) {
            case VISIT_INFO:
                fragment = VisitReadFragment.newInstance(activityRootData);
                break;
            case SYMPTOMS:
                fragment = SymptomsReadFragment.newInstance(activityRootData);
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

    @Override
    public void goToEditView() {
        VisitSection section = VisitSection.fromOrdinal(getActivePage().getKey());
        VisitEditActivity.startActivity(getContext(), getRootUuid(), contactUuid, section);
    }
}
