package de.symeda.sormas.api.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public final class DateHelper {

	public static final String getApproximateAge(Date birthDate, Date deathDate) {
		if (birthDate == null)
			return null;
		
		LocalDate toDate = deathDate==null?LocalDate.now():deathDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate birthdate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Period period = Period.between(birthdate, toDate);
		
		if(period.getYears()<1) {
			return period.getMonths() + " Months";
		}
		else {
			return period.getYears() + " Years";
		}
	}
	
	public static final String getApproximateAge(Date birthDate) {
		return getApproximateAge(birthDate, null);
	}
}
