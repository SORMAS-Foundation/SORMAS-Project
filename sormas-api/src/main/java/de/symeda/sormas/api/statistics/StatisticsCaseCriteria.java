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
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.EpiWeek;

public class StatisticsCaseCriteria implements Serializable {

	private static final long serialVersionUID = 4997176351789123549L;

	private List<Year> onsetYears;
	private List<Year> reportYears;
	private List<Year> outcomeYears;
	private List<Quarter> onsetQuarters;
	private List<Quarter> reportQuarters;
	private List<Quarter> outcomeQuarters;
	private List<Month> onsetMonths;
	private List<Month> reportMonths;
	private List<Month> outcomeMonths;
	private List<EpiWeek> onsetEpiWeeks;
	private List<EpiWeek> reportEpiWeeks;
	private List<EpiWeek> outcomeEpiWeeks;
	private List<QuarterOfYear> onsetQuartersOfYear;
	private List<QuarterOfYear> reportQuartersOfYear;
	private List<QuarterOfYear> outcomeQuartersOfYear;
	private List<MonthOfYear> onsetMonthsOfYear;
	private List<MonthOfYear> reportMonthsOfYear;
	private List<MonthOfYear> outcomeMonthsOfYear;
	private List<EpiWeek> onsetEpiWeeksOfYear;
	private List<EpiWeek> reportEpiWeeksOfYear;
	private List<EpiWeek> outcomeEpiWeeksOfYear;
	private Date onsetDateFrom;
	private Date onsetDateTo;
	private Date reportDateFrom;
	private Date reportDateTo;
	private Date outcomeDateFrom;
	private Date outcomeDateTo;
	private List<Sex> sexes;
	private Boolean sexUnknown;
	private List<IntegerRange> ageIntervals;
	private List<AgeGroup> ageGroups;
	private List<Disease> diseases;
	private List<CaseClassification> classifications;
	private List<CaseOutcome> outcomes;
	private List<RegionReferenceDto> regions;
	private List<DistrictReferenceDto> districts;
	private List<CommunityReferenceDto> communities;
	private List<FacilityReferenceDto> healthFacilities;
	private List<RegionReferenceDto> personRegions;
	private List<DistrictReferenceDto> personDistricts;
	private List<CommunityReferenceDto> personCommunities;
	private String personCity;
	private String personPostcode;
	private List<UserRole> reportingUserRoles;

	public List<Year> getOnsetYears() {
		return onsetYears;
	}

	public List<Year> getReportYears() {
		return reportYears;
	}

	public List<Year> getOutcomeYears() {
		return outcomeYears;
	}

	public List<Quarter> getOnsetQuarters() {
		return onsetQuarters;
	}

	public List<Quarter> getReportQuarters() {
		return reportQuarters;
	}

	public List<Quarter> getOutcomeQuarters() {
		return outcomeQuarters;
	}

	public List<Month> getOnsetMonths() {
		return onsetMonths;
	}

	public List<Month> getReportMonths() {
		return reportMonths;
	}

	public List<Month> getOutcomeMonths() {
		return outcomeMonths;
	}

	public List<EpiWeek> getOnsetEpiWeeks() {
		return onsetEpiWeeks;
	}

	public List<EpiWeek> getReportEpiWeeks() {
		return reportEpiWeeks;
	}

	public List<EpiWeek> getOutcomeEpiWeeks() {
		return outcomeEpiWeeks;
	}

	public List<QuarterOfYear> getOnsetQuartersOfYear() {
		return onsetQuartersOfYear;
	}

	public List<QuarterOfYear> getReportQuartersOfYear() {
		return reportQuartersOfYear;
	}

	public List<QuarterOfYear> getOutcomeQuartersOfYear() {
		return outcomeQuartersOfYear;
	}

	public List<MonthOfYear> getOnsetMonthsOfYear() {
		return onsetMonthsOfYear;
	}

	public List<MonthOfYear> getReportMonthsOfYear() {
		return reportMonthsOfYear;
	}

	public List<MonthOfYear> getOutcomeMonthsOfYear() {
		return outcomeMonthsOfYear;
	}

	public List<EpiWeek> getOnsetEpiWeeksOfYear() {
		return onsetEpiWeeksOfYear;
	}

	public List<EpiWeek> getReportEpiWeeksOfYear() {
		return reportEpiWeeksOfYear;
	}

	public List<EpiWeek> getOutcomeEpiWeeksOfYear() {
		return outcomeEpiWeeksOfYear;
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

	public Date getOutcomeDateFrom() {
		return outcomeDateFrom;
	}

	public Date getOutcomeDateTo() {
		return outcomeDateTo;
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

	public List<CaseClassification> getClassifications() {
		return classifications;
	}

	public List<CaseOutcome> getOutcomes() {
		return outcomes;
	}

	public List<RegionReferenceDto> getRegions() {
		return regions;
	}

	public List<DistrictReferenceDto> getDistricts() {
		return districts;
	}

	public List<CommunityReferenceDto> getCommunities() {
		return communities;
	}

	public List<FacilityReferenceDto> getHealthFacilities() {
		return healthFacilities;
	}

	public List<RegionReferenceDto> getPersonRegions() {
		return personRegions;
	}

	public List<DistrictReferenceDto> getPersonDistricts() {
		return personDistricts;
	}

	public List<CommunityReferenceDto> getPersonCommunities() {
		return personCommunities;
	}

	public String getPersonCity() {
		return personCity;
	}

	public String getPersonPostcode() {
		return personPostcode;
	}

	public List<UserRole> getReportingUserRoles() {
		return reportingUserRoles;
	}

	public StatisticsCaseCriteria years(List<Year> years, StatisticsCaseAttribute mainAttribute) {
		switch (mainAttribute) {
		case ONSET_TIME:
			this.onsetYears = years;
			break;
		case REPORT_TIME:
			this.reportYears = years;
			break;
		case OUTCOME_TIME:
			this.outcomeYears = years;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsCaseCriteria quarters(List<Quarter> quarters, StatisticsCaseAttribute mainAttribute) {
		switch (mainAttribute) {
		case ONSET_TIME:
			this.onsetQuarters = quarters;
			break;
		case REPORT_TIME:
			this.reportQuarters = quarters;
			break;
		case OUTCOME_TIME:
			this.outcomeQuarters = quarters;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsCaseCriteria months(List<Month> months, StatisticsCaseAttribute mainAttribute) {
		switch (mainAttribute) {
		case ONSET_TIME:
			this.onsetMonths = months;
			break;
		case REPORT_TIME:
			this.reportMonths = months;
			break;
		case OUTCOME_TIME:
			this.outcomeMonths = months;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsCaseCriteria epiWeeks(List<EpiWeek> epiWeeks, StatisticsCaseAttribute mainAttribute) {
		switch (mainAttribute) {
		case ONSET_TIME:
			this.onsetEpiWeeks = epiWeeks;
			break;
		case REPORT_TIME:
			this.reportEpiWeeks = epiWeeks;
			break;
		case OUTCOME_TIME:
			this.outcomeEpiWeeks = epiWeeks;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsCaseCriteria quartersOfYear(List<QuarterOfYear> quartersOfYear, StatisticsCaseAttribute mainAttribute) {
		switch (mainAttribute) {
		case ONSET_TIME:
			this.onsetQuartersOfYear = quartersOfYear;
			break;
		case REPORT_TIME:
			this.reportQuartersOfYear = quartersOfYear;
			break;
		case OUTCOME_TIME:
			this.outcomeQuartersOfYear = quartersOfYear;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsCaseCriteria monthsOfYear(List<MonthOfYear> monthsOfYear, StatisticsCaseAttribute mainAttribute) {
		switch (mainAttribute) {
		case ONSET_TIME:
			this.onsetMonthsOfYear = monthsOfYear;
			break;
		case REPORT_TIME:
			this.reportMonthsOfYear = monthsOfYear;
			break;
		case OUTCOME_TIME:
			this.outcomeMonthsOfYear = monthsOfYear;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsCaseCriteria epiWeeksOfYear(List<EpiWeek> epiWeeksOfYear, StatisticsCaseAttribute mainAttribute) {
		switch (mainAttribute) {
		case ONSET_TIME:
			this.onsetEpiWeeksOfYear = epiWeeksOfYear;
			break;
		case REPORT_TIME:
			this.reportEpiWeeksOfYear = epiWeeksOfYear;
			break;
		case OUTCOME_TIME:
			this.outcomeEpiWeeksOfYear = epiWeeksOfYear;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsCaseCriteria dateRange(Date from, Date to, StatisticsCaseAttribute mainAttribute) {
		switch (mainAttribute) {
		case ONSET_TIME:
			this.onsetDateFrom = from;
			this.onsetDateTo = to;
			break;
		case REPORT_TIME:
			this.reportDateFrom = from;
			this.reportDateTo = to;
			break;
		case OUTCOME_TIME:
			this.outcomeDateFrom = from;
			this.outcomeDateTo = to;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsCaseCriteria sexes(List<Sex> sexes) {
		this.sexes = sexes;
		return this;
	}

	public StatisticsCaseCriteria sexUnknown(Boolean sexUnknown) {
		this.sexUnknown = sexUnknown;
		return this;
	}

	public StatisticsCaseCriteria addAgeIntervals(List<IntegerRange> ageIntervals) {
		if (this.ageIntervals == null) {
			this.ageIntervals = new ArrayList<>();
		}

		this.ageIntervals.addAll(ageIntervals);
		return this;
	}

	public StatisticsCaseCriteria addAgeGroups(List<AgeGroup> ageGroups) {
		if (this.ageGroups == null) {
			this.ageGroups = new ArrayList<>();
		}

		this.ageGroups.addAll(ageGroups);
		return this;
	}

	public StatisticsCaseCriteria diseases(List<Disease> diseases) {
		this.diseases = diseases;
		return this;
	}

	public StatisticsCaseCriteria classifications(List<CaseClassification> classifications) {
		this.classifications = classifications;
		return this;
	}

	public StatisticsCaseCriteria outcomes(List<CaseOutcome> outcomes) {
		this.outcomes = outcomes;
		return this;
	}

	public StatisticsCaseCriteria regions(List<RegionReferenceDto> regions) {
		this.regions = regions;
		return this;
	}

	public StatisticsCaseCriteria districts(List<DistrictReferenceDto> districts) {
		this.districts = districts;
		return this;
	}

	public StatisticsCaseCriteria communities(List<CommunityReferenceDto> communities) {
		this.communities = communities;
		return this;
	}

	public StatisticsCaseCriteria healthFacilities(List<FacilityReferenceDto> healthFacilities) {
		this.healthFacilities = healthFacilities;
		return this;
	}

	public StatisticsCaseCriteria personRegions(List<RegionReferenceDto> personRegions) {
		this.personRegions = personRegions;
		return this;
	}

	public StatisticsCaseCriteria personDistricts(List<DistrictReferenceDto> personDistricts) {
		this.personDistricts = personDistricts;
		return this;
	}

	public StatisticsCaseCriteria personCommunities(List<CommunityReferenceDto> personCommunities) {
		this.personCommunities = personCommunities;
		return this;
	}

	public StatisticsCaseCriteria setPersonCity(String personCity) {
		this.personCity = personCity;
		return this;
	}

	public StatisticsCaseCriteria setPersonPostcode(String personPostcode) {
		this.personPostcode = personPostcode;
		return this;
	}

	public StatisticsCaseCriteria reportingUserRoles(List<UserRole> reportingUserRoles) {
		this.reportingUserRoles = reportingUserRoles;
		return this;
	}

	public List<? extends StatisticsGroupingKey> getFilterValuesForGrouping(
		StatisticsCaseAttribute attribute,
		StatisticsCaseSubAttribute subAttribute) {

		if (subAttribute != null) {
			switch (subAttribute) {
			case REGION:
				return regions;
			case DISTRICT:
				return districts;
			case COMMUNITY:
				return communities;
			case FACILITY:
				return healthFacilities;
			case YEAR:
				switch (attribute) {
				case ONSET_TIME:
					return onsetYears;
				case REPORT_TIME:
					return reportYears;
				case OUTCOME_TIME:
					return outcomeYears;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case QUARTER:
				switch (attribute) {
				case ONSET_TIME:
					return onsetQuarters;
				case REPORT_TIME:
					return reportQuarters;
				case OUTCOME_TIME:
					return outcomeQuarters;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case MONTH:
				switch (attribute) {
				case ONSET_TIME:
					return onsetMonths;
				case REPORT_TIME:
					return reportMonths;
				case OUTCOME_TIME:
					return outcomeMonths;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case EPI_WEEK:
				switch (attribute) {
				case ONSET_TIME:
					return onsetEpiWeeks;
				case REPORT_TIME:
					return reportEpiWeeks;
				case OUTCOME_TIME:
					return outcomeEpiWeeks;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case QUARTER_OF_YEAR:
				switch (attribute) {
				case ONSET_TIME:
					return onsetQuartersOfYear;
				case REPORT_TIME:
					return reportQuartersOfYear;
				case OUTCOME_TIME:
					return outcomeQuartersOfYear;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case MONTH_OF_YEAR:
				switch (attribute) {
				case ONSET_TIME:
					return onsetMonthsOfYear;
				case REPORT_TIME:
					return reportMonthsOfYear;
				case OUTCOME_TIME:
					return outcomeMonthsOfYear;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			case EPI_WEEK_OF_YEAR:
				switch (attribute) {
				case ONSET_TIME:
					return onsetEpiWeeksOfYear;
				case REPORT_TIME:
					return reportEpiWeeksOfYear;
				case OUTCOME_TIME:
					return outcomeEpiWeeksOfYear;
				default:
					throw new IllegalArgumentException(attribute.toString());
				}
			default:
				throw new IllegalArgumentException(subAttribute.toString());
			}
		} else {
			switch (attribute) {
			case DISEASE:
				return diseases;
			case SEX:
				return sexes;
			case CLASSIFICATION:
				return classifications;
			case OUTCOME:
				return outcomes;
			case AGE_INTERVAL_1_YEAR:
			case AGE_INTERVAL_5_YEARS:
			case AGE_INTERVAL_CHILDREN_COARSE:
			case AGE_INTERVAL_CHILDREN_FINE:
			case AGE_INTERVAL_CHILDREN_MEDIUM:
			case AGE_INTERVAL_BASIC:
				return ageIntervals;
			case REPORTING_USER_ROLE:
				return reportingUserRoles;
			default:
				throw new IllegalArgumentException(attribute.toString());
			}
		}
	}

	public boolean hasOnsetDate() {

		return onsetDateFrom != null
			|| onsetDateTo != null
			|| onsetEpiWeeks != null
			|| onsetEpiWeeksOfYear != null
			|| onsetMonths != null
			|| onsetMonthsOfYear != null
			|| onsetQuarters != null
			|| onsetQuartersOfYear != null
			|| onsetYears != null;
	}
}
