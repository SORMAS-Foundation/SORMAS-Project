package de.symeda.sormas.app.shared;

import android.content.Context;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

/**
 * Created by Orson on 09/01/2018.
 */

public class EventFormNavigationCapsule extends BaseFormNavigationCapsule<Event, EventFormNavigationCapsule> {

    private IStatusElaborator filterStatus;
    private SearchBy searchBy;

    public EventFormNavigationCapsule(Context context, EventStatus pageStatus) {
        super(context, null, pageStatus);
    }

    public EventFormNavigationCapsule(Context context, String recordUuid, EventStatus pageStatus) {
        super(context, recordUuid, pageStatus);
    }

    public static class EventFormNavigationCapsule1 extends EventFormNavigationCapsule {

        public EventFormNavigationCapsule1(Context context, EventStatus pageStatus) {
            super(context, pageStatus);
        }
    }
}