package de.symeda.sormas.api.user;

import java.io.Serializable;

import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class UserCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 1702083604616047628L;

	private Boolean active;
	private UserRoleReferenceDto userRole;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private String freeText;
	private Boolean showOnlyRestrictedAccessToAssignedEntities;

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

	public Boolean getShowOnlyRestrictedAccessToAssignedEntities() {
		return showOnlyRestrictedAccessToAssignedEntities;
	}

	public void setShowOnlyRestrictedAccessToAssignedEntities(Boolean showOnlyRestrictedAccessToAssignedEntities) {
		this.showOnlyRestrictedAccessToAssignedEntities = showOnlyRestrictedAccessToAssignedEntities;
	}
}
