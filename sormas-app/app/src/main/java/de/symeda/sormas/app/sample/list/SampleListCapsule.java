package de.symeda.sormas.app.sample.list;

import android.content.Context;

import de.symeda.sormas.app.core.BaseListNavigationCapsule;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.sample.ShipmentStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class SampleListCapsule extends BaseListNavigationCapsule {

    public SampleListCapsule(Context context, ShipmentStatus filterStatus, SearchStrategy searchStrategy) {
        super(context, filterStatus, searchStrategy);
    }
}