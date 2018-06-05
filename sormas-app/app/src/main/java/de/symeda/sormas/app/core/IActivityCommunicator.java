package de.symeda.sormas.app.core;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ProgressBar;

import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.Callback;

/**
 * Created by Orson on 09/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface IActivityCommunicator {
    Context getContext();
    ProgressBar getPreloader();
    void showPreloader();
    void hidePreloader();
    boolean isFirstRun();

    void showFragmentView();
    void hideFragmentView();

    void synchronizeCompleteData();
    void synchronizeChangedData();
    void synchronizeChangedData(Callback callback);
    void synchronizeData(final SynchronizeDataAsync.SyncMode syncMode, final boolean showResultSnackbar, final boolean showProgressDialog, boolean showUpgradePrompt, final SwipeRefreshLayout swipeRefreshLayout, final Callback callback);

}
