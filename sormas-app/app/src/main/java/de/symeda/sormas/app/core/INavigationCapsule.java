package de.symeda.sormas.app.core;

import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Orson on 06/01/2018.
 */

public interface INavigationCapsule<T extends AbstractDomainObject> {
    IStatusElaborator getFilterStatus();

    IStatusElaborator getPageStatus();

    List<IStatusElaborator> getOtherStatus();

    String getRecordUuid();

    T getRecord();
}
