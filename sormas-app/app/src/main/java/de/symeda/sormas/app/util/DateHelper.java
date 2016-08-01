package de.symeda.sormas.app.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Stefan Szczesny on 01.08.2016.
 */
public final class DateHelper {

    private static final SimpleDateFormat dMy_FORMAT = new SimpleDateFormat("dd.MM.yy");
    private static final SimpleDateFormat DDMMYY_FORMAT = new SimpleDateFormat("dd/MM/yy");

    /*public static final Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate, Date deathDate) {
        if (birthDate == null)
            return Pair.createPair(null, ApproximateAgeType.YEARS);

        LocalDate toDate = deathDate==null?LocalDate.now():deathDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate birthdate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(birthdate, toDate);

        if(period.getYears()<1) {
            return Pair.createPair(period.getMonths(), ApproximateAgeType.MONTHS);
        }
        else {
            return Pair.createPair(period.getYears(), ApproximateAgeType.YEARS);
        }
    }

    public static final Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate) {
        return getApproximateAge(birthDate, null);
    }*/

    /**
     * Returns a Date at 0 h, 0 m, 0 s
     * @param day
     * @param month
     * @param year
     * @return
     */
    public static Date getDateZero(int year, int month,int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year,month,day,0,0,0);
        return calendar.getTime();
    }

    /**
     * Formats to "dd.MM.yy"
     * @return
     */
    public static String formatDMY(Date date) {
        if (date != null) {
            return clone(dMy_FORMAT).format(date);
        } else {
            return "";
        }
    }

    public static String formatDDMMYY(Date date) {
        if (date != null) {
            return clone(DDMMYY_FORMAT).format(date);
        } else {
            return "";
        }
    }

    private static SimpleDateFormat clone(SimpleDateFormat sdf) {
        return (SimpleDateFormat) sdf.clone();
    }
}