package de.symeda.sormas.app.searchstrategy;

import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ISearchStrategy<ADO extends AbstractDomainObject> {

    List<ADO> search();

}
