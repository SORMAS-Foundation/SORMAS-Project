package de.symeda.sormas.app.contact.list;

import android.content.Context;

import de.symeda.sormas.app.core.BaseListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;

import de.symeda.sormas.api.contact.FollowUpStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class ContactListCapsule extends BaseListNavigationCapsule {

    public ContactListCapsule(Context context, FollowUpStatus filterStatus, SearchBy searchBy) {
        super(context, filterStatus, searchBy);
    }
}