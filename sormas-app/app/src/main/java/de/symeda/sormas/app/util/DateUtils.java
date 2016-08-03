package de.symeda.sormas.app.util;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Years;

import java.util.Date;

/**
 * Created by Stefan Szczesny on 03.08.2016.
 */
public class DateUtils {

    public static final float getApproximateAgeYears(Date birthDate, Date deathDate) {
        if (birthDate == null)
            return 0;

        DateTime toDate = deathDate==null?DateTime.now(): new DateTime(deathDate);
        DateTime startDate = new DateTime(birthDate);

        Period period = new Period(startDate, toDate);

        // able to calculate whole months between two dates easily
        Years years = Years.yearsBetween(startDate, toDate);

        if(years.getYears()<1) {
            Months months = Months.monthsBetween(startDate, toDate);
            return (float) months.getMonths()/12;
        }
        else {
            return years.getYears();
        }
    }
}
