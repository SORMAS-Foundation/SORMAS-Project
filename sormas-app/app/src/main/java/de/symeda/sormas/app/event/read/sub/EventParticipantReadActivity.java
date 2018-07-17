package de.symeda.sormas.app.event.read.sub;

import android.content.Context;

import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.event.edit.sub.EventParticipantEditActivity;
import de.symeda.sormas.app.shared.EventParticipantFormNavigationCapsule;

public class EventParticipantReadActivity extends BaseReadActivity<EventParticipant> {

    @Override
    protected EventParticipant queryRootData(String recordUuid) {
        return DatabaseHelper.getEventParticipantDao().queryUuid(recordUuid);
    }

    @Override
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, EventParticipant activityRootData) {
        EventParticipantFormNavigationCapsule dataCapsule = new EventParticipantFormNavigationCapsule(this, getRootEntityUuid());
        return EventParticipantReadFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    public void goToEditView() {
        EventParticipantFormNavigationCapsule dataCapsule = new EventParticipantFormNavigationCapsule(this, getRootEntityUuid());
        EventParticipantEditActivity.goToActivity(this, dataCapsule);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_1_event_read_person_involved_info;
    }

    public static void goToActivity(Context fromActivity, EventParticipantFormNavigationCapsule dataCapsule) {
        BaseReadActivity.goToActivity(fromActivity, EventParticipantReadActivity.class, dataCapsule);
    }
}
