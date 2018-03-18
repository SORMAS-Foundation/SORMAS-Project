package de.symeda.sormas.app.core.async;

import de.symeda.sormas.app.core.BoolResult;

/**
 * Created by Orson on 08/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ITaskResultCallback {

    void searchResult(BoolResult resultStatus, TaskResultHolder resultHolder);
}