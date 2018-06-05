package de.symeda.sormas.app.shared;

import android.content.Context;

import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

/**
 * Created by Orson on 09/01/2018.
 */

public class ContactFormNavigationCapsule extends BaseFormNavigationCapsule<Contact, ContactFormNavigationCapsule> {

    private IStatusElaborator filterStatus;
    private SearchBy searchBy;

    public ContactFormNavigationCapsule(Context context, ContactClassification pageStatus) {
        super(context, null, pageStatus);
    }

    public ContactFormNavigationCapsule(Context context, String recordUuid, ContactClassification pageStatus) {
        super(context, recordUuid, pageStatus);
    }
}