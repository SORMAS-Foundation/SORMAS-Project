package de.symeda.sormas.app.core;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

/**
 * Created by Orson on 06/01/2018.
 */

public interface INavigationCapsule<T extends AbstractDomainObject> {
    IStatusElaborator getFilterStatus();

    IStatusElaborator getPageStatus();

    List<IStatusElaborator> getOtherStatus();

    String getRecordUuid();

    T getRecord();

    String getSampleMaterial();

    String getCaseUuid();

    String getTaskUuid();

    String getContactUuid();

    String getSampleUuid();

    Disease getDisease();

    boolean isForVisit();

    boolean isVisitCooperative();

    UserRight getUserRight();
}
