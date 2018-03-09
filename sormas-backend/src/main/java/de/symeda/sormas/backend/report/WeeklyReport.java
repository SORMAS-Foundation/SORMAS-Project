package de.symeda.sormas.backend.report;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.user.User;

@Entity(name="weeklyreport")
public class WeeklyReport extends AbstractDomainObject {

	private static final long serialVersionUID = 2192478891179257201L;

	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String INFORMANT = "informant";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String TOTAL_NUMBER_OF_CASES = "totalNumberOfCases";
	public static final String YEAR = "year";
	public static final String EPI_WEEK = "epiWeek";

	private Facility healthFacility;
	private User informant;
	private Date reportDateTime;
	private Integer totalNumberOfCases;
	private Integer year;
	private Integer epiWeek;
	
	private List<WeeklyReportEntry> reportEntries;

	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public Facility getHealthFacility() {
		return healthFacility;
	}
	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	@ManyToOne(cascade = {})
	@JoinColumn(nullable=false)
	public User getInformant() {
		return informant;
	}
	public void setInformant(User informant) {
		this.informant = informant;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	public Date getReportDateTime() {
		return reportDateTime;
	}
	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	@Column(nullable=false)
	public Integer getTotalNumberOfCases() {
		return totalNumberOfCases;
	}
	public void setTotalNumberOfCases(Integer totalNumberOfCases) {
		this.totalNumberOfCases = totalNumberOfCases;
	}

	@OneToMany(cascade = {}, mappedBy = WeeklyReportEntry.WEEKLY_REPORT)
	public List<WeeklyReportEntry> getReportEntries() {
		return reportEntries;
	}
	public void setReportEntries(List<WeeklyReportEntry> reportEntries) {
		this.reportEntries = reportEntries;
	}

	@Column(nullable=false)
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	@Column(nullable=false)
	public Integer getEpiWeek() {
		return epiWeek;
	}
	public void setEpiWeek(Integer epiWeek) {
		this.epiWeek = epiWeek;
	}
	
}
