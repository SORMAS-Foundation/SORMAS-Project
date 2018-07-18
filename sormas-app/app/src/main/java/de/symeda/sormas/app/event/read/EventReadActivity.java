package de.symeda.sormas.app.event.read;

import android.content.Context;
import android.view.Menu;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.event.EventSection;
import de.symeda.sormas.app.event.edit.EventEditActivity;
import de.symeda.sormas.app.shared.EventFormNavigationCapsule;

public class EventReadActivity extends BaseReadActivity<Event> {

    public static final String TAG = EventReadActivity.class.getSimpleName();

    @Override
    protected Event queryRootData(String recordUuid) {
        return DatabaseHelper.getEventDao().queryUuid(recordUuid);
    }

    @Override
    public EventStatus getPageStatus() {
        return (EventStatus) super.getPageStatus();
    }

    @Override
    public int getPageMenuData() {
        return R.xml.data_form_page_alert_menu;
    }

    @Override
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Event activityRootData) {
        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());

        EventSection section = EventSection.fromMenuKey(menuItem.getKey());
        BaseReadFragment fragment;
        switch (section) {
            case EVENT_INFO:
                fragment = EventReadFragment.newInstance(dataCapsule, activityRootData);
                break;
            case EVENT_PERSONS:
                fragment = EventReadPersonsInvolvedListFragment.newInstance(dataCapsule, activityRootData);
                break;
            case TASKS:
                fragment = EventReadTaskListFragement.newInstance(dataCapsule, activityRootData);
                break;
            default:
                throw new IndexOutOfBoundsException(DataHelper.toStringNullable(section));
        }

        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_event);
        return result;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_event_read;
    }

    @Override
    public void goToEditView(PageMenuItem menuItem) {
        EventFormNavigationCapsule dataCapsule = new EventFormNavigationCapsule(this, getRootEntityUuid(), getPageStatus());
        if (menuItem != null) dataCapsule.setActiveMenu(menuItem.getKey());
        EventEditActivity.goToActivity(this, dataCapsule);
    }

    public static void goToActivity(Context fromActivity, EventFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, EventReadActivity.class, dataCapsule);
    }
}

