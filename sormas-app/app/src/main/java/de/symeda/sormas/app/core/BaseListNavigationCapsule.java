package de.symeda.sormas.app.core;

import android.content.Context;

import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;

/**
 * Created by Orson on 09/01/2018.
 */

public class BaseListNavigationCapsule implements IListNavigationCapsule {


    private IStatusElaborator filterStatus;
    private SearchStrategy searchStrategy;


    public BaseListNavigationCapsule(Context context, Enum filterStatus, SearchStrategy searchStrategy) {
        if (filterStatus != null)
            this.filterStatus = StatusElaboratorFactory.getElaborator(context, filterStatus);

        this.searchStrategy = searchStrategy;
    }

    @Override
    public IStatusElaborator getFilterStatus() {
        return filterStatus;
    }

    @Override
    public SearchStrategy getSearchStrategy() {
        return searchStrategy;
    }

}
