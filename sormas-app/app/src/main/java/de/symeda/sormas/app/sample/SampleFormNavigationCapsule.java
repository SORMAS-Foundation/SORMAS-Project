package de.symeda.sormas.app.sample;

import android.content.Context;

import de.symeda.sormas.app.core.BaseFormNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

/**
 * Created by Orson on 09/01/2018.
 */

public class SampleFormNavigationCapsule extends BaseFormNavigationCapsule {

    private IStatusElaborator filterStatus;
    private SearchBy searchBy;

    public SampleFormNavigationCapsule(Context context, String recordUuid, ShipmentStatus pageStatus) {
        super(context, recordUuid, pageStatus);
    }
}