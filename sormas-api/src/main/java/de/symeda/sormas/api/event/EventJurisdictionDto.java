package de.symeda.sormas.api.event;

import java.io.Serializable;

public class EventJurisdictionDto implements Serializable {

	private String reportingUserUuid;
	private String surveillanceOfficerUuid;
	private String regionUuid;
	private String districtUuid;
	private String communityUuid;

	public EventJurisdictionDto() {
	}

	public EventJurisdictionDto(
		String reportingUserUuid,
		String surveillanceOfficerUuid,
		String regionUuid,
		String districtUuid,
		String communityUuid) {
		this.reportingUserUuid = reportingUserUuid;
		this.surveillanceOfficerUuid = surveillanceOfficerUuid;
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

	public String getSurveillanceOfficerUuid() {
		return surveillanceOfficerUuid;
	}

	public void setSurveillanceOfficerUuid(String surveillanceOfficerUuid) {
		this.surveillanceOfficerUuid = surveillanceOfficerUuid;
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
