package de.symeda.sormas.api.user;

import java.io.Serializable;

import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 1702083604616047628L;

	@Schema(description = "Wether the user account is enabled")
	private Boolean active;
	@Schema(description = "Corresponding user role")
	private UserRoleReferenceDto userRole;
	@Schema(description = "User's region w.r.t. to hierarchical infrastructure")
	private RegionReferenceDto region;
	@Schema(description = "User's district w.r.t. to hierarchical infrastructure")
	private DistrictReferenceDto district;
	@Schema(description = "Free text search filter for user name")
	private String freeText;

	public UserCriteria active(Boolean active) {
		this.active = active;
		return this;
	}

	public Boolean getActive() {
		return active;
	}

	public UserCriteria userRole(UserRoleReferenceDto userRole) {
		this.userRole = userRole;
		return this;
	}

	public UserRoleReferenceDto getUserRole() {
		return userRole;
	}

	public UserCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public UserCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public UserCriteria freeText(String freeText) {
		this.freeText = freeText;
		return this;
	}

	@IgnoreForUrl
	public String getFreeText() {
		return freeText;
	}
}
