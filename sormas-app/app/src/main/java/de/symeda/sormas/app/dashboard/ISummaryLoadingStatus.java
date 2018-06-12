package de.symeda.sormas.app.dashboard;

import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;

/**
 * Created by Orson on 09/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public interface ISummaryLoadingStatus {

    void onAllSummaryLoadingCompleted(Callback.IAction<BoolResult> callback);
    void notifyActivitySummaryLoadingCompleted();
}
