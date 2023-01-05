package de.symeda.sormas.api.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

public class CommunityUserReportModelDto extends EntityDto {
	
	private static final long serialVersionUID = -8558187171374254398L;

	public static final String I18N_PREFIX = "User";

	public static final String REGION = "region";
	public static final String AREA = "area";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String REP_FORMACCESS = "formAccess";
	public static final String REP_CLUSTERNO = "clusterNumber";
	
	public static final String REP_USERNAME = "username";
	
	public static final String REP_MESSAGE = "message";
	
	private boolean active = true;
	
	private String region;
	private String area;
	private String district;
	private String community;
	private Set<FormAccess> formAccess;
	private String clusterNumber;
	private String username;
	private String message;
	
	
//
//	public String getClusterNumber() {
//		List<String> stre = new ArrayList<>();
//		
//		for(CommunityReferenceDto comSet : getCommunity()) {
//		//	System.out.println("...... "+comSet.getNumber());
//			stre.add(comSet.getNumber() + "");
//		}
//		
//		
//		return stre.toString();
//	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}


	public String getClusterNumber() {
		return clusterNumber;
	}

	public void setClusterNumber(String clusterNumber) {
		this.clusterNumber = clusterNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public CommunityUserReportModelDto toReference() {
		return new CommunityUserReportModelDto();
	}

	public Set<FormAccess> getFormAccess() {
		return formAccess;
	}

	public void setFormAccess(Set<FormAccess> formAccess) {
		this.formAccess = formAccess;
	}
	
	

}
