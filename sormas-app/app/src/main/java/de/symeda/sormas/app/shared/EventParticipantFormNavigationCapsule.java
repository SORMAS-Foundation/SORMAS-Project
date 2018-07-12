package de.symeda.sormas.app.shared;

import android.content.Context;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

public class EventParticipantFormNavigationCapsule extends BaseFormNavigationCapsule<Event, EventParticipantFormNavigationCapsule> {

    public EventParticipantFormNavigationCapsule(Context context, String recordUuid) {
        super(context, recordUuid, null);
    }
}