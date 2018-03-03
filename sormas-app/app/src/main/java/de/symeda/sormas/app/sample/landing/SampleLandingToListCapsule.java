package de.symeda.sormas.app.sample.landing;

import android.content.Context;

import de.symeda.sormas.app.core.BaseLandingToListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.sample.ShipmentStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class SampleLandingToListCapsule extends BaseLandingToListNavigationCapsule {

    public SampleLandingToListCapsule(Context context, ShipmentStatus filterStatus, SearchBy searchBy) {
        super(context, filterStatus, searchBy);
    }
}