package de.symeda.sormas.app.event;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum EventSection implements StatusElaborator {

    EVENT_INFO(R.string.caption_event_information, R.drawable.ic_alert_24dp),
    EVENT_PARTICIPANTS(R.string.caption_event_participants, R.drawable.ic_group_black_24dp),
    TASKS(R.string.caption_event_tasks, R.drawable.ic_drawer_user_task_blue_24dp);

    private int friendlyNameResourceId;
    private int iconResourceId;

    EventSection(int friendlyNameResourceId, int iconResourceId) {
        this.friendlyNameResourceId = friendlyNameResourceId;
        this.iconResourceId = iconResourceId;
    }

    public static EventSection fromOrdinal(int ordinal) {
        return EventSection.values()[ordinal];
    }

    @Override
    public String getFriendlyName(Context context) {
        return context.getResources().getString(friendlyNameResourceId);
    }

    @Override
    public int getColorIndicatorResource() {
        return 0;
    }

    @Override
    public Enum getValue() {
        return this;
    }

    @Override
    public int getIconResourceId() {
        return iconResourceId;
    }
}
