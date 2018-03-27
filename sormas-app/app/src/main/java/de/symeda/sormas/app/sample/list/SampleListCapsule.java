package de.symeda.sormas.app.sample.list;

import android.content.Context;

import de.symeda.sormas.app.core.BaseListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.shared.ShipmentStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class SampleListCapsule extends BaseListNavigationCapsule {

    public SampleListCapsule(Context context, ShipmentStatus filterStatus, SearchBy searchBy) {
        super(context, filterStatus, searchBy);
    }
}