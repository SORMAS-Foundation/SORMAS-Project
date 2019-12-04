package de.symeda.sormas.api.report;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

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
	private int newCases;
	private int labConfirmations;
	private int deaths;
	
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
	
	public int getNewCases() {
		return newCases;
	}
	
	public void setNewCases(int newCases) {
		this.newCases = newCases;
	}
	
	public int getLabConfirmations() {
		return labConfirmations;
	}
	
	public void setLabConfirmations(int labConfirmations) {
		this.labConfirmations = labConfirmations;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	
}
