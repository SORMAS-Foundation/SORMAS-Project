package de.symeda.sormas.app.core;

import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

import java.util.List;

/**
 * Created by Orson on 09/01/2018.
 */

public interface IListToReadNavigationCapsule {
    IStatusElaborator getFilterStatus();

    IStatusElaborator getPageStatus();

    List<IStatusElaborator> getOtherStatus();

    String getRecordUuid();
}
