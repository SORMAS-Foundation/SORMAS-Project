package de.symeda.sormas.app.shared;

import android.content.Context;

import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;

/**
 * Created by Orson on 07/01/2018.
 */

public class ContactFormFollowUpNavigationCapsule extends BaseFormNavigationCapsule<Contact, ContactFormFollowUpNavigationCapsule> {

    public ContactFormFollowUpNavigationCapsule(Context context, String recordUuid, VisitStatus pageStatus) {
        super(context, recordUuid, pageStatus);
    }

}
