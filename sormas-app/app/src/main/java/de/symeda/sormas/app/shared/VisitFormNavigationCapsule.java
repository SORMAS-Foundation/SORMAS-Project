package de.symeda.sormas.app.shared;

import android.content.Context;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;

public class VisitFormNavigationCapsule extends BaseFormNavigationCapsule<Contact, VisitFormNavigationCapsule> {

    public VisitFormNavigationCapsule(Context context, String recordUuid, VisitStatus pageStatus) {
        super(context, recordUuid, pageStatus);
    }

}
