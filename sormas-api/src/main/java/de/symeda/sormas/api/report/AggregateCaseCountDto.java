package de.symeda.sormas.api.report;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;

public class AggregateCaseCountDto implements Serializable {

	public static final String I18N_PREFIX = "AggregateReport";

	public static final String REGION_NAME = "regionName";
	public static final String REGION_ID = "regionId";
	public static final String DISTRICT_NAME = "districtName";
	public static final String DISTRICT_ID = "districtId";
	public static final String HEALTH_FACILITY_NAME = "healthFacilityName";
	public static final String HEALTH_FACILITY_ID = "healthFacilityId";
	public static final String POINT_OF_ENTRY_NAME = "pointOfEntryName";
	public static final String POINT_OF_ENTRY_ID = "pointOfEntryId";
	public static final String YEAR = "year";
	public static final String EPI_WEEK = "epiWeek";

	public static final String DISEASE = "disease";
	public static final String AGE_GROUP = "ageGroup";
	public static final String NEW_CASES = "newCases";
	public static final String LAB_CONFIRMATIONS = "labConfirmations";
	public static final String DEATHS = "deaths";
	public static final String REPORTING_USER = "reportingUser";
	private static final long serialVersionUID = -6857559727281292882L;
	private String regionName;
	private Long regionId;
	private String districtName;
	private Long districtId;
	private String healthFacilityName;
	private Long healthFacilityId;
	private String pointOfEntryName;
	private Long pointOfEntryId;
	private int year;
	private int epiWeek;
	private Disease disease;
	private long newCases;
	private long labConfirmations;
	private long deaths;
	private String ageGroup;
	private UserReferenceDto reportingUser;
	private Date changeDate;

	public AggregateCaseCountDto() {
	}

	public AggregateCaseCountDto(
		Disease disease,
		long newCases,
		long labConfirmations,
		long deaths,
		int year,
		int epiWeek,
		String ageGroup,
		String reportingUserUuid,
		String reportingUserFirstName,
		String reportingUserLastName,
		Date changeDate) {

		this.disease = disease;
		this.newCases = newCases;
		this.labConfirmations = labConfirmations;
		this.deaths = deaths;
		this.year = year;
		this.epiWeek = epiWeek;
		this.ageGroup = ageGroup;
		this.reportingUser = new UserReferenceDto(reportingUserUuid, reportingUserFirstName, reportingUserLastName);
		this.changeDate = changeDate;
	}

	public AggregateCaseCountDto(
		Disease disease,
		long newCases,
		long labConfirmations,
		long deaths,
		int year,
		Integer epiWeek,
		String ageGroup,
		String regionName,
		Long regionId,
		String districtName,
		Long districtId,
		String healthFacilityName,
		Long healthFacilityId,
		String pointOfEntryName,
		Long pointOfEntryId,
		String reportingUserUuid,
		String reportingUserFirstName,
		String reportingUserLastName,
		Date changeDate) {
		this(
			disease,
			newCases,
			labConfirmations,
			deaths,
			year,
			epiWeek,
			ageGroup,
			reportingUserUuid,
			reportingUserFirstName,
			reportingUserLastName,
			changeDate);
		this.regionName = regionName;
		this.regionId = regionId;
		this.districtName = districtName;
		this.districtId = districtId;
		this.healthFacilityName = healthFacilityName;
		this.healthFacilityId = healthFacilityId;
		this.pointOfEntryName = pointOfEntryName;
		this.pointOfEntryId = pointOfEntryId;
	}

	public boolean similar(AggregateCaseCountDto that, AggregateReportGroupingLevel groupingLevel) {

		if (this == that) {
			return true;
		}

		boolean similar = year == that.year && epiWeek == that.epiWeek && disease == that.disease && Objects.equals(ageGroup, that.ageGroup);

		if (groupingLevel != null) {
			switch (groupingLevel) {
			case REGION:
				similar = similar && Objects.equals(regionId, that.regionId);
				break;
			case DISTRICT:
				similar = similar && Objects.equals(districtId, that.districtId);
				break;
			case HEALTH_FACILITY:
				similar = similar && Objects.equals(healthFacilityId, that.healthFacilityId);
				break;
			case POINT_OF_ENTRY:
				similar = similar && Objects.equals(pointOfEntryId, that.pointOfEntryId);
				break;
			default:
			}
		}

		return similar;
	}

	public boolean hasEqualJurisdiction(AggregateCaseCountDto that) {

		if (this == that) {
			return true;
		} else {
			return Objects.equals(regionId, that.regionId)
				&& Objects.equals(districtId, that.districtId)
				&& Objects.equals(healthFacilityId, that.healthFacilityId)
				&& Objects.equals(pointOfEntryId, that.pointOfEntryId);
		}
	}

	public boolean hasHigherJurisdictionLevel(AggregateCaseCountDto that) {

		if (this == that) {
			return true;
		}

		if (regionId == null && that.regionId != null) {
			return true;
		}
		if (districtId == null && that.districtId != null) {
			return true;
		}
		if (healthFacilityId == null && pointOfEntryId == null && (that.healthFacilityId != null || that.pointOfEntryId != null)) {
			return true;
		}
		return false;
	}

	public boolean hasSameJurisdictionLevel(AggregateCaseCountDto that) {

		if (this == that)
			return true;
		if (that == null || getClass() != that.getClass())
			return false;

		if (regionId == null && that.regionId == null) {
			return true;
		}
		if (districtId == null && that.districtId == null && regionId != null && that.regionId != null) {
			return true;
		}
		if (districtId != null && that.districtId != null && regionId != null && that.regionId != null) {
			if (healthFacilityId != null && that.healthFacilityId != null && pointOfEntryId == null && that.pointOfEntryId == null) {
				return true;
			}
			if (healthFacilityId == null && that.healthFacilityId == null && pointOfEntryId != null && that.pointOfEntryId != null) {
				return true;
			}
			if (healthFacilityId == null && that.healthFacilityId == null && pointOfEntryId == null && that.pointOfEntryId == null) {
				return true;
			}
			if ((healthFacilityId != null || pointOfEntryId != null) && (that.healthFacilityId != null || that.pointOfEntryId != null)) {
				return true;
			}
		}
		return false;
	}

	public JurisdictionLevel getJurisdictionlevel() {

		if (pointOfEntryId != null) {
			return JurisdictionLevel.POINT_OF_ENTRY;
		}
		if (healthFacilityId != null) {
			return JurisdictionLevel.HEALTH_FACILITY;
		}
		if (districtId != null) {
			return JurisdictionLevel.DISTRICT;
		}
		if (regionId != null) {
			return JurisdictionLevel.REGION;
		}
		return JurisdictionLevel.NATION;
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Integer getEpiWeek() {
		return epiWeek;
	}

	public void setEpiWeek(Integer epiWeek) {
		this.epiWeek = epiWeek;
	}

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AggregateCaseCountDto that = (AggregateCaseCountDto) o;
		return year == that.year
			&& epiWeek == that.epiWeek
			&& newCases == that.newCases
			&& labConfirmations == that.labConfirmations
			&& deaths == that.deaths
			&& Objects.equals(regionName, that.regionName)
			&& Objects.equals(regionId, that.regionId)
			&& Objects.equals(districtName, that.districtName)
			&& Objects.equals(districtId, that.districtId)
			&& Objects.equals(healthFacilityName, that.healthFacilityName)
			&& Objects.equals(healthFacilityId, that.healthFacilityId)
			&& Objects.equals(pointOfEntryName, that.pointOfEntryName)
			&& Objects.equals(pointOfEntryId, that.pointOfEntryId)
			&& disease == that.disease
			&& Objects.equals(ageGroup, that.ageGroup);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			regionName,
			regionId,
			districtName,
			districtId,
			healthFacilityName,
			healthFacilityId,
			pointOfEntryName,
			pointOfEntryId,
			year,
			epiWeek,
			disease,
			newCases,
			labConfirmations,
			deaths,
			ageGroup);
	}

}
