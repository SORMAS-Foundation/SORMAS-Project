package de.symeda.sormas.app.event;

import android.content.Context;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.enumeration.StatusElaborator;

public enum EventSection implements StatusElaborator {

    EVENT_INFO,
    EVENT_PARTICIPANTS,
    TASKS;

    public static EventSection fromOrdinal(int ordinal) {
        return EventSection.values()[ordinal];
    }

    @Override
    public String getFriendlyName(Context context) {
        switch(this) {
            case EVENT_INFO:
                return context.getResources().getString(R.string.caption_event_information);
            case EVENT_PARTICIPANTS:
                return context.getResources().getString(R.string.caption_event_participants);
            case TASKS:
                return context.getResources().getString(R.string.caption_event_tasks);
            default:
                throw new IllegalArgumentException(this.toString());
        }
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
        return 0;
    }
}
