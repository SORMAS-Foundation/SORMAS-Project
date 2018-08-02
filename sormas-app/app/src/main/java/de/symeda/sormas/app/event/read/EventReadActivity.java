package de.symeda.sormas.app.event.read;

import android.content.Context;
import android.view.Menu;

import java.util.List;

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

public class EventReadActivity extends BaseReadActivity<Event> {

    public static final String TAG = EventReadActivity.class.getSimpleName();

    public static void startActivity(Context context, String rootUuid, boolean finishInsteadOfUpNav) {
        BaseReadActivity.startActivity(context, EventReadActivity.class, buildBundle(rootUuid, finishInsteadOfUpNav));
    }

    @Override
    protected Event queryRootData(String recordUuid) {
        return DatabaseHelper.getEventDao().queryUuid(recordUuid);
    }

    @Override
    public EventStatus getPageStatus() {
        return getStoredRootEntity() == null ? null : getStoredRootEntity().getEventStatus();
    }

    @Override
    public List<PageMenuItem> getPageMenuData() {
        return PageMenuItem.fromEnum(EventSection.values(), getContext());
    }

    @Override
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, Event activityRootData) {
        EventSection section = EventSection.fromOrdinal(menuItem.getKey());
        BaseReadFragment fragment;
        switch (section) {
            case EVENT_INFO:
                fragment = EventReadFragment.newInstance(activityRootData);
                break;
            case EVENT_PARTICIPANTS:
                fragment = EventReadPersonsInvolvedListFragment.newInstance(activityRootData);
                break;
            case TASKS:
                fragment = EventReadTaskListFragement.newInstance(activityRootData);
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
    public void goToEditView() {
        EventSection section = EventSection.fromOrdinal(getActivePage().getKey());
        EventEditActivity.startActivity(this, getRootUuid(), section);
    }
}

