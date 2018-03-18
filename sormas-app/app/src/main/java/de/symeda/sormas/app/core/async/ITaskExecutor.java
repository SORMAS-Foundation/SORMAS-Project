package de.symeda.sormas.app.core.async;

import android.os.AsyncTask;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Orson on 08/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ITaskExecutor<ADO1 extends AbstractDomainObject, ADO2 extends AbstractDomainObject, ADO3 extends AbstractDomainObject> {

    AsyncTask search(ITaskResultCallback resultCallback);
}

