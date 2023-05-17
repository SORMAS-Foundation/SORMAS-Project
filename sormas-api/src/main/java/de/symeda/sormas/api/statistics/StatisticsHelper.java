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
package de.symeda.sormas.api.statistics;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.MonthOfYear;
import de.symeda.sormas.api.Quarter;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.Year;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.disease.DiseaseConfigurationFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRoleFacade;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.UtilDate;

public final class StatisticsHelper {

	private StatisticsHelper() {
		// Hide Utility Class Constructor
	}

	public static final String VALUE_UNKNOWN = "VALUE_UNKNOWN";
	public static final String TOTAL = "total";
	public static final String NOT_SPECIFIED = "notSpecified";

	public static StatisticsGroupingKey buildGroupingKey(
		Object attributeValue,
		StatisticsCaseAttribute attribute,
		StatisticsCaseSubAttribute subAttribute,
		Function<Integer, RegionReferenceDto> regionProvider,
		Function<Integer, DistrictReferenceDto> districtProvider,
		Function<Integer, CommunityReferenceDto> communityProvider,
		Function<Integer, FacilityReferenceDto> facilityProvider,
		Function<Integer, UserRoleReferenceDto> userRoleProvider) {

		if (isNullOrUnknown(attributeValue)) {
			return null;
		}

		if (subAttribute != null) {
			switch (subAttribute) {
			case YEAR:
				return new Year((int) attributeValue);
			case QUARTER:
				return new Quarter((int) attributeValue);
			case MONTH:
				return Month.values()[(int) attributeValue - 1];
			case EPI_WEEK:
				return new EpiWeek(null, (int) attributeValue);
			case QUARTER_OF_YEAR:
				String entryAsString = String.valueOf(attributeValue);
				return new QuarterOfYear(
					new Quarter(Integer.valueOf(entryAsString.substring(entryAsString.length() - 1))),
					new Year(Integer.valueOf(entryAsString.substring(0, entryAsString.length() - 1))));
			case MONTH_OF_YEAR:
				entryAsString = String.valueOf(attributeValue);
				return new MonthOfYear(
					Month.values()[Integer.valueOf(entryAsString.substring(entryAsString.length() - 2)) - 1],
					new Year(Integer.valueOf(entryAsString.substring(0, entryAsString.length() - 2))));
			case EPI_WEEK_OF_YEAR:
				entryAsString = String.valueOf(attributeValue);
				return new EpiWeek(
					Integer.valueOf(entryAsString.substring(0, entryAsString.length() - 2)),
					Integer.valueOf(entryAsString.substring(entryAsString.length() - 2)));
			case REGION:
				return regionProvider.apply(((Number) attributeValue).intValue());
			case DISTRICT:
				return districtProvider.apply(((Number) attributeValue).intValue());
			case COMMUNITY:
				return communityProvider.apply(((Number) attributeValue).intValue());
			case FACILITY:
				return facilityProvider.apply(((Number) attributeValue).intValue());
			default:
				throw new IllegalArgumentException(subAttribute.toString());
			}
		} else {
			switch (attribute) {
			case DISEASE:
				return Disease.valueOf(attributeValue.toString());
			case SEX:
				return Sex.valueOf(attributeValue.toString());
			case CLASSIFICATION:
				return CaseClassification.valueOf(attributeValue.toString());
			case OUTCOME:
				return CaseOutcome.valueOf(attributeValue.toString());
			case AGE_INTERVAL_1_YEAR:
			case AGE_INTERVAL_5_YEARS:
			case AGE_INTERVAL_CHILDREN_COARSE:
			case AGE_INTERVAL_CHILDREN_FINE:
			case AGE_INTERVAL_CHILDREN_MEDIUM:
			case AGE_INTERVAL_BASIC:
				String entryAsString = attributeValue.toString();
				if (attribute == StatisticsCaseAttribute.AGE_INTERVAL_5_YEARS) {
					try {
						AgeGroup ageGroup = AgeGroup.valueOf(entryAsString);
						return ageGroup.toIntegerRange();
					} catch (IllegalArgumentException e) {
						// This is fine; continue to build the IntegerGroup based on the entry string
					}
				}

				if (entryAsString.contains("-")) {
					return new IntegerRange(
						Integer.valueOf(entryAsString.substring(0, entryAsString.indexOf("-"))),
						Integer.valueOf(entryAsString.substring(entryAsString.indexOf("-") + 1)));
				} else if (entryAsString.contains("+")) {
					return new IntegerRange(Integer.valueOf(entryAsString.substring(0, entryAsString.indexOf("+"))), null);
				} else {
					return new IntegerRange(Integer.valueOf(entryAsString), Integer.valueOf(entryAsString));
				}
			case REPORTING_USER_ROLE:
				return userRoleProvider.apply(((Number) attributeValue).intValue());
			default:
				throw new IllegalArgumentException(attribute.toString());
			}
		}
	}

	public static List<StatisticsGroupingKey> getAgeIntervalGroupingKeys(StatisticsCaseAttribute attribute) {

		List<StatisticsGroupingKey> ageIntervalList = new ArrayList<>();
		switch (attribute) {
		case AGE_INTERVAL_1_YEAR:
			for (int i = 0; i < 80; i++) {
				ageIntervalList.add(new IntegerRange(i, i));
			}
			break;
		case AGE_INTERVAL_5_YEARS:
			for (int i = 0; i < 80; i += 5) {
				ageIntervalList.add(new IntegerRange(i, i + 4));
			}
			break;
		case AGE_INTERVAL_CHILDREN_COARSE:
			ageIntervalList.add(new IntegerRange(0, 14));
			for (int i = 15; i < 30; i += 5) {
				ageIntervalList.add(new IntegerRange(i, i + 4));
			}
			for (int i = 30; i < 80; i += 10) {
				ageIntervalList.add(new IntegerRange(i, i + 9));
			}
			break;
		case AGE_INTERVAL_CHILDREN_FINE:
			for (int i = 0; i < 5; i++) {
				ageIntervalList.add(new IntegerRange(i, i));
			}
			for (int i = 5; i < 30; i += 5) {
				ageIntervalList.add(new IntegerRange(i, i + 4));
			}
			for (int i = 30; i < 80; i += 10) {
				ageIntervalList.add(new IntegerRange(i, i + 9));
			}
			break;
		case AGE_INTERVAL_CHILDREN_MEDIUM:
			for (int i = 0; i < 30; i += 5) {
				ageIntervalList.add(new IntegerRange(i, i + 4));
			}
			for (int i = 30; i < 80; i += 10) {
				ageIntervalList.add(new IntegerRange(i, i + 9));
			}
			break;
		case AGE_INTERVAL_BASIC:
			ageIntervalList.add(new IntegerRange(0, 0));
			ageIntervalList.add(new IntegerRange(1, 4));
			ageIntervalList.add(new IntegerRange(5, 14));
			ageIntervalList.add(new IntegerRange(15, null));
			break;
		default:
			throw new IllegalArgumentException(attribute.toString());
		}

		if (attribute != StatisticsCaseAttribute.AGE_INTERVAL_BASIC) {
			ageIntervalList.add(new IntegerRange(80, null));
		}
		ageIntervalList.add(new IntegerRange(null, null));
		return ageIntervalList;
	}

	public static List<StatisticsGroupingKey> getTimeGroupingKeys(
		StatisticsCaseAttribute attribute,
		StatisticsCaseSubAttribute subAttribute,
		CaseFacade caseFacade) {

		Date oldestCaseDate = null;
		switch (attribute) {
		case ONSET_TIME:
			oldestCaseDate = caseFacade.getOldestCaseOnsetDate();
			break;
		case REPORT_TIME:
			oldestCaseDate = caseFacade.getOldestCaseReportDate();
			break;
		case OUTCOME_TIME:
			oldestCaseDate = caseFacade.getOldestCaseOutcomeDate();
			break;
		default:
			return new ArrayList<>();
		}

		return getTimeGroupingKeys(subAttribute, oldestCaseDate, new Date());
	}

	/**
	 *
	 * @param subAttribute
	 *            Needed for StatisticsCaseAttribute.ONSET_TIME, REPORT_TIME, OUTCOME_TIME
	 * @return
	 */
	public static List<StatisticsGroupingKey> getTimeGroupingKeys(StatisticsCaseSubAttribute subAttribute, Date dateFrom, Date dateTo) {

		if (dateFrom == null && dateTo == null) {
			return new ArrayList<>();
		}

		LocalDate earliest = dateFrom == null
			? new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
			: dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate latest = dateTo == null
			? new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
			: dateTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		switch (subAttribute) {
		case YEAR:
			List<StatisticsGroupingKey> years = new ArrayList<>();
			for (int i = earliest.getYear(); i <= latest.getYear(); i++) {
				years.add(new Year(i));
			}
			return years;
		case QUARTER:
			List<StatisticsGroupingKey> quarters = new ArrayList<>();
			for (int i = 1; i <= 4; i++) {
				quarters.add(new Quarter(i));
			}
			return quarters;
		case MONTH:
			return toGroupingKeys(Month.values());
		case EPI_WEEK:
			List<StatisticsGroupingKey> epiWeeks = new ArrayList<>();
			for (int i = 1; i <= DateHelper.getMaximumEpiWeekNumber(); i++) {
				epiWeeks.add(new EpiWeek(null, i));
			}
			return epiWeeks;
		case QUARTER_OF_YEAR:
			List<StatisticsGroupingKey> quarterOfYearList = new ArrayList<>();
			QuarterOfYear earliestQuarter = new QuarterOfYear(new Quarter(earliest.get(IsoFields.QUARTER_OF_YEAR)), new Year(earliest.getYear()));
			QuarterOfYear latestQuarter = new QuarterOfYear(new Quarter(latest.get(IsoFields.QUARTER_OF_YEAR)), new Year(latest.getYear()));
			while (earliestQuarter.compareTo(latestQuarter) <= 0) {
				quarterOfYearList.add(earliestQuarter);
				earliestQuarter = earliestQuarter.createNextQuarter();
			}
			return quarterOfYearList;
		case MONTH_OF_YEAR:
			List<StatisticsGroupingKey> monthOfYearList = new ArrayList<>();
			for (int year = earliest.getYear(); year <= latest.getYear(); year++) {
				for (Month month : Month.values()) {
					if ((earliest.getYear() == year
						&& earliest.getMonth().getValue() <= month.getMonthNumber()
						&& (latest.getYear() == year ? latest.getMonth().getValue() >= month.getMonthNumber() : true))
						|| (latest.getYear() == year
							&& latest.getMonth().getValue() >= month.getMonthNumber()
							&& (earliest.getYear() == year ? earliest.getMonth().getValue() <= month.getMonthNumber() : true))
						|| (year > earliest.getYear() && year < latest.getYear())) {
						monthOfYearList.add(new MonthOfYear(month, year));
					}
				}
			}
			return monthOfYearList;
		case EPI_WEEK_OF_YEAR:
			List<StatisticsGroupingKey> epiWeekOfYearList = new ArrayList<>();
			for (int year = earliest.getYear(); year <= latest.getYear(); year++) {
				epiWeekOfYearList.addAll(
					DateHelper
						.createEpiWeekListFromInterval(DateHelper.getEpiWeek(UtilDate.from(earliest)), DateHelper.getEpiWeek(UtilDate.from(latest))));
			}
			return epiWeekOfYearList;
		default:
			return new ArrayList<>();
		}
	}

	/**
	 *
	 * @param attribute
	 * @param subAttribute
	 * @param diseaseConfigurationFacade
	 *            Needed for StatisticsCaseAttribute.DISEASE
	 * @param caseFacade
	 *            Needed for StatisticsCaseAttribute.ONSET_TIME, REPORT_TIME, OUTCOME_TIME
	 * @param regionFacade
	 *            Needed for StatisticsCaseSubAttribute.REGION
	 * @param districtFacade
	 *            Needed for StatisticsCaseSubAttribute.DISTRICT
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<StatisticsGroupingKey> getAttributeGroupingKeys(
		StatisticsCaseAttribute attribute,
		StatisticsCaseSubAttribute subAttribute,
		DiseaseConfigurationFacade diseaseConfigurationFacade,
		CaseFacade caseFacade,
		RegionFacade regionFacade,
		DistrictFacade districtFacade,
		UserRoleFacade userRoleFacade,
		Date dateFrom,
		Date dateTo) {

		if (subAttribute != null) {
			switch (subAttribute) {
			case YEAR:
			case QUARTER:
			case MONTH:
			case EPI_WEEK:
			case QUARTER_OF_YEAR:
			case MONTH_OF_YEAR:
			case EPI_WEEK_OF_YEAR:
				return StatisticsHelper.getTimeGroupingKeys(subAttribute, dateFrom, dateTo);
			case REGION:
				return (List<StatisticsGroupingKey>) (List<? extends StatisticsGroupingKey>) regionFacade.getAllActiveByServerCountry();
			case DISTRICT:
				return (List<StatisticsGroupingKey>) (List<? extends StatisticsGroupingKey>) districtFacade.getAllActiveAsReference();
			case COMMUNITY:
			case FACILITY:
				return new ArrayList<>();
			default:
				throw new IllegalArgumentException(subAttribute.toString());
			}
		} else {
			switch (attribute) {
			case SEX:
				return toGroupingKeys(Sex.values());
			case DISEASE:
				return toGroupingKeys(diseaseConfigurationFacade.getAllDiseases(true, true, true));
			case CLASSIFICATION:
				return toGroupingKeys(CaseClassification.values());
			case OUTCOME:
				return toGroupingKeys(CaseOutcome.values());
			case AGE_INTERVAL_1_YEAR:
			case AGE_INTERVAL_5_YEARS:
			case AGE_INTERVAL_CHILDREN_COARSE:
			case AGE_INTERVAL_CHILDREN_FINE:
			case AGE_INTERVAL_CHILDREN_MEDIUM:
			case AGE_INTERVAL_BASIC:
				return StatisticsHelper.getAgeIntervalGroupingKeys(attribute);
			case REPORTING_USER_ROLE:
				return (List<StatisticsGroupingKey>) (List<? extends StatisticsGroupingKey>) userRoleFacade.getAllAsReference();
			default:
				throw new IllegalArgumentException(attribute.toString());
			}
		}
	}

	/**
	 * Converts the given values to a {@link List} of {@link StatisticsGroupingKey}s.
	 */
	private static <K extends StatisticsGroupingKey> List<StatisticsGroupingKey> toGroupingKeys(K[] keys) {

		List<StatisticsGroupingKey> keyList = new ArrayList<>();
		for (K ur : keys) {
			keyList.add(ur);
		}
		return keyList;
	}

	/**
	 * Converts the given values to a {@link List} of {@link StatisticsGroupingKey}s.
	 */
	private static <K extends StatisticsGroupingKey> List<StatisticsGroupingKey> toGroupingKeys(Collection<K> keys) {

		List<StatisticsGroupingKey> keyList = new ArrayList<>();
		keyList.addAll(keys);
		return keyList;
	}

	public static boolean isNullOrUnknown(Object value) {

		if (value == null) {
			return true;
		}
		if (value instanceof IntegerRange && ((IntegerRange) value).getFrom() == null && ((IntegerRange) value).getTo() == null) {
			return true;
		}

		return isUnknown(value);
	}

	public static boolean isUnknown(Object value) {
		return value.toString().equalsIgnoreCase(VALUE_UNKNOWN);
	}

	public static class StatisticsKeyComparator implements Comparator<StatisticsGroupingKey> {

		public int compare(StatisticsGroupingKey a, StatisticsGroupingKey b) {
			if (a == null && b == null) {
				return 0;
			}

			if (a == null && b != null) {
				return -1;
			}

			if (b == null && a != null) {
				return 1;
			}

			return a.keyCompareTo(b);
		}
	}
}
