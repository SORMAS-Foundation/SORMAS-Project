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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContextIndexCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface UserFacade {

	UserDto getByUuid(String uuid);

	UserDto saveUser(@Valid UserDto dto, boolean isCurrentUser);

	boolean isLoginUnique(String uuid, String userName);

	String resetPassword(String uuid);

	List<UserDto> getAllAfter(Date date);

	UserDto getByUserName(String userName);

	/**
	 * 
	 * @param regionRef
	 *            reference of the region to be filtered for. When this region is null, it is not filtered in this regard.
	 *            NOTE: some users don't have a region (often users with NATIONAL_USER role, for example). They will
	 *            not be included when a region is specified, but otherwise they will.
	 * @param limitedDisease
	 *            can be used to remove users from the return value that are limited to diseases other that limitedDisease.
	 * @param userRights
	 *            user rights to be filtered for.
	 * @return
	 */
	List<UserReferenceDto> getUsersByRegionAndRights(RegionReferenceDto regionRef, Disease limitedDisease, UserRight... userRights);

	List<UserReferenceDto> getUsersWithSuperiorJurisdiction(UserDto user);

	List<UserDto> getIndexList(UserCriteria userCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	Page<UserDto> getIndexPage(UserCriteria userCriteria, int offset, int size, List<SortProperty> sortProperties);

	long count(UserCriteria userCriteria);

	/**
	 * 
	 * @param district
	 *            reference of the district to be filtered for. When this district is null, it is not filtered in this regard.
	 *            NOTE: some users don't have a district (often users with NATIONAL_USER role, for example). They will
	 *            not be included when a district is specified, but otherwise they will.
	 * @param limitedDisease
	 *            can be used to remove users from the return value that are limited to diseases other that limitedDisease.
	 * @param userRights
	 *            user rights to be filtered for.
	 * @return
	 */
	List<UserReferenceDto> getUserRefsByDistrict(DistrictReferenceDto district, Disease limitedDisease, UserRight... userRights);

	/**
	 * 
	 * @param district
	 *            reference of the district to be filtered for. When this district is null, it is not filtered in this regard.
	 *            NOTE: some users don't have a district (often users with NATIONAL_USER role, for example). They will
	 *            * not be included when a district is specified, but otherwise they will.
	 * @param excludeLimitedDiseaseUsers
	 *            if true, all users limited to diseases are excluded from the return value.
	 * @param userRights
	 *            user rights to be filtered for.
	 * @return
	 */
	List<UserReferenceDto> getUserRefsByDistrict(DistrictReferenceDto district, boolean excludeLimitedDiseaseUsers, UserRight... userRights);

	List<UserReferenceDto> getUserRefsByDistricts(List<DistrictReferenceDto> districts, Disease limitedDisease, UserRight... userRights);

	List<UserReferenceDto> getUserRefsByInfrastructure(
		InfrastructureDataReferenceDto infrastructure,
		JurisdictionLevel jurisdictionLevel,
		JurisdictionLevel allowedJurisdictionLevel,
		Disease limitedDisease,
		UserRight... userRights);

	List<UserReferenceDto> getAllUserRefs(boolean includeInactive);

	List<UserDto> getUsersByAssociatedOfficer(UserReferenceDto associatedOfficer, UserRight... userRights);

	List<String> getAllUuids();

	List<UserDto> getByUuids(List<String> uuids);

	UserDto getCurrentUser();

	UserReferenceDto getCurrentUserAsReference();

	Set<UserRight> getValidLoginRights(String userName, String password);

	void removeUserAsSurveillanceAndContactOfficer(String userUuid);

	UserSyncResult syncUser(String userUuid);

	List<UserDto> getUsersWithDefaultPassword();

	List<ProcessedEntity> enableUsers(List<String> userUuids);

	List<ProcessedEntity> disableUsers(List<String> userUuids);

	List<UserReferenceDto> getUsersHavingCaseInJurisdiction(CaseReferenceDto caseReferenceDto);

	List<UserReferenceDto> getUsersHavingContactInJurisdiction(ContactReferenceDto contactReferenceDto);

	List<UserReferenceDto> getUsersHavingEventInJurisdiction(EventReferenceDto event);

	List<UserReferenceDto> getUsersHavingTravelEntryInJurisdiction(TravelEntryReferenceDto travelEntryReferenceDto);

	List<UserReferenceDto> getUsersHavingEnvironmentInJurisdiction(EnvironmentReferenceDto environmentReferenceDto);

	List<UserReferenceWithTaskNumbersDto> getAssignableUsersWithTaskNumbers(@NotNull TaskContextIndexCriteria taskContextIndexCriteria);

	Set<UserRoleDto> getUserRoles(UserDto user);

	long getUserCountHavingRole(UserRoleReferenceDto userRoleRef);

	List<UserReferenceDto> getUsersHavingOnlyRole(UserRoleReferenceDto userRoleRef);

	/**
	 * Retrieves the user rights of the user specified by the passed UUID, or those of the current user if no UUID is specified.
	 * Requesting the user rights of another user without the rights to view users and user roles results in an AccessDeniedException.
	 * 
	 * @param userUuid
	 *            The UUID of the user to request the user rights for
	 * @return A set containing the user rights associated to all user roles assigned to the user
	 */
	List<UserRight> getUserRights(String userUuid);
}
