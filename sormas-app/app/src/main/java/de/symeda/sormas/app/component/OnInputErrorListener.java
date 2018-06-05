package de.symeda.sormas.app.component;

import android.view.View;

/**
 * Created by Orson on 23/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface OnInputErrorListener {
    void onInputErrorChange(View v, String message, boolean errorState);
}
