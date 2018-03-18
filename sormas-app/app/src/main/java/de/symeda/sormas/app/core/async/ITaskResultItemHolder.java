package de.symeda.sormas.app.core.async;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Orson on 10/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ITaskResultItemHolder {

    <ADO extends AbstractDomainObject> void add(ADO value);

    public ITaskResultHolderIterator iterator();
}
