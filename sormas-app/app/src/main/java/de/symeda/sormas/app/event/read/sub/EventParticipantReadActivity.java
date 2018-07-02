package de.symeda.sormas.app.event.read.sub;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.event.edit.sub.EventParticipantEditActivity;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.MenuOptionsHelper;

public class EventParticipantReadActivity extends BaseReadActivity<EventParticipant> {

    private String recordUuid = null;
    private EventStatus eventStatus = null;
    private EventStatus pageStatus = null;
    private BaseReadActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveFilterStatusState(outState, eventStatus);
        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
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
        eventStatus = (EventStatus) getFilterStatusArg(arguments);
        pageStatus = (EventStatus) getPageStatusArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    protected EventParticipant getActivityRootData(String recordUuid) {
        return DatabaseHelper.getEventParticipantDao().queryUuid(recordUuid);
    }

    @Override
    public BaseReadActivityFragment getActiveReadFragment(EventParticipant activityRootData) {
        if (activeFragment == null) {
            EventParticipantFormNavigationCapsule dataCapsule = new EventParticipantFormNavigationCapsule(this, recordUuid);
            activeFragment = EventParticipantReadFragment.newInstance(dataCapsule, activityRootData);
        }
        return activeFragment;
    }

    @Override
    public void goToEditView() {
        EventParticipantFormNavigationCapsule dataCapsule = new EventParticipantFormNavigationCapsule(this, recordUuid);
        EventParticipantEditActivity.goToActivity(this, dataCapsule);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getEditMenu().setTitle(R.string.action_edit_event);

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
        return R.string.heading_level3_1_event_read_person_involved_info;
    }

    public static void goToActivity(Context fromActivity, EventParticipantFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, EventParticipantReadActivity.class, dataCapsule);
    }

    private EventStatus getEventStatusArg(Bundle arguments) {
        EventStatus e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_FILTER_STATUS)) {
                e = (EventStatus) arguments.getSerializable(ConstantHelper.ARG_FILTER_STATUS);
            }
        }

        return e;
    }

    private String getEventParticipantUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_DATA_UUID)) {
                result = (String) arguments.getSerializable(ConstantHelper.KEY_DATA_UUID);
            }
        }

        return result;
    }
}
