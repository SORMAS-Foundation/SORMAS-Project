package de.symeda.sormas.app.searchstrategy;

import android.os.AsyncTask;

import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.BoolResult;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ListingSearchTask<ADO extends AbstractDomainObject> extends AsyncTask<Void, Void, AsyncTaskResult<List<ADO>>> {

    private ISearchResultCallback<ADO> callback;
    private ISearchStrategy<ADO> searchStrategy;

    public ListingSearchTask(ISearchStrategy<ADO> searchStrategy, ISearchResultCallback<ADO> callback) {
        this.searchStrategy = searchStrategy;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.preExecute();
    }

    @Override
    protected AsyncTaskResult<List<ADO>> doInBackground(Void... voids) {
        BoolResult resultStatus = BoolResult.TRUE;
        try {
            return new AsyncTaskResult<List<ADO>>(resultStatus, searchStrategy.search());
        } catch (Exception e) {
            return new AsyncTaskResult<List<ADO>>(resultStatus, e);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<ADO>> result) {
        BoolResult resultStatus = result.getResultStatus();
        List<ADO> list = result.getResult();

        if (resultStatus == BoolResult.FALSE) {
            callback.searchResult(list, resultStatus);
        } else if (result.getError() != null) {
            callback.searchResult(list, new BoolResult(false, result.getError().getMessage()));
        }  else if ( isCancelled()) {
            callback.searchResult(list, new BoolResult(false, "Listing search has been cancelled"));
        } else {
            if (list != null)
                resultStatus = BoolResult.TRUE;

            callback.searchResult(list, resultStatus);
        }
    }

}
