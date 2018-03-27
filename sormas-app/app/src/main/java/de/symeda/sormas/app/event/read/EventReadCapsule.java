package de.symeda.sormas.app.event.read;

import android.content.Context;

import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;

/**
 * Created by Orson on 09/01/2018.
 */

public class EventReadCapsule extends BaseFormNavigationCapsule<Event, EventReadCapsule> {

    public EventReadCapsule(Context context, String recordUuid, EventType pageStatus) {
        super(context, recordUuid, pageStatus);
    }
}
