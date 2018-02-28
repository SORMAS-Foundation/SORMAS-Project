package de.symeda.sormas.app.event;

import android.content.Context;

import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

import de.symeda.sormas.api.event.EventStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class EventFormNavigationCapsule extends BaseFormNavigationCapsule {

    private IStatusElaborator filterStatus;
    private SearchStrategy searchStrategy;

    public EventFormNavigationCapsule(Context context, String recordUuid, EventStatus pageStatus) {
        super(context, recordUuid, pageStatus);
    }
}