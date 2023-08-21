package de.symeda.sormas.app.backend.environment;

import java.io.Serializable;

public class EnvironmentJurisdictionDto implements Serializable {

	private String reportingUserUuid;
	private String responsibleUserUuid;
	private String regionUuid;
	private String districtUuid;
	private String communityUuid;

	public EnvironmentJurisdictionDto() {
	}

	public EnvironmentJurisdictionDto(
		String reportingUserUuid,
		String responsibleUserUuid,
		String regionUuid,
		String districtUuid,
		String communityUuid) {

		this.reportingUserUuid = reportingUserUuid;
		this.responsibleUserUuid = responsibleUserUuid;
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.communityUuid = communityUuid;
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public void setReportingUserUuid(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
	}

	public String getResponsibleUserUuid() {
		return responsibleUserUuid;
	}

	public void setResponsibleUserUuid(String responsibleUserUuid) {
		this.responsibleUserUuid = responsibleUserUuid;
	}

	public String getRegionUuid() {
		return regionUuid;
	}

	public void setRegionUuid(String regionUuid) {
		this.regionUuid = regionUuid;
	}

	public String getDistrictUuid() {
		return districtUuid;
	}

	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}

	public String getCommunityUuid() {
		return communityUuid;
	}

	public void setCommunityUuid(String communityUuid) {
		this.communityUuid = communityUuid;
	}
}
