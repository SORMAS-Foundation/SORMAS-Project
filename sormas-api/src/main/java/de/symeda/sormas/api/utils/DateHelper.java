package de.symeda.sormas.api.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.utils.DataHelper.Pair;

public final class DateHelper {

	public static final Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate, Date deathDate) {
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
	}
	
	
}
