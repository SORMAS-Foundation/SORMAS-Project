package de.symeda.sormas.app.util;

import android.content.Context;

/**
 * Created by Orson on 11/12/2017.
 */

public class DisplayMetricsHelper {

    public static int dpToPixels(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    public static int dpToPixels(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
