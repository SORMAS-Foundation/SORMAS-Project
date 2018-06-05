package de.symeda.sormas.app.core;

import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

/**
 * Created by Orson on 09/01/2018.
 */

public interface IListNavigationCapsule {
    IStatusElaborator getFilterStatus();

    IStatusElaborator getPageStatus();

    SearchBy getSearchStrategy();

    int getActiveMenuKey();
}
