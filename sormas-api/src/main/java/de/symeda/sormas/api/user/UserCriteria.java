package de.symeda.sormas.api.user;

import java.io.Serializable;

import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class UserCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 1702083604616047628L;

	private Boolean active;
	private UserRole userRole;
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private String freeText;

	public UserCriteria active(Boolean active) {
		this.active = active;
		return this;
	}

	public Boolean getActive() {
		return active;
	}

	public UserCriteria userRole(UserRole userRole) {
		this.userRole = userRole;
		return this;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public UserCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}
	
	public UserCriteria area(AreaReferenceDto area) {
		this.area = area;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}
	
	public AreaReferenceDto getArea() {
		return area;
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
