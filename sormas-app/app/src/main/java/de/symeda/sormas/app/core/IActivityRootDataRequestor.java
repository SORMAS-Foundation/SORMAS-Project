package de.symeda.sormas.app.core;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Orson on 29/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IActivityRootDataRequestor<TActivityRootData extends AbstractDomainObject> {

    void requestActivityRootData(final Callback.IAction<TActivityRootData> callback);
}
