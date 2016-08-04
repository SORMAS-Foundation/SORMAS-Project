package de.symeda.sormas.app.util;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Years;

import java.util.Date;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.utils.DataHelper.Pair;

/**
 * Created by Stefan Szczesny on 03.08.2016.
 */
public class DateUtils {

    /**
     * JodaTime based
     * @param birthDate
     * @param deathDate
     * @return
     */
    public static final Pair<Integer, ApproximateAgeType> getApproximateAgeYears(Date birthDate, Date deathDate) {
        if (birthDate == null)
            return Pair.createPair(null, ApproximateAgeType.YEARS);

        DateTime toDate = deathDate==null?DateTime.now(): new DateTime(deathDate);
        DateTime startDate = new DateTime(birthDate);
        Years years = Years.yearsBetween(startDate, toDate);

        if(years.getYears()<1) {
            Months months = Months.monthsBetween(startDate, toDate);
            return Pair.createPair(months.getMonths(), ApproximateAgeType.MONTHS);
        }
        else {
            return Pair.createPair(years.getYears(), ApproximateAgeType.YEARS);
        }
    }
}
