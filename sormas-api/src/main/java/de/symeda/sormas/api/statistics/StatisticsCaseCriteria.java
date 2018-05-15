package de.symeda.sormas.api.statistics;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.MonthOfYear;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.EpiWeek;

public class StatisticsCaseCriteria implements Serializable {

	private static final long serialVersionUID = 4997176351789123549L;

	private List<Integer> onsetYears;
	private List<Integer> receptionYears;
	private List<Integer> reportYears;
	private List<Integer> onsetQuarters;
	private List<Integer> receptionQuarters;
	private List<Integer> reportQuarters;
	private List<Month> onsetMonths;
	private List<Month> receptionMonths;
	private List<Month> reportMonths;
	private List<EpiWeek> onsetEpiWeeks;
	private List<EpiWeek> receptionEpiWeeks;
	private List<EpiWeek> reportEpiWeeks;
	private List<QuarterOfYear> onsetQuartersOfYear;
	private List<QuarterOfYear> receptionQuartersOfYear;
	private List<QuarterOfYear> reportQuartersOfYear;
	private List<MonthOfYear> onsetMonthsOfYear;
	private List<MonthOfYear> receptionMonthsOfYear;
	private List<MonthOfYear> reportMonthsOfYear;
	private List<EpiWeek> onsetEpiWeeksOfYear; 
	private List<EpiWeek> receptionEpiWeeksOfYear; 
	private List<EpiWeek> reportEpiWeeksOfYear; 
	private Date onsetDateFrom;
	private Date onsetDateTo;
	private Date receptionDateFrom;
	private Date receptionDateTo;
	private Date reportDateFrom;
	private Date reportDateTo;
	private List<Sex> sexes;
	private Boolean sexUnknown;
	private Set<IntegerRange> ageIntervals = new HashSet<>();
	private List<Disease> diseases;
	private List<CaseClassification> classifications;
	private List<CaseOutcome> outcomes;
	private List<RegionReferenceDto> regions;
	private List<DistrictReferenceDto> districts;

	public List<Integer> getOnsetYears() {
		return onsetYears;
	}

	public List<Integer> getReceptionYears() {
		return receptionYears;
	}

	public List<Integer> getReportYears() {
		return reportYears;
	}

	public List<Integer> getOnsetQuarters() {
		return onsetQuarters;
	}

	public List<Integer> getReceptionQuarters() {
		return receptionQuarters;
	}

	public List<Integer> getReportQuarters() {
		return reportQuarters;
	}

	public List<Month> getOnsetMonths() {
		return onsetMonths;
	}

	public List<Month> getReceptionMonths() {
		return receptionMonths;
	}

	public List<Month> getReportMonths() {
		return reportMonths;
	}

	public List<EpiWeek> getOnsetEpiWeeks() {
		return onsetEpiWeeks;
	}

	public List<EpiWeek> getReceptionEpiWeeks() {
		return receptionEpiWeeks;
	}

	public List<EpiWeek> getReportEpiWeeks() {
		return reportEpiWeeks;
	}

	public List<QuarterOfYear> getOnsetQuartersOfYear() {
		return onsetQuartersOfYear;
	}

	public List<QuarterOfYear> getReceptionQuartersOfYear() {
		return receptionQuartersOfYear;
	}

	public List<QuarterOfYear> getReportQuartersOfYear() {
		return reportQuartersOfYear;
	}

	public List<MonthOfYear> getOnsetMonthsOfYear() {
		return onsetMonthsOfYear;
	}

	public List<MonthOfYear> getReceptionMonthsOfYear() {
		return receptionMonthsOfYear;
	}

	public List<MonthOfYear> getReportMonthsOfYear() {
		return reportMonthsOfYear;
	}

	public List<EpiWeek> getOnsetEpiWeeksOfYear() {
		return onsetEpiWeeksOfYear;
	}

	public List<EpiWeek> getReceptionEpiWeeksOfYear() {
		return receptionEpiWeeksOfYear;
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

	public Date getReceptionDateFrom() {
		return receptionDateFrom;
	}

	public Date getReceptionDateTo() {
		return receptionDateTo;
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

	public Set<IntegerRange> getAgeIntervals() {
		return ageIntervals;
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

	public StatisticsCaseCriteria years(List<Integer> years, StatisticsCaseAttribute mainAttribute) {
		switch (mainAttribute) {
		case ONSET_TIME:
			this.onsetYears = years;
			break;
		case RECEPTION_TIME:
			this.receptionYears = years;
			break;
		case REPORT_TIME:
			this.reportYears = years;
			break;
		default:
			throw new IllegalArgumentException(mainAttribute.toString());
		}

		return this;
	}

	public StatisticsCaseCriteria quarters(List<Integer> quarters, StatisticsCaseAttribute mainAttribute) {
		switch (mainAttribute) {
		case ONSET_TIME:
			this.onsetQuarters = quarters;
			break;
		case RECEPTION_TIME:
			this.receptionQuarters = quarters;
			break;
		case REPORT_TIME:
			this.reportQuarters = quarters;
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
		case RECEPTION_TIME:
			this.receptionMonths = months;
			break;
		case REPORT_TIME:
			this.reportMonths = months;
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
		case RECEPTION_TIME:
			this.receptionEpiWeeks = epiWeeks;
			break;
		case REPORT_TIME:
			this.reportEpiWeeks = epiWeeks;
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
		case RECEPTION_TIME:
			this.receptionQuartersOfYear = quartersOfYear;
			break;
		case REPORT_TIME:
			this.reportQuartersOfYear = quartersOfYear;
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
		case RECEPTION_TIME:	
			this.receptionMonthsOfYear = monthsOfYear;
			break;
		case REPORT_TIME:
			this.reportMonthsOfYear = monthsOfYear;
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
		case RECEPTION_TIME:
			this.receptionEpiWeeksOfYear = epiWeeksOfYear;
			break;
		case REPORT_TIME:
			this.reportEpiWeeksOfYear = epiWeeksOfYear;
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
		case RECEPTION_TIME:
			this.receptionDateFrom = from;
			this.receptionDateTo = to;
			break;
		case REPORT_TIME:
			this.reportDateFrom = from;
			this.reportDateTo = to;
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
		this.ageIntervals.addAll(ageIntervals);
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

}
