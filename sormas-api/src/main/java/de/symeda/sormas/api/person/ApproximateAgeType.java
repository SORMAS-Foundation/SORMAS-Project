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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.person;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper.Pair;

public enum ApproximateAgeType {

	YEARS,
	MONTHS,
	DAYS;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static final class ApproximateAgeHelper {

		private ApproximateAgeHelper() {
			// Hide Utility Class Constructor
		}

		public static Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate, Date deathDate) {

			if (birthDate == null) {
				return Pair.createPair(null, ApproximateAgeType.YEARS);
			}

			DateTime toDate = deathDate == null ? DateTime.now() : new DateTime(deathDate);
			DateTime startDate = new DateTime(birthDate);
			Years years = Years.yearsBetween(startDate, toDate);

			if (years.getYears() < 1) {
				Months months = Months.monthsBetween(startDate, toDate);
				if (months.getMonths() < 1) {
					Days days = Days.daysBetween(startDate, toDate);
					return Pair.createPair(days.getDays(), ApproximateAgeType.DAYS);
				} else {
					return Pair.createPair(months.getMonths(), ApproximateAgeType.MONTHS);
				}
			} else {
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

		public static Pair<Integer, ApproximateAgeType> getApproximateAge(Date birthDate) {
			return getApproximateAge(birthDate, null);
		}

		public static Pair<Integer, ApproximateAgeType> getApproximateAge(
			Integer birthdateYYYY,
			Integer birthdateMM,
			Integer birthdateDD,
			Date deathDate) {

			Calendar birthdate = new GregorianCalendar();
			birthdate.set(birthdateYYYY, birthdateMM != null ? birthdateMM - 1 : 0, birthdateDD != null ? birthdateDD : 1);

			return getApproximateAge(birthdate.getTime(), deathDate);
		}

		public static String formatApproximateAge(Integer approximateAge, ApproximateAgeType approximateAgeType) {

			if (approximateAge == null) {
				return "";
			} else if (approximateAgeType != null) {
				switch (approximateAgeType) {
				case YEARS:
					return approximateAge.toString();
				case MONTHS:
				case DAYS:
					return approximateAge + " " + approximateAgeType.toString();
				default:
					throw new IllegalArgumentException(approximateAgeType.toString());
				}
			} else {
				return approximateAge.toString();
			}
		}

		public static Integer getAgeYears(Integer age, ApproximateAgeType approximateAgeType) {

			if (age == null || approximateAgeType == null) {
				return age;
			}

			switch (approximateAgeType) {
			case YEARS:
				return age;
			case MONTHS:
				return Math.floorDiv(age, 12);
			case DAYS:
				return Math.floorDiv(age, 365);
			default:
				throw new IllegalArgumentException(approximateAgeType.toString());
			}
		}

		public static String getAgeGroupFromAge(Integer age, ApproximateAgeType ageType) {
			Integer ageYears = ApproximateAgeHelper.getAgeYears(age, ageType);
			if (ageYears == null) {
				return null;
			}

			int lowerAgeBoundary = (int) Math.floor(ageYears / 5f) * 5;
			if (lowerAgeBoundary >= 120) {
				return "120+";
			} else {
				return lowerAgeBoundary + "--" + (lowerAgeBoundary + 4);
			}
		}
	}
}
