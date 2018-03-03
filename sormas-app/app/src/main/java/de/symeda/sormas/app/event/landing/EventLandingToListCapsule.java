package de.symeda.sormas.app.event.landing;

import android.content.Context;

import de.symeda.sormas.app.core.BaseLandingToListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;

import de.symeda.sormas.api.event.EventStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class EventLandingToListCapsule extends BaseLandingToListNavigationCapsule {

    public EventLandingToListCapsule(Context context, EventStatus filterStatus, SearchBy searchBy) {
        super(context, filterStatus, searchBy);
    }
}