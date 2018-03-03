package de.symeda.sormas.app.event.list;

import android.content.Context;

import de.symeda.sormas.app.core.BaseListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;

import de.symeda.sormas.api.event.EventStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class EventListCapsule extends BaseListNavigationCapsule {

    public EventListCapsule(Context context, EventStatus filterStatus, SearchBy searchBy) {
        super(context, filterStatus, searchBy);
    }
}