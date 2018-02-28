package de.symeda.sormas.app.util;

import java.util.Date;

/**
 * Created by Orson on 02/01/2018.
 */

public class DateHelper {

    public static long toSeconds(Date date) {
        return date.getTime()/1000;
    }

    public static long toMilliSeconds(Date date) {
        return date.getTime();
    }
}
