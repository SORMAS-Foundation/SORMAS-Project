package de.symeda.sormas.app.caze.list;

import android.content.Context;

import de.symeda.sormas.app.core.BaseListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;

import de.symeda.sormas.api.caze.InvestigationStatus;

/**
 * Created by Orson on 09/01/2018.
 */

public class CaseListCapsule extends BaseListNavigationCapsule {

    public CaseListCapsule(Context context, InvestigationStatus filterStatus, SearchBy searchBy) {
        super(context, filterStatus, searchBy);
    }
}