package de.symeda.sormas.app.sample.landing;

import android.content.Context;

import de.symeda.sormas.app.core.BaseLandingToListNavigationCapsule;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.sample.ShipmentStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class SampleLandingToListCapsule extends BaseLandingToListNavigationCapsule {

    public SampleLandingToListCapsule(Context context, ShipmentStatus filterStatus, SearchStrategy searchStrategy) {
        super(context, filterStatus, searchStrategy);
    }
}