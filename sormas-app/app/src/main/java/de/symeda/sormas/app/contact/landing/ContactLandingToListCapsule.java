package de.symeda.sormas.app.contact.landing;

import android.content.Context;

import de.symeda.sormas.app.core.BaseLandingToListNavigationCapsule;
import de.symeda.sormas.app.core.SearchStrategy;

import de.symeda.sormas.api.contact.FollowUpStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class ContactLandingToListCapsule extends BaseLandingToListNavigationCapsule {

    public ContactLandingToListCapsule(Context context, FollowUpStatus filterStatus, SearchStrategy searchStrategy) {
        super(context, filterStatus, searchStrategy);
    }
}