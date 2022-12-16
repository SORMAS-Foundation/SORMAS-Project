package de.symeda.sormas.api.report;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for aggregate report-related information.")
public class AggregateReportDto extends EntityDto {

	private static final long serialVersionUID = 8293942361133853979L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 861;

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
	public static final String AGE_GROUP = "ageGroup";
	public static final String EXPIRED_AGE_GROUP = "expiredAgeGroup";

	private UserReferenceDto reportingUser;
	private Disease disease;
	@Schema(description = "The year the aggregated report was generated for (for filtering).")
	private Integer year;
	@Schema(description = "The epidemiological week the aggregated report was generated for (for filtering).")
	private Integer epiWeek;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private FacilityReferenceDto healthFacility;
	private PointOfEntryReferenceDto pointOfEntry;
	@Schema(description = "Number of suspected new cases per disease in the aggregated report.")
	private Integer newCases;
	@Schema(description = "Number of lab confirmations per disease in the aggregated report.")
	private Integer labConfirmations;
	@Schema(description = "Number of deaths per disease in the aggregated report.")
	private Integer deaths;
	@Schema(description = "The age group in which the cases occured.")
	private String ageGroup;
	@Schema(description = "Indicates whether the aggregated report is a duplicate.")
	private boolean duplicate;
	@Schema(description = "Indicates whether the age group is expired.")
	private boolean expiredAgeGroup;

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

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	public boolean isExpiredAgeGroup() {
		return expiredAgeGroup;
	}

	public void setExpiredAgeGroup(boolean expiredAgeGroup) {
		this.expiredAgeGroup = expiredAgeGroup;
	}

	public static AggregateReportDto build() {
		AggregateReportDto aggregateReport = new AggregateReportDto();
		aggregateReport.setUuid(DataHelper.createUuid());
		return aggregateReport;
	}
}
