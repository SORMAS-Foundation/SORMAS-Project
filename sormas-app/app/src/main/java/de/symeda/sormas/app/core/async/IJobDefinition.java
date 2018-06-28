package de.symeda.sormas.app.core.async;

import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.core.BoolResult;

/**
 * Created by Orson on 09/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IJobDefinition {
    void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder);
    void execute(BoolResult resultStatus, TaskResultHolder resultHolder);
}
