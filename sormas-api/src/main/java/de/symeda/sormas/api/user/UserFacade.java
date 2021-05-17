/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.user;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface UserFacade {

	UserDto getByUuid(String uuid);

	UserDto saveUser(UserDto dto);

	boolean isLoginUnique(String uuid, String userName);

	String resetPassword(String uuid);

	List<UserDto> getAllAfter(Date date);

	UserDto getByUserName(String userName);

	List<UserReferenceDto> getUsersByRegionAndRoles(RegionReferenceDto regionRef, UserRole... assignableRoles);

	List<UserReferenceDto> getUsersByRegionsAndRoles(List<RegionReferenceDto> regionRefs, UserRole... assignableRoles);

	List<UserReferenceDto> getUsersWithSuperiorJurisdiction(UserDto user);

	List<UserDto> getIndexList(UserCriteria userCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	Page<UserDto> getIndexPage(UserCriteria userCriteria, int offset, int size, List<SortProperty> sortProperties);

	long count(UserCriteria userCriteria);

	/**
	 * 
	 * @param district
	 * @param includeSupervisors
	 *            independent from the district
	 * @param userRoles
	 *            roles of the users by district
	 * @return
	 */
	List<UserReferenceDto> getUserRefsByDistrict(DistrictReferenceDto district, boolean includeSupervisors, UserRole... userRoles);

	List<UserReferenceDto> getUserRefsByDistricts(List<DistrictReferenceDto> districts, boolean includeSupervisors, UserRole... userRoles);

	List<UserReferenceDto> getAllUserRefs(boolean includeInactive);

	List<UserDto> getUsersByAssociatedOfficer(UserReferenceDto associatedOfficer, UserRole... userRoles);

	List<String> getAllUuids();

	List<UserDto> getByUuids(List<String> uuids);

	UserDto getCurrentUser();

	UserReferenceDto getCurrentUserAsReference();

	Set<UserRole> getValidLoginRoles(String userName, String password);

	void removeUserAsSurveillanceAndContactOfficer(String userUuid);

	UserSyncResult syncUser(String userUuid);

	List<UserDto> getUsersWithDefaultPassword();

	void enableUsers(List<String> userUuids);

	void disableUsers(List<String> userUuids);
}
