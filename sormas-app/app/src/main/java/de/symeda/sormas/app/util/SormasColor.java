package de.symeda.sormas.app.util;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Size;

import java.util.HashMap;

/**
 * Created by Orson on 06/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SormasColor {

    @ColorInt public static final int BLACK       = 0xFF000000;
    @ColorInt public static final int TRANSPARENT = 0;
    @ColorInt public static final int BLUE        = 0xFF005A9C;
    @ColorInt public static final int LTBLUE      = 0xFF6691C4;
    @ColorInt public static final int LTERBLUE    = 0xFFCDD8EC;
    @ColorInt public static final int LTESTBLUE   = 0xFFE1E7F0;
    @ColorInt public static final int CTRL_NORMAL = LTERBLUE;
    @ColorInt public static final int CTRL_FOCUS  = BLUE;
    @ColorInt public static final int TEXT_NORMAL               = 0xFF374b5a;
    @ColorInt public static final int SWITCH_CHECKED_BG         = LTERBLUE;
    @ColorInt public static final int SWITCH_UNCHECKED_BG       = TRANSPARENT;
    @ColorInt public static final int SWITCH_CHECKED_TEXT       = BLUE;
    @ColorInt public static final int SWITCH_UNCHECKED_TEXT     = TEXT_NORMAL;

    private static final HashMap<String, Integer> sColorNameMap;

    static {
        sColorNameMap = new HashMap<String, Integer>();
        sColorNameMap.put("black", BLACK);
        sColorNameMap.put("transparent", TRANSPARENT);
        sColorNameMap.put("blue", BLUE);
        sColorNameMap.put("light_blue", LTBLUE);
        sColorNameMap.put("lighter_blue", LTERBLUE);
        sColorNameMap.put("lightest_blue", LTESTBLUE);

    }

    public static int alpha(int color) {
        return Color.alpha(color);
    }

    public static int red(int color) {
        return Color.red(color);
    }

    public static int green(int color) {
        return Color.green(color);
    }

    public static int blue(int color) {
        return Color.blue(color);
    }

    @ColorInt
    public static int rgb(int red, int green, int blue) {
        return Color.rgb(red, green, blue);
    }

    @ColorInt
    public static int argb(int alpha, int red, int green, int blue) {
        return Color.argb(alpha, red, green, blue);
    }

    public static float luminance(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Color.luminance(color);
        }

        throw new UnsupportedOperationException("SormasColor: luminance");
    }

    @ColorInt
    public static int parseColor(@Size(min=1) String colorString) {
        return Color.parseColor(colorString);
    }


    public static void RGBToHSV(int red, int green, int blue, @Size(3) float hsv[]) {
        Color.RGBToHSV(red, green, blue, hsv);
    }

    public static void colorToHSV(@ColorInt int color, @Size(3) float hsv[]) {
        RGBToHSV((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, hsv);
    }

    public static int HSVToColor(@Size(3) float hsv[]) {
        return HSVToColor(0xFF, hsv);
    }


    public static int HSVToColor(int alpha, @Size(3) float hsv[]) {
        return Color.HSVToColor(alpha, hsv);
    }
}