package de.symeda.sormas.app.component;

import android.view.View;

/**
 * Created by Orson on 31/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public interface ITeboDatePicker {

    void setInputType(int type);
    void setOnClickListener(View.OnClickListener listener);
    void setOnFocusChangeListener(View.OnFocusChangeListener listener);

}
