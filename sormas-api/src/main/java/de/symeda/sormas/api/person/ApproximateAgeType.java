/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.person;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper.Pair;

public enum ApproximateAgeType {
	YEARS,
	MONTHS
	;
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
	
	public static final class ApproximateAgeHelper {
		public static final Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate, Date deathDate) {
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
	        
//	 		Same code for Java8		
//			if (birthDate == null)
//				return Pair.createPair(null, ApproximateAgeType.YEARS);
//			
//			LocalDate toDate = deathDate==null?LocalDate.now():Instant.ofEpochMilli(deathDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
//			LocalDate birthdate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			Period period = Period.between(birthdate, toDate);
//			
//			if(period.getYears()<1) {
//				return Pair.createPair(period.getMonths(), ApproximateAgeType.MONTHS);
//			}
//			else {
//				return Pair.createPair(period.getYears(), ApproximateAgeType.YEARS);
//			}
		}
		
		public static final Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate) {
			return getApproximateAge(birthDate, null);
		}
		
		public static String formatApproximateAge(Integer approximateAge, ApproximateAgeType approximateAgeType) {
			if (approximateAge == null) {
				return "";
			} else if (approximateAgeType == ApproximateAgeType.MONTHS) {
				return approximateAge + " " + approximateAgeType.toString();
			} else {
				return approximateAge.toString();
			}
		}
	}
}


