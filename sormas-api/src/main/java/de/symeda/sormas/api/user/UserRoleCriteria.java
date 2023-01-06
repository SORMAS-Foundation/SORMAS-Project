package de.symeda.sormas.api.user;

import java.io.Serializable;

import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserRoleCriteria extends BaseCriteria implements Serializable {

	@Schema(description = "Whether the user role of interest is enabled")
	private Boolean enabled;
	@Schema(description = "User right associated with the desired user role")
	private UserRight userRight;
	@Schema(description = "Jurisdictional level associated with the desired user role")
	private JurisdictionLevel jurisdictionLevel;

	public UserRoleCriteria enabled(Boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public UserRoleCriteria userRight(UserRight userRight) {
		this.userRight = userRight;
		return this;
	}

	public UserRight getUserRight() {
		return userRight;
	}

	public UserRoleCriteria jurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		this.jurisdictionLevel = jurisdictionLevel;
		return this;
	}

	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	public void setUserRight(UserRight userRight) {
		this.userRight = userRight;
	}
}
