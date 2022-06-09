package de.symeda.sormas.api.report;

import java.io.Serializable;
import java.util.Objects;

import de.symeda.sormas.api.Disease;

public class AggregatedCaseCountDto implements Serializable {

	public static final String I18N_PREFIX = "AggregateReport";

	public static final String REGION_NAME = "regionName";
	public static final String REGION_ID = "regionId";
	public static final String DISTRICT_NAME = "districtName";
	public static final String DISTRICT_ID = "districtId";
	public static final String HEALTH_FACILITY_NAME = "healthFacilityName";
	public static final String HEALTH_FACILITY_ID = "healthFacilityId";
	public static final String POINT_OF_ENTRY_NAME = "pointOfEntryName";
	public static final String POINT_OF_ENTRY_ID = "pointOfEntryId";
	public static final String EPI_WEEK = "epiWeek";

	public static final String DISEASE = "disease";
	public static final String AGE_GROUP = "ageGroup";
	public static final String NEW_CASES = "newCases";
	public static final String LAB_CONFIRMATIONS = "labConfirmations";
	public static final String DEATHS = "deaths";

	private String regionName;
	private Long regionId;
	private String districtName;
	private Long districtId;
	private String healthFacilityName;
	private Long healthFacilityId;
	private String pointOfEntryName;
	private Long pointOfEntryId;
	private int epiWeek;
	private static final long serialVersionUID = -6857559727281292882L;
	private Disease disease;
	private long newCases;
	private long labConfirmations;
	private long deaths;
	private AggregateReportGroupingDto aggregateReportGroupingDto;
	private String ageGroup;

	public AggregatedCaseCountDto() {
	}

	public AggregatedCaseCountDto(Disease disease, long newCases, long labConfirmations, long deaths, int epiWeek, String ageGroup) {

		this.disease = disease;
		this.newCases = newCases;
		this.labConfirmations = labConfirmations;
		this.deaths = deaths;
		this.epiWeek = epiWeek;
		this.ageGroup = ageGroup;
	}

	public AggregatedCaseCountDto(
		Disease disease,
		long newCases,
		long labConfirmations,
		long deaths,
		Integer epiWeek,
		String ageGroup,
		String regionName,
		Long regionId) {
		this(disease, newCases, labConfirmations, deaths, epiWeek, ageGroup);
		this.regionName = regionName;
		this.regionId = regionId;
	}

	public AggregatedCaseCountDto(
		Disease disease,
		long newCases,
		long labConfirmations,
		long deaths,
		Integer epiWeek,
		String ageGroup,
		String regionName,
		Long regionId,
		String districtName,
		Long districtId) {
		this(disease, newCases, labConfirmations, deaths, epiWeek, ageGroup, regionName, regionId);
		this.districtName = districtName;
		this.districtId = districtId;
	}

	public AggregatedCaseCountDto(
		Disease disease,
		long newCases,
		long labConfirmations,
		long deaths,
		Integer epiWeek,
		String ageGroup,
		String regionName,
		Long regionId,
		String districtName,
		Long districtId,
		String healthFacilityName,
		Long healthFacilityId) {
		this(disease, newCases, labConfirmations, deaths, epiWeek, ageGroup, regionName, regionId, districtName, districtId);
		this.healthFacilityName = healthFacilityName;
		this.healthFacilityId = healthFacilityId;
	}

	/**
	 * constructor created for Point of Entry grouping
	 * @param disease
	 * @param newCases
	 * @param labConfirmations
	 * @param deaths
	 * @param epiWeek
	 * @param regionName
	 * @param regionId
	 * @param districtName
	 * @param districtId
	 * @param pointOfEntryId
	 * @param pointOfEntryName
	 */
	public AggregatedCaseCountDto(
		Disease disease,
		long newCases,
		long labConfirmations,
		long deaths,
		Integer epiWeek,
		String ageGroup,
		String regionName,
		Long regionId,
		String districtName,
		Long districtId,
		Long pointOfEntryId,
		String pointOfEntryName) {
		this(disease, newCases, labConfirmations, deaths, epiWeek, ageGroup, regionName, regionId, districtName, districtId);
		this.pointOfEntryName = pointOfEntryName;
		this.pointOfEntryId = pointOfEntryId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public Long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	public String getHealthFacilityName() {
		return healthFacilityName;
	}

	public void setHealthFacilityName(String healthFacilityName) {
		this.healthFacilityName = healthFacilityName;
	}

	public Long getHealthFacilityId() {
		return healthFacilityId;
	}

	public void setHealthFacilityId(Long healthFacilityId) {
		this.healthFacilityId = healthFacilityId;
	}

	public String getPointOfEntryName() {
		return pointOfEntryName;
	}

	public void setPointOfEntryName(String pointOfEntryName) {
		this.pointOfEntryName = pointOfEntryName;
	}

	public Long getPointOfEntryId() {
		return pointOfEntryId;
	}

	public void setPointOfEntryId(Long pointOfEntryId) {
		this.pointOfEntryId = pointOfEntryId;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public long getNewCases() {
		return newCases;
	}

	public void setNewCases(long newCases) {
		this.newCases = newCases;
	}

	public long getLabConfirmations() {
		return labConfirmations;
	}

	public void setLabConfirmations(long labConfirmations) {
		this.labConfirmations = labConfirmations;
	}

	public long getDeaths() {
		return deaths;
	}

	public void setDeaths(long deaths) {
		this.deaths = deaths;
	}

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public AggregateReportGroupingDto getAggregateReportGroupingDto() {
		return aggregateReportGroupingDto;
	}

	public void setAggregateReportGroupingDto(AggregateReportGroupingDto aggregateReportGroupingDto) {
		this.aggregateReportGroupingDto = aggregateReportGroupingDto;
	}

	public Integer getEpiWeek() {
		return epiWeek;
	}

	public void setEpiWeek(Integer epiWeek) {
		this.epiWeek = epiWeek;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + (int) deaths;
		result = prime * result + ((disease == null) ? 0 : disease.hashCode());
		result = prime * result + (int) labConfirmations;
		result = prime * result + (int) newCases;
		result = prime * result + ((ageGroup == null) ? 0 : ageGroup.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AggregatedCaseCountDto other = (AggregatedCaseCountDto) obj;
		if (deaths != other.deaths)
			return false;
		if (disease != other.disease)
			return false;
		if (labConfirmations != other.labConfirmations)
			return false;
		if (newCases != other.newCases)
			return false;
		if (!Objects.equals(ageGroup, other.ageGroup))
			return false;
		return true;
	}
}
