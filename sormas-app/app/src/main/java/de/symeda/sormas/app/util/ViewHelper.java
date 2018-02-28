package de.symeda.sormas.app.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Orson on 30/12/2017.
 */

public class ViewHelper {

    public static View inflateView(Context context, int layout) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(layout, null);
    }
}
