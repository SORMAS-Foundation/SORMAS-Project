package de.symeda.sormas.app.component;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Orson on 25/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ShowKeyboardOnEditTextClickHandler implements View.OnClickListener {

    private Context context;

    public ShowKeyboardOnEditTextClickHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (v.hasFocus()) {
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
