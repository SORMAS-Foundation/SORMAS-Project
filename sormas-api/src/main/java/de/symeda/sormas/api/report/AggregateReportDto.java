package de.symeda.sormas.api.report;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class AggregateReportDto extends EntityDto {

	private static final long serialVersionUID = 8293942361133853979L;

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

	private UserReferenceDto reportingUser;
	private Disease disease;
	private Integer year;
	private Integer epiWeek;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private FacilityReferenceDto healthFacility;
	private PointOfEntryReferenceDto pointOfEntry;
	private Integer newCases;
	private Integer labConfirmations;
	private Integer deaths;
	
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}
	
	public void setReportingUser(UserReferenceDto reportingUser) {
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
	
	public RegionReferenceDto getRegion() {
		return region;
	}
	
	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	
	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}
	
	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}
	
	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}
	
	public PointOfEntryReferenceDto getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntryReferenceDto pointOfEntry) {
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

	public static AggregateReportDto build() {
		AggregateReportDto aggregateReport = new AggregateReportDto();
		aggregateReport.setUuid(DataHelper.createUuid());
		return aggregateReport;
	}
	
}
