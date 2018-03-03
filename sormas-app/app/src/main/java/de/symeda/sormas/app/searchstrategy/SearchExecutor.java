package de.symeda.sormas.app.searchstrategy;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.core.AsyncTaskResult;
import de.symeda.sormas.app.core.BoolResult;

/**
 * Created by Orson on 03/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SearchExecutor<ADO extends AbstractDomainObject> implements ISearchExecutor<ADO> {
    private ISearchStrategy<ADO> s;
    private AsyncTask<Void, List<ADO>, AsyncTaskResult<List<ADO>>> task;

    public SearchExecutor(ISearchStrategy<ADO> s){
        this.s = s;
    }

    @Override
    public AsyncTask search(ISearchResultCallback<ADO> resultCallback) {
        BoolResult resultStatus = BoolResult.FALSE;
        List<ADO> list = new ArrayList<>();

        try {
            if (this.s == null){
                resultCallback.searchResult(list, new BoolResult(false, "No search strategy found"));
                return null;
            }

            task = new ListingSearchTask(this.s, resultCallback);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            resultCallback.searchResult(list, new BoolResult(false, e.getMessage()));
        }

        return task;
    }
}
