package de.symeda.sormas.app.component;

import android.view.View;

/**
 * Created by Orson on 25/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface OnShowInputErrorListener {
    void onInputErrorShowing(View v, String message, boolean errorState);
    //void onInputErrorShowing(TeboTextInputEditText v, List<String> messages, boolean errorState);
}
