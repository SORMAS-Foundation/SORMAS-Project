package de.symeda.sormas.app.contact;

import android.content.Context;

import de.symeda.sormas.app.core.BaseNavigationCapsule;

import de.symeda.sormas.api.visit.VisitStatus;

/**
 * Created by Orson on 07/01/2018.
 */

public class ContactFormFollowUpNavigationCapsule extends BaseNavigationCapsule {

    public ContactFormFollowUpNavigationCapsule(Context context, String recordUuid, VisitStatus pageStatus) {
        super(context, recordUuid, pageStatus);
    }

}
