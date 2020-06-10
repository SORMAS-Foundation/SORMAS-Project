package de.symeda.sormas.app.backend.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;

@Entity(name = AggregateReport.TABLE_NAME)
@DatabaseTable(tableName = AggregateReport.TABLE_NAME)
public class AggregateReport extends AbstractDomainObject {

	private static final long serialVersionUID = -2809338755584760337L;

	public static final String TABLE_NAME = "aggregateReport";
	public static final String I18N_PREFIX = "AggregateReport";

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

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;
	@Enumerated(EnumType.STRING)
	private Disease disease;
	@Column
	private Integer year;
	@Column
	private Integer epiWeek;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Region region;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private District district;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility healthFacility;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private PointOfEntry pointOfEntry;
	@Column
	private Integer newCases;
	@Column
	private Integer labConfirmations;
	@Column
	private Integer deaths;

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
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

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Facility getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	public PointOfEntry getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntry pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public Integer getNewCases() {
		return newCases;
	}

	public void setNewCases(Integer newCases) {
		this.newCases = newCases;
	}

	public Integer getLabConfirmations() {
		return labConfirmations;
	}

	public void setLabConfirmations(Integer labConfirmations) {
		this.labConfirmations = labConfirmations;
	}

	public Integer getDeaths() {
		return deaths;
	}

	public void setDeaths(Integer deaths) {
		this.deaths = deaths;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
