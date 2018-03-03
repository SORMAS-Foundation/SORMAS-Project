package de.symeda.sormas.app.core;

import android.content.Context;

import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;

/**
 * Created by Orson on 09/01/2018.
 */

public class BaseListNavigationCapsule implements IListNavigationCapsule {


    private IStatusElaborator filterStatus;
    private SearchBy searchBy;


    public BaseListNavigationCapsule(Context context, Enum filterStatus, SearchBy searchBy) {
        if (filterStatus != null)
            this.filterStatus = StatusElaboratorFactory.getElaborator(context, filterStatus);

        this.searchBy = searchBy;
    }

    @Override
    public IStatusElaborator getFilterStatus() {
        return filterStatus;
    }

    @Override
    public SearchBy getSearchStrategy() {
        return searchBy;
    }

}
