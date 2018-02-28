package de.symeda.sormas.app.contact.list;

import android.content.Context;

import de.symeda.sormas.app.core.BaseListNavigationCapsule;
import de.symeda.sormas.app.core.SearchStrategy;

import de.symeda.sormas.api.contact.FollowUpStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class ContactListCapsule extends BaseListNavigationCapsule {

    public ContactListCapsule(Context context, FollowUpStatus filterStatus, SearchStrategy searchStrategy) {
        super(context, filterStatus, searchStrategy);
    }
}