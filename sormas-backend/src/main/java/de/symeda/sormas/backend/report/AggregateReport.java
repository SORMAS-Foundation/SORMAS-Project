package de.symeda.sormas.backend.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Entity(name = "aggregatereport")
public class AggregateReport extends AbstractDomainObject {

	private static final long serialVersionUID = -2809338755584760337L;

	public static final String REPORTING_USER = "reportingUser";
	public static final String DISEASE = "disease";
	public static final String YEAR = "year";
	public static final String EPI_WEEK = "epiWeek";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String NEW_CASES = "newCases";
	public static final String LAB_CONFIRMATIONS = "labConfirmations";
	public static final String DEATHS = "deaths";

	private User reportingUser;
	private Disease disease;
	private Integer year;
	private Integer epiWeek;
	private Region region;
	private District district;
	private Facility healthFacility;
	private PointOfEntry pointOfEntry;
	private Integer newCases;
	private Integer labConfirmations;
	private Integer deaths;

	@ManyToOne(cascade = {})
	@JoinColumn
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Column
	public Integer getEpiWeek() {
		return epiWeek;
	}

	public void setEpiWeek(Integer epiWeek) {
		this.epiWeek = epiWeek;
	}

	@ManyToOne(cascade = {})
	@JoinColumn
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne(cascade = {})
	@JoinColumn
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne(cascade = {})
	public Facility getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	@ManyToOne(cascade = {})
	public PointOfEntry getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntry pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	@Column
	public Integer getNewCases() {
		return newCases;
	}

	public void setNewCases(Integer newCases) {
		this.newCases = newCases;
	}

	@Column
	public Integer getLabConfirmations() {
		return labConfirmations;
	}

	public void setLabConfirmations(Integer labConfirmations) {
		this.labConfirmations = labConfirmations;
	}

	@Column
	public Integer getDeaths() {
		return deaths;
	}

	public void setDeaths(Integer deaths) {
		this.deaths = deaths;
	}
}
