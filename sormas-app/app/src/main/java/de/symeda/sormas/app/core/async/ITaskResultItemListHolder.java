package de.symeda.sormas.app.core.async;

import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Orson on 10/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ITaskResultItemListHolder {

    <ADO extends AbstractDomainObject> void add(List<ADO> value);

    ITaskResultHolderIterator iterator();
}
