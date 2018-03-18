package de.symeda.sormas.app.core;

import android.content.Context;
import android.widget.ProgressBar;

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
}
