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
package de.symeda.sormas.api.statistics;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.MonthOfYear;
import de.symeda.sormas.api.Quarter;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.Year;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;

public final class StatisticsHelper {

	private StatisticsHelper() {
		// Hide Utility Class Constructor
	}

	public static final String VALUE_UNKNOWN = "VALUE_UNKNOWN";
	public static final String TOTAL = "total";
	public static final String UNKNOWN = "unknown";
	
	public static StatisticsGroupingKey buildGroupingKey(Object attributeValue, StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute, Function<Integer, RegionReferenceDto> regionProvider, Function<Integer, DistrictReferenceDto> districtProvider) {
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
				return new QuarterOfYear(new Quarter(Integer.valueOf(entryAsString.substring(entryAsString.length() - 1))), new Year(Integer.valueOf(entryAsString.substring(0, entryAsString.length() - 1))));
			case MONTH_OF_YEAR:
				entryAsString = String.valueOf(attributeValue);
				return new MonthOfYear(Month.values()[Integer.valueOf(entryAsString.substring(entryAsString.length() - 2)) - 1], new Year(Integer.valueOf(entryAsString.substring(0, entryAsString.length() - 2))));
			case EPI_WEEK_OF_YEAR:
				entryAsString = String.valueOf(attributeValue);
				return new EpiWeek(Integer.valueOf(entryAsString.substring(0, entryAsString.length() - 2)), Integer.valueOf(entryAsString.substring(entryAsString.length() - 2)));
			case REGION:
				return regionProvider.apply(((Number) attributeValue).intValue());
			case DISTRICT:
				return districtProvider.apply(((Number) attributeValue).intValue());
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
					return new IntegerRange(Integer.valueOf(entryAsString.substring(0, entryAsString.indexOf("-"))), Integer.valueOf(entryAsString.substring(entryAsString.indexOf("-") + 1)));
				} else if (entryAsString.contains("+")) {
					return new IntegerRange(Integer.valueOf(entryAsString.substring(0, entryAsString.indexOf("+"))), null);
				} else {
					return new IntegerRange(Integer.valueOf(entryAsString), Integer.valueOf(entryAsString));
				}
			case REPORTING_USER_ROLE:
				return UserRole.valueOf(attributeValue.toString());
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

	public static List<StatisticsGroupingKey> getTimeGroupingKeys(StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
		Date oldestCaseDate = null;
		switch (attribute) {
		case ONSET_TIME:
			oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseOnsetDate();
			break;
		case REPORT_TIME:
			oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseReportDate();
			break;
		default:
			return new ArrayList<>();
		}

		LocalDate earliest = oldestCaseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate now = LocalDate.now();

		switch (subAttribute) {
		case YEAR:
			List<StatisticsGroupingKey> years = new ArrayList<>();
			for (int i = earliest.getYear(); i <= now.getYear(); i++) {
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
			List<StatisticsGroupingKey> months = new ArrayList<>();
			for (Month month : Month.values()) {
				months.add(month);
			}
			return months;
		case EPI_WEEK:
			List<StatisticsGroupingKey> epiWeeks = new ArrayList<>();
			for (int i = 1; i <= DateHelper.getMaximumEpiWeekNumber(); i++) {
				epiWeeks.add(new EpiWeek(null, i));
			}
			return epiWeeks;
		case QUARTER_OF_YEAR:
			List<StatisticsGroupingKey> quarterOfYearList = new ArrayList<>();
			QuarterOfYear earliestQuarter = new QuarterOfYear(new Quarter(1), new Year(earliest.getYear()));
			QuarterOfYear latestQuarter = new QuarterOfYear(new Quarter(4), new Year(now.getYear()));
			while (earliestQuarter.getYear().getValue() <= latestQuarter.getYear().getValue()) {
				quarterOfYearList.add(new QuarterOfYear(earliestQuarter.getQuarter(), earliestQuarter.getYear()));
				earliestQuarter.increaseQuarter();
			}
			return quarterOfYearList;
		case MONTH_OF_YEAR:
			List<StatisticsGroupingKey> monthOfYearList = new ArrayList<>();
			for (int year = earliest.getYear(); year <= now.getYear(); year++) {
				for (Month month : Month.values()) {
					monthOfYearList.add(new MonthOfYear(month, year));
				}
			}
			return monthOfYearList;
		case EPI_WEEK_OF_YEAR:
			List<StatisticsGroupingKey> epiWeekOfYearList = new ArrayList<>();
			for (int year = earliest.getYear(); year <= now.getYear(); year++) {
				epiWeekOfYearList.addAll(DateHelper.createEpiWeekList(year));
			}
			return epiWeekOfYearList;
		default:
			return new ArrayList<>();
		}
	}	
	
	@SuppressWarnings("unchecked")
	public static List<StatisticsGroupingKey> getAttributeGroupingKeys(StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
		if (subAttribute != null) {
			switch (subAttribute) {
			case YEAR:
			case QUARTER:
			case MONTH:
			case EPI_WEEK:
			case QUARTER_OF_YEAR:
			case MONTH_OF_YEAR:
			case EPI_WEEK_OF_YEAR:
				return StatisticsHelper.getTimeGroupingKeys(attribute, subAttribute);
			case REGION:
				return (List<StatisticsGroupingKey>)(List<? extends StatisticsGroupingKey>)FacadeProvider.getRegionFacade().getAllActiveAsReference();
			case DISTRICT:
				return (List<StatisticsGroupingKey>)(List<? extends StatisticsGroupingKey>)FacadeProvider.getDistrictFacade().getAllActiveAsReference();
			default:
				throw new IllegalArgumentException(subAttribute.toString());
			}
		} else {
			switch (attribute) {
			case SEX:
				ArrayList<StatisticsGroupingKey> sexList = new ArrayList<>();
				for (Sex sex : Sex.values()) {
					sexList.add(sex);
				}
				return sexList;
			case DISEASE:
				ArrayList<StatisticsGroupingKey> diseaseList = new ArrayList<>();
				for (Disease disease : FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true)) {
					diseaseList.add(disease);
				}
				return diseaseList;
			case CLASSIFICATION:
				ArrayList<StatisticsGroupingKey> classificationList = new ArrayList<>();
				for (CaseClassification classification : CaseClassification.values()) {
					classificationList.add(classification);
				}
				return classificationList;
			case OUTCOME:
				ArrayList<StatisticsGroupingKey> outcomeList = new ArrayList<>();
				for (CaseOutcome outcome : CaseOutcome.values()) {
					outcomeList.add(outcome);
				}
				return outcomeList;
			case AGE_INTERVAL_1_YEAR:
			case AGE_INTERVAL_5_YEARS:
			case AGE_INTERVAL_CHILDREN_COARSE:
			case AGE_INTERVAL_CHILDREN_FINE:
			case AGE_INTERVAL_CHILDREN_MEDIUM:
			case AGE_INTERVAL_BASIC:
				return StatisticsHelper.getAgeIntervalGroupingKeys(attribute);
			default:
				throw new IllegalArgumentException(attribute.toString());
			}
		}
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
