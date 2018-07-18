package de.symeda.sormas.app.util;

import android.content.Context;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Orson on 29/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class SoftKeyboardHelper {

    public static void hideKeyboard(FragmentActivity activity, Fragment fragment) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && fragment.getView() != null) {
            imm.hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(AppCompatActivity activity, IBinder binder) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binder, 0);
        }
    }

}
