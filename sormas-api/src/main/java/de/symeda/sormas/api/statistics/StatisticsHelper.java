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

import org.apache.commons.lang3.EnumUtils;

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
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;

public class StatisticsHelper {

	public static final String UNKNOWN = "unknown";
	public static final String TOTAL = "total";
	public static final String CASE_COUNT = "caseCount";
	
	public static StatisticsGroupingKey buildGroupingKey(Object attributeValue, StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
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
				return new QuarterOfYear(new Quarter(Integer.valueOf(entryAsString.substring(4))), new Year(Integer.valueOf(entryAsString.substring(0, 4))));
			case MONTH_OF_YEAR:
				entryAsString = String.valueOf(attributeValue);
				return new MonthOfYear(Month.values()[Integer.valueOf(entryAsString.substring(4)) - 1], new Year(Integer.valueOf(entryAsString.substring(0, 4))));
			case EPI_WEEK_OF_YEAR:
				entryAsString = String.valueOf(attributeValue);
				return new EpiWeek(Integer.valueOf(entryAsString.substring(0, 4)), Integer.valueOf(entryAsString.substring(4)));
			case REGION:
				return FacadeProvider.getRegionFacade().getRegionReferenceByUuid(attributeValue.toString());
			case DISTRICT:
				return FacadeProvider.getDistrictFacade().getDistrictReferenceByUuid(attributeValue.toString());
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
				if (isNullOrUnknown(attributeValue)) {
					return null;
				}
				
				String entryAsString = attributeValue.toString();
				if (entryAsString.contains("-")) {
					return new IntegerRange(Integer.valueOf(entryAsString.substring(0, entryAsString.indexOf("-"))), Integer.valueOf(entryAsString.substring(entryAsString.indexOf("-") + 1)));
				} else if (entryAsString.contains("+")) {
					return new IntegerRange(Integer.valueOf(entryAsString.substring(0, entryAsString.indexOf("+"))), null);
				} else {
					return new IntegerRange(Integer.valueOf(entryAsString), Integer.valueOf(entryAsString));
				}
			default:
				throw new IllegalArgumentException(attribute.toString());
			}
		}
	}
	
	public static List<Object> getListOfAgeIntervalValues(StatisticsCaseAttribute attribute) {
		List<Object> ageIntervalList = new ArrayList<>();
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

	public static List<Object> getListOfDateValues(StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
		Date oldestCaseDate = null;
		switch (attribute) {
		case ONSET_TIME:
			oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseOnsetDate();
			break;
		case RECEPTION_TIME:
			oldestCaseDate = FacadeProvider.getCaseFacade().getOldestCaseReceptionDate();
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
			List<Object> years = new ArrayList<>();
			for (int i = earliest.getYear(); i <= now.getYear(); i++) {
				years.add(i);
			}
			return years;
		case QUARTER:
			List<Object> quarters = new ArrayList<>();
			for (int i = 1; i <= 4; i++) {
				quarters.add(i);
			}
			return quarters;
		case MONTH:
			List<Object> months = new ArrayList<>();
			for (int i = 1; i <= 12; i++) {
				months.add(i);
			}
			return months;
		case EPI_WEEK:
			List<Object> epiWeeks = new ArrayList<>();
			for (int i = 1; i <= DateHelper.getMaximumEpiWeekNumber(); i++) {
				epiWeeks.add(i);
			}
			return epiWeeks;
		case QUARTER_OF_YEAR:
			List<Object> quarterOfYearList = new ArrayList<>();
			QuarterOfYear earliestQuarter = new QuarterOfYear(new Quarter(1), new Year(earliest.getYear()));
			QuarterOfYear latestQuarter = new QuarterOfYear(new Quarter(4), new Year(now.getYear()));
			while (earliestQuarter.getYear().getValue() <= latestQuarter.getYear().getValue()) {
				QuarterOfYear newQuarter = new QuarterOfYear(earliestQuarter.getQuarter(), earliestQuarter.getYear());
				quarterOfYearList.add(newQuarter.getYear().getValue() * 10 + newQuarter.getQuarter().getValue());
				earliestQuarter.increaseQuarter();
			}
			return quarterOfYearList;
		case MONTH_OF_YEAR:
			List<Object> monthOfYearList = new ArrayList<>();
			for (int year = earliest.getYear(); year <= now.getYear(); year++) {
				final int thisYear = year;
				for (int month = 1; month <= 12; month++) {
					monthOfYearList.add(thisYear * 100 + month);
				}
			}
			return monthOfYearList;
		case EPI_WEEK_OF_YEAR:
			List<Object> epiWeekOfYearList = new ArrayList<>();
			for (int year = earliest.getYear(); year <= now.getYear(); year++) {
				final int thisYear = year;
				for (int epiWeek = 1; epiWeek <= DateHelper.createEpiWeekList(year).size(); epiWeek++) {
					epiWeekOfYearList.add(thisYear * 100 + epiWeek);
				}
			}
			return epiWeekOfYearList;
		default:
			return new ArrayList<>();
		}
	}	
	
	public static List<Object> getAllAttributeValues(StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute) {
		if (subAttribute != null) {
			switch (subAttribute) {
			case YEAR:
			case QUARTER:
			case MONTH:
			case EPI_WEEK:
			case QUARTER_OF_YEAR:
			case MONTH_OF_YEAR:
			case EPI_WEEK_OF_YEAR:
				return StatisticsHelper.getListOfDateValues(attribute, subAttribute);
			case REGION:
				return new ArrayList<Object>(FacadeProvider.getRegionFacade().getAllUuids());
			case DISTRICT:
				return new ArrayList<Object>(FacadeProvider.getDistrictFacade().getAllUuids());
			default:
				throw new IllegalArgumentException(subAttribute.toString());
			}
		} else {
			switch (attribute) {
			case SEX:
				ArrayList<Object> sexList = new ArrayList<>();
				for (Sex sex : EnumUtils.getEnumList(Sex.class)) {
					sexList.add(sex.getName());
				}
				return sexList;
			case DISEASE:
				ArrayList<Object> diseaseList = new ArrayList<>();
				for (Disease disease : EnumUtils.getEnumList(Disease.class)) {
					diseaseList.add(disease.getName());
				}
				return diseaseList;
			case CLASSIFICATION:
				ArrayList<Object> classificationList = new ArrayList<>();
				for (CaseClassification classification : EnumUtils.getEnumList(CaseClassification.class)) {
					classificationList.add(classification.getName());
				}
				return classificationList;
			case OUTCOME:
				ArrayList<Object> outcomeList = new ArrayList<>();
				for (CaseOutcome outcome : EnumUtils.getEnumList(CaseOutcome.class)) {
					outcomeList.add(outcome.getName());
				}
				return outcomeList;
			case AGE_INTERVAL_1_YEAR:
			case AGE_INTERVAL_5_YEARS:
			case AGE_INTERVAL_CHILDREN_COARSE:
			case AGE_INTERVAL_CHILDREN_FINE:
			case AGE_INTERVAL_CHILDREN_MEDIUM:
			case AGE_INTERVAL_BASIC:
				return StatisticsHelper.getListOfAgeIntervalValues(attribute);
			default:
				throw new IllegalArgumentException(attribute.toString());
			}
		}
	}
	
	public static boolean isNullOrUnknown(Object value) {
		return value == null || value.toString().equals(UNKNOWN);
	}
	
	public static class StatisticsKeyComparator implements Comparator<StatisticsGroupingKey> {
		public int compare(StatisticsGroupingKey a, StatisticsGroupingKey b) {
			return a.keyCompareTo(b);
		}
	}
	
}
