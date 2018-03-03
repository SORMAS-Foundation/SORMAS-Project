package de.symeda.sormas.app.contact.landing;

import android.content.Context;

import de.symeda.sormas.app.core.BaseLandingToListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;

import de.symeda.sormas.api.contact.FollowUpStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class ContactLandingToListCapsule extends BaseLandingToListNavigationCapsule {

    public ContactLandingToListCapsule(Context context, FollowUpStatus filterStatus, SearchBy searchBy) {
        super(context, filterStatus, searchBy);
    }
}