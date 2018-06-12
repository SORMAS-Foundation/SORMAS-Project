package de.symeda.sormas.app.searchstrategy;

import android.os.AsyncTask;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ISearchExecutor<ADO extends AbstractDomainObject> {

    AsyncTask search(ISearchResultCallback<ADO> resultCallback);
}
