package de.symeda.sormas.api.report;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;

public class WeeklyReportEntryDto extends EntityDto {

	private static final long serialVersionUID = 7863410150359837423L;

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
	
	public WeeklyReportEntryReferenceDto toReference() {
		return new WeeklyReportEntryReferenceDto(getUuid());
	}
	
}
