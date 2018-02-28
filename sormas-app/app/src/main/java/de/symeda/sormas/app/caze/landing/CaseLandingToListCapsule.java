package de.symeda.sormas.app.caze.landing;

import android.content.Context;

import de.symeda.sormas.app.core.BaseLandingToListNavigationCapsule;
import de.symeda.sormas.app.core.SearchStrategy;

import de.symeda.sormas.api.caze.InvestigationStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class CaseLandingToListCapsule extends BaseLandingToListNavigationCapsule {

    public CaseLandingToListCapsule(Context context, InvestigationStatus filterStatus, SearchStrategy searchStrategy) {
        super(context, filterStatus, searchStrategy);
    }
}