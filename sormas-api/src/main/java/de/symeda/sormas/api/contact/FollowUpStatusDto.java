package de.symeda.sormas.api.contact;

import java.io.Serializable;

public class FollowUpStatusDto implements Serializable {

	private static final long serialVersionUID = 6075542609471404489L;

	private String uuid;
	private FollowUpStatus followUpStatus;

	public FollowUpStatusDto(String uuid, FollowUpStatus followUpStatus) {

		this.uuid = uuid;
		this.followUpStatus = followUpStatus;

	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}
}
