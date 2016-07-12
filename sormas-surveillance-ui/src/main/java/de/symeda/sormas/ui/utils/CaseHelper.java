package de.symeda.sormas.ui.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import de.symeda.sormas.api.DataTransferObject;

public final class CaseHelper {

	public static final String getShortUuid(DataTransferObject domainObject) {
		return getShortUuid(domainObject.getUuid());
	}
	
	public static final String getShortUuid(String uuid) {
		if (uuid == null)
			return null;
		return uuid.substring(0, 6).toUpperCase();
	}
	
	
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
