package de.symeda.sormas.app.contact;

import android.content.Context;

import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

import de.symeda.sormas.api.contact.ContactClassification;

/**
 * Created by Orson on 09/01/2018.
 */

public class ContactFormNavigationCapsule extends BaseFormNavigationCapsule {

    private IStatusElaborator filterStatus;
    private SearchStrategy searchStrategy;

    public ContactFormNavigationCapsule(Context context, String recordUuid, ContactClassification pageStatus) {
        super(context, recordUuid, pageStatus);
    }
}