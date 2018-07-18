package de.symeda.sormas.app.shared;

import android.content.Context;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

public class EventFormNavigationCapsule extends BaseFormNavigationCapsule<Event, EventFormNavigationCapsule> {

    public EventFormNavigationCapsule(Context context, EventStatus pageStatus) {
        super(context, null, pageStatus);
    }

    public EventFormNavigationCapsule(Context context, String recordUuid, EventStatus pageStatus) {
        super(context, recordUuid, pageStatus);
    }
}