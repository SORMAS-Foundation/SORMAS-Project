package de.symeda.sormas.api.report;

import java.util.Date;

import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class WeeklyReportDto extends WeeklyReportReferenceDto {

	private static final long serialVersionUID = -2884998571593631851L;

	public static final String I18N_PREFIX = "WeeklyReport";
	
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String INFORMANT = "informant";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String TOTAL_NUMBER_OF_CASES = "totalNumberOfCases";
	public static final String YEAR = "year";
	public static final String EPI_WEEK = "epiWeek";
	
	private FacilityReferenceDto healthFacility;
	private UserReferenceDto informant;
	private Date reportDateTime;
	private Integer totalNumberOfCases;
	private Integer year;
	private Integer epiWeek;
	
	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}
	
	public UserReferenceDto getInformant() {
		return informant;
	}
	public void setInformant(UserReferenceDto informant) {
		this.informant = informant;
	}
	
	public Date getReportDateTime() {
		return reportDateTime;
	}
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}
	
	public Integer getTotalNumberOfCases() {
		return totalNumberOfCases;
	}
	public void setTotalNumberOfCases(Integer totalNumberOfCases) {
		this.totalNumberOfCases = totalNumberOfCases;
	}
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Integer getEpiWeek() {
		return epiWeek;
	}
	public void setEpiWeek(Integer epiWeek) {
		this.epiWeek = epiWeek;
	}
	
}
