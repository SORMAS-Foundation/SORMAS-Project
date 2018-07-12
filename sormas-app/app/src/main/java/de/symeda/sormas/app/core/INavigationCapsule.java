package de.symeda.sormas.app.core;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;

public interface INavigationCapsule<T extends AbstractDomainObject> {
    IStatusElaborator getFilterStatus();

    IStatusElaborator getPageStatus();

    String getRecordUuid();

    T getRecord();

    int getActiveMenuKey();

    String getPersonUuid();

    String getCaseUuid();

    String getEventUuid();

    String getTaskUuid();

    String getContactUuid();

    String getSampleUuid();

    Disease getDisease();

    boolean isForVisit();

    boolean isVisitCooperative();

    UserRight getUserRight();
}
