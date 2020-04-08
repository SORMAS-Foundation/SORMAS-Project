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
package de.symeda.sormas.api.statistics.contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.MonthOfYear;
import de.symeda.sormas.api.Quarter;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.Year;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.infrastructure.PopulationStatisticsCriteria;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.StatisticsSubAttributeEnum;

public class StatisticsContactCriteria implements Serializable, PopulationStatisticsCriteria {

	private static final long serialVersionUID = 4997176351789123549L;

	private List<Year> onsetYears;
	private List<Year> reportYears;
	private List<Quarter> onsetQuarters;
	private List<Quarter> reportQuarters;
	private List<Month> onsetMonths;
	private List<Month> reportMonths;
	private List<EpiWeek> onsetEpiWeeks;
	private List<EpiWeek> reportEpiWeeks;
	private List<QuarterOfYear> onsetQuartersOfYear;
	private List<QuarterOfYear> reportQuartersOfYear;
	private List<MonthOfYear> onsetMonthsOfYear;
	private List<MonthOfYear> reportMonthsOfYear;
	private List<EpiWeek> onsetEpiWeeksOfYear; 
	private List<EpiWeek> reportEpiWeeksOfYear; 
	private Date onsetDateFrom;
	private Date onsetDateTo;
	private Date reportDateFrom;
	private Date reportDateTo;
	private List<Sex> sexes;
	private Boolean sexUnknown;
	private List<IntegerRange> ageIntervals;
	private List<AgeGroup> ageGroups;
	private List<Disease> diseases;
	private List<ContactClassification> classifications;
	private List<FollowUpStatus> followUpStatuses;
	private List<RegionReferenceDto> regions;
	private List<DistrictReferenceDto> districts;
	private List<UserRole> reportingUserRoles;
	
	public List<Year> getOnsetYears() {
		return onsetYears;
	}
	
	public List<Year> getReportYears() {
		return reportYears;
	}

	public List<Quarter> getOnsetQuarters() {
		return onsetQuarters;
	}

	public List<Quarter> getReportQuarters() {
		return reportQuarters;
	}

	public List<Month> getOnsetMonths() {
		return onsetMonths;
	}

	public List<Month> getReportMonths() {
		return reportMonths;
	}

	public List<EpiWeek> getOnsetEpiWeeks() {
		return onsetEpiWeeks;
	}

	public List<EpiWeek> getReportEpiWeeks() {
		return reportEpiWeeks;
	}

	public List<QuarterOfYear> getOnsetQuartersOfYear() {
		return onsetQuartersOfYear;
	}

	public List<QuarterOfYear> getReportQuartersOfYear() {
		return reportQuartersOfYear;
	}

	public List<MonthOfYear> getOnsetMonthsOfYear() {
		return onsetMonthsOfYear;
	}

	public List<MonthOfYear> getReportMonthsOfYear() {
		return reportMonthsOfYear;
	}

	public List<EpiWeek> getOnsetEpiWeeksOfYear() {
		return onsetEpiWeeksOfYear;
	}

	public List<EpiWeek> getReportEpiWeeksOfYear() {
		return reportEpiWeeksOfYear;
	}

	public Date getOnsetDateFrom() {
		return onsetDateFrom;
	}

	public Date getOnsetDateTo() {
		return onsetDateTo;
	}

	public Date getReportDateFrom() {
		return reportDateFrom;
	}

	public Date getReportDateTo() {
		return reportDateTo;
	}

	public List<Sex> getSexes() {
		return sexes;
	}

	public Boolean isSexUnknown() {
		return sexUnknown;
	}

	public List<IntegerRange> getAgeIntervals() {
		return ageIntervals;
	}
	
	public List<AgeGroup> getAgeGroups() {
		return ageGroups;
	}

	public List<Disease> getDiseases() {
		return diseases;
	}

	public List<ContactClassification> getClassifications() {
		return classifications;
	}

	public List<FollowUpStatus> getFollowUpStatuses() {
		return followUpStatuses;
	}

	public List<RegionReferenceDto> getRegions() {
		return regions;
	}

	public List<DistrictReferenceDto> getDistricts() {
		return districts;
	}

	public List<UserRole> getReportingUserRoles() {
		return reportingUserRoles;
	}

	public StatisticsContactCriteria years(List<Year> years, StatisticsContactAttribute mainAttribute) {
		switch (mainAttribute) {
		case REPORT_TIME:
			this.reportYears = years;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsContactCriteria quarters(List<Quarter> quarters, StatisticsContactAttribute mainAttribute) {
		switch (mainAttribute) {
		case REPORT_TIME:
			this.reportQuarters = quarters;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsContactCriteria months(List<Month> months, StatisticsContactAttribute mainAttribute) {
		switch (mainAttribute) {
		case REPORT_TIME:
			this.reportMonths = months;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsContactCriteria epiWeeks(List<EpiWeek> epiWeeks, StatisticsContactAttribute mainAttribute) {
		switch (mainAttribute) {
		case REPORT_TIME:
			this.reportEpiWeeks = epiWeeks;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsContactCriteria quartersOfYear(List<QuarterOfYear> quartersOfYear, StatisticsContactAttribute mainAttribute) {
		switch (mainAttribute) {
		case REPORT_TIME:
			this.reportQuartersOfYear = quartersOfYear;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsContactCriteria monthsOfYear(List<MonthOfYear> monthsOfYear, StatisticsContactAttribute mainAttribute) {
		switch (mainAttribute) {
		case REPORT_TIME:
			this.reportMonthsOfYear = monthsOfYear;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsContactCriteria epiWeeksOfYear(List<EpiWeek> epiWeeksOfYear, StatisticsContactAttribute mainAttribute) {
		switch (mainAttribute) {
		case REPORT_TIME:
			this.reportEpiWeeksOfYear = epiWeeksOfYear;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsContactCriteria dateRange(Date from, Date to, StatisticsContactAttribute mainAttribute) {
		switch (mainAttribute) {
		case REPORT_TIME:
			this.reportDateFrom = from;
			this.reportDateTo = to;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsContactCriteria sexes(List<Sex> sexes) {
		this.sexes = sexes;
		return this;
	}

	public StatisticsContactCriteria sexUnknown(Boolean sexUnknown) {
		this.sexUnknown = sexUnknown;
		return this;
	}

	public StatisticsContactCriteria addAgeIntervals(List<IntegerRange> ageIntervals) {
		if (this.ageIntervals == null) {
			this.ageIntervals = new ArrayList<>();
		}
		
		this.ageIntervals.addAll(ageIntervals);
		return this;
	}
	
	public StatisticsContactCriteria addAgeGroups(List<AgeGroup> ageGroups) {
		if (this.ageGroups == null) {
			this.ageGroups = new ArrayList<>();
		}
		
		this.ageGroups.addAll(ageGroups);
		return this;
	}

	public StatisticsContactCriteria diseases(List<Disease> diseases) {
		this.diseases = diseases;
		return this;
	}

	public StatisticsContactCriteria classifications(List<ContactClassification> classifications) {
		this.classifications = classifications;
		return this;
	}

	public StatisticsContactCriteria contactFollowUpStatuses(List<FollowUpStatus> statuses) {
		this.followUpStatuses = statuses;
		return this;
	}

	public StatisticsContactCriteria regions(List<RegionReferenceDto> regions) {
		this.regions = regions;
		return this;
	}

	public StatisticsContactCriteria districts(List<DistrictReferenceDto> districts) {
		this.districts = districts;
		return this;
	}

	public StatisticsContactCriteria reportingUserRoles(List<UserRole> reportingUserRoles) {
		this.reportingUserRoles = reportingUserRoles;
		return this;
	}

	public List<? extends StatisticsGroupingKey> getFilterValuesForGrouping(StatisticsContactAttribute attribute, StatisticsSubAttributeEnum subAttribute) {
		if (subAttribute != null) {
			switch (subAttribute) {
			case REGION:
				return regions;
			case DISTRICT:
				return districts;
			case YEAR:
				switch (attribute) {
				case REPORT_TIME:
					return reportYears;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case QUARTER:
				switch (attribute) {
				case REPORT_TIME:
					return reportQuarters;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case MONTH:
				switch (attribute) {
				case REPORT_TIME:
					return reportMonths;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case EPI_WEEK:
				switch (attribute) {
				case REPORT_TIME:
					return reportEpiWeeks;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case QUARTER_OF_YEAR:
				switch (attribute) {
				case REPORT_TIME:
					return reportQuartersOfYear;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case MONTH_OF_YEAR:
				switch (attribute) {
				case REPORT_TIME:
					return reportMonthsOfYear;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case EPI_WEEK_OF_YEAR:
				switch (attribute) {
				case REPORT_TIME:
					return reportEpiWeeksOfYear;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			default: throw new IllegalArgumentException(subAttribute.toString());
			}
		} else {
			switch (attribute) {
			case DISEASE:
				return diseases;
			case SEX:
				return sexes;
			case CLASSIFICATION:
				return classifications;
			case FOLLOW_UP_STATUS:
				return followUpStatuses;
			case AGE_INTERVAL_1_YEAR:
			case AGE_INTERVAL_5_YEARS:
			case AGE_INTERVAL_CHILDREN_COARSE:
			case AGE_INTERVAL_CHILDREN_FINE:
			case AGE_INTERVAL_CHILDREN_MEDIUM:
			case AGE_INTERVAL_BASIC:
				return ageIntervals;
			case REPORTING_USER_ROLE:
				return reportingUserRoles;
			default: throw new IllegalArgumentException(attribute.toString());
			}
		}
	}

	public boolean hasOnsetDate() {
		return onsetDateFrom != null || onsetDateTo != null || onsetEpiWeeks != null || onsetEpiWeeksOfYear != null
				|| onsetMonths != null || onsetMonthsOfYear != null || onsetQuarters != null || onsetQuartersOfYear != null
				|| onsetYears != null;
	}
	
}
