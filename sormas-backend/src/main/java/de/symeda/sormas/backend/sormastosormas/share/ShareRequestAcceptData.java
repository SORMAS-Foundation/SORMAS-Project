package de.symeda.sormas.backend.sormastosormas.share;

import java.io.Serializable;

public class ShareRequestAcceptData implements Serializable {
	private String requestUuid;

	private String districtExternalId;

	public ShareRequestAcceptData() {
	}

	public ShareRequestAcceptData(String requestUuid, String districtExternalId) {
		this.requestUuid = requestUuid;
		this.districtExternalId = districtExternalId;
	}

	public String getRequestUuid() {
		return requestUuid;
	}

	public void setRequestUuid(String requestUuid) {
		this.requestUuid = requestUuid;
	}

	public String getDistrictExternalId() {
		return districtExternalId;
	}

	public void setDistrictExternalId(String districtExternalId) {
		this.districtExternalId = districtExternalId;
	}
}
