package de.symeda.sormas.app.util;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Orson on 29/11/2017.
 */

public class PercentageUtils {

    public static float percentageOf(float portion, List<Float> whole) {
        if (whole == null)
            throw new IllegalArgumentException("The whole list is null.");

        float sum = 0f;
        for (float f: whole) {
            sum = sum + f;
        }



        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return Float.valueOf(df.format((portion/sum) * 100));
    }

    public static float percentageOf(float portion, float[] whole) {
        if (whole == null)
            throw new IllegalArgumentException("The whole list is null.");

        float sum = 0f;
        for (float f: whole) {
            sum = sum + f;
        }



        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return Float.valueOf(df.format((portion/sum) * 100));
    }

    public static float percentageOf(float portion, float sum) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return Float.valueOf(df.format((portion/sum) * 100));
    }


}
