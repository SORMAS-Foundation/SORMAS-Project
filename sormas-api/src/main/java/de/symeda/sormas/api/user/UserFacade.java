package de.symeda.sormas.api.user;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

@Remote
public interface UserFacade {

    List<UserDto> getAll(UserRole... roles);
    
    UserDto getByUuid(String uuid);
    
    UserDto saveUser(UserDto dto);

    boolean isLoginUnique(String uuid, String userName);
    
    String resetPassword(String uuid);

	List<UserDto> getAllAfter(Date date);

	UserDto getByUserName(String userName);

	UserReferenceDto getByUserNameAsReference(String userName);

	List<UserReferenceDto> getAllAfterAsReference(Date date);

	List<UserReferenceDto> getUsersByRegionAndRoles(RegionReferenceDto regionRef, UserRole... assignableRoles);
	
	/**
	 * 
	 * @param district
	 * @param includeSupervisors independent from the district
	 * @param assignableRoles roles of the users by district
	 * @return
	 */
	List<UserReferenceDto> getAssignableUsersByDistrict(DistrictReferenceDto district, boolean includeSupervisors, UserRole... assignableRoles);

	int getNumberOfInformantsByFacility(FacilityReferenceDto facilityRef);
	
	List<UserReferenceDto> getForWeeklyReportDetails(DistrictReferenceDto districtRef);

	List<String> getAllUuids(String userUuid);

	List<UserDto> getByUuids(List<String> uuids);
	
}
