package de.symeda.sormas.app.core;

import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

import java.util.List;

/**
 * Created by Orson on 12/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IEditToEditNavigationCapsule extends INavigationCapsule {
    IStatusElaborator getFilterStatus();

    IStatusElaborator getPageStatus();

    List<IStatusElaborator> getOtherStatus();

    String getRecordUuid();
}
