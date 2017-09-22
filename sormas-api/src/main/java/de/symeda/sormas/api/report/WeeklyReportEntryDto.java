package de.symeda.sormas.api.report;

import de.symeda.sormas.api.Disease;

public class WeeklyReportEntryDto extends WeeklyReportEntryReferenceDto {

	private static final long serialVersionUID = 7863410150359837423L;

	private static final String I18N_PREFIX = "WeeklyReportEntry";
	
	public static final String WEEKLY_REPORT = "weeklyReport";
	public static final String DISEASE = "disease";
	public static final String NUMBER_OF_CASES = "numberOfCases";
	
	private WeeklyReportReferenceDto weeklyReport;
	private Disease disease;
	private Integer numberOfCases;
	
	public WeeklyReportReferenceDto getWeeklyReport() {
		return weeklyReport;
	}
	public void setWeeklyReport(WeeklyReportReferenceDto weeklyReport) {
		this.weeklyReport = weeklyReport;
	}
	
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	public Integer getNumberOfCases() {
		return numberOfCases;
	}
	public void setNumberOfCases(Integer numberOfCases) {
		this.numberOfCases = numberOfCases;
	}
	
}
