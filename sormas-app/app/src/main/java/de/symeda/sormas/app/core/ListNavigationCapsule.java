package de.symeda.sormas.app.core;

import android.content.Context;

import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.core.enumeration.StatusElaboratorFactory;

/**
 * Created by Orson on 07/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class ListNavigationCapsule implements IListNavigationCapsule {

    private Context context;
    private Enum filterStatus;
    private Enum pageStatus;
    private SearchBy searchBy;
    private int activeMenuKey;

    public ListNavigationCapsule(Context context, Enum filterStatus, SearchBy searchBy) {
        this.context = context;
        this.filterStatus = filterStatus;
        this.searchBy = searchBy;
    }

    @Override
    public IStatusElaborator getFilterStatus() {
        if (filterStatus != null)
            return StatusElaboratorFactory.getElaborator(context, filterStatus);

        return null;
    }

    @Override
    public IStatusElaborator getPageStatus() {
        if (pageStatus != null)
            return StatusElaboratorFactory.getElaborator(context, pageStatus);

        return null;
    }

    @Override
    public SearchBy getSearchStrategy() {
        return searchBy;
    }

    @Override
    public int getActiveMenuKey() {
        return activeMenuKey;
    }

    public ListNavigationCapsule setFilterStatus(Enum filterStatus) {
        this.filterStatus = filterStatus;
        return this;
    }

    public ListNavigationCapsule setPageStatus(Enum pageStatus) {
        this.pageStatus = pageStatus;
        return this;
    }

    public ListNavigationCapsule setSearchBy(SearchBy searchBy) {
        this.searchBy = searchBy;
        return this;
    }

    public ListNavigationCapsule setActiveMenuKey(int activeMenuKey) {
        this.activeMenuKey = activeMenuKey;
        return this;
    }
}
