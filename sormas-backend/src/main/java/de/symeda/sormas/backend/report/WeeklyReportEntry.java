package de.symeda.sormas.backend.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name="weeklyreportentry")
public class WeeklyReportEntry extends AbstractDomainObject {

	private static final long serialVersionUID = -4161597011857710604L;
	
	public static final String WEEKLY_REPORT = "weeklyReport";
	public static final String DISEASE = "disease";
	public static final String NUMBER_OF_CASES = "numberOfCases";
	
	private WeeklyReport weeklyReport;
	private Disease disease;
	private Integer numberOfCases;

	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public WeeklyReport getWeeklyReport() {
		return weeklyReport;
	}
	public void setWeeklyReport(WeeklyReport weeklyReport) {
		this.weeklyReport = weeklyReport;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column(nullable=false)
	public Integer getNumberOfCases() {
		return numberOfCases;
	}
	public void setNumberOfCases(Integer numberOfCases) {
		this.numberOfCases = numberOfCases;
	}

}
