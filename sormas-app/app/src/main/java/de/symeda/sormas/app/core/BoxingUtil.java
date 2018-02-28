package de.symeda.sormas.app.core;

import android.databinding.InverseMethod;

/**
 * Created by Orson on 28/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class BoxingUtil {

    @InverseMethod("boxBoolean")
    public static boolean safeUnbox(Boolean boxed) {
        return boxed == null ? false : (boolean)boxed;
    }

    public static Boolean boxBoolean(boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }

    @InverseMethod("boxInteger")
    public static int safeUnbox(Integer boxed) {
        return boxed == null ? 0 : (int)boxed;
    }

    public static Integer boxInteger(int b) {
        return Integer.valueOf(b);
    }

    @InverseMethod("boxLong")
    public static long safeUnbox(Long boxed) {
        return boxed == null ? 0L : (long)boxed;
    }

    public static Long boxLong(long b) {
        return Long.valueOf(b);
    }

    @InverseMethod("boxShort")
    public static short safeUnbox(Short boxed) {
        return boxed == null ? 0 : (short)boxed;
    }

    public static Short boxShort(short b) {
        return Short.valueOf(b);
    }

    @InverseMethod("boxByte")
    public static byte safeUnbox(Byte boxed) {
        return boxed == null ? 0 : (byte)boxed;
    }

    public static Byte boxByte(byte b) {
        return Byte.valueOf(b);
    }

    @InverseMethod("boxCharacter")
    public static char safeUnbox(Character boxed) {
        return boxed == null ? '\u0000' : (char)boxed;
    }

    public static Character boxCharacter(char b) {
        return Character.valueOf(b);
    }

    @InverseMethod("boxDouble")
    public static double safeUnbox(Double boxed) {
        return boxed == null ? 0.0 : (double)boxed;
    }

    public static Double boxDouble(double b) {
        return Double.valueOf(b);
    }

    @InverseMethod("boxFloat")
    public static float safeUnbox(Float boxed) {
        return boxed == null ? 0f : (float)boxed;
    }

    public static Float boxFloat(float b) {
        return Float.valueOf(b);
    }
}
