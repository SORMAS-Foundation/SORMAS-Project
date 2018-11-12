package de.symeda.sormas.app.event.read.eventparticipant;

import android.content.Context;
import android.os.Bundle;

import de.symeda.sormas.app.BaseReadActivity;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.component.menu.PageMenuItem;
import de.symeda.sormas.app.event.edit.eventparticipant.EventParticipantEditActivity;
import de.symeda.sormas.app.util.Bundler;

public class EventParticipantReadActivity extends BaseReadActivity<EventParticipant> {

    private String eventUuid;

    public static void startActivity(Context context, String rootUuid, String eventUuid) {
        BaseReadActivity.startActivity(context, EventParticipantReadActivity.class, buildBundle(rootUuid, eventUuid));
    }

    public static Bundler buildBundle(String rootUuid, String eventUuid) {
        return buildBundle(rootUuid, 0).setEventUuid(eventUuid);
    }

    @Override
    protected void onCreateInner(Bundle savedInstanceState) {
        super.onCreateInner(savedInstanceState);
        eventUuid = new Bundler(savedInstanceState).getEventUuid();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        new Bundler(outState).setEventUuid(eventUuid);
    }

    @Override
    protected EventParticipant queryRootEntity(String recordUuid) {
        return DatabaseHelper.getEventParticipantDao().queryUuid(recordUuid);
    }

    @Override
    protected BaseReadFragment buildReadFragment(PageMenuItem menuItem, EventParticipant activityRootData) {
        return EventParticipantReadFragment.newInstance(activityRootData);
    }

    @Override
    public void goToEditView() {
        EventParticipantEditActivity.startActivity(this, getRootUuid(), eventUuid);
    }

    @Override
    public Enum getPageStatus() {
        return null;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level3_1_event_read_person_involved_info;
    }
}
