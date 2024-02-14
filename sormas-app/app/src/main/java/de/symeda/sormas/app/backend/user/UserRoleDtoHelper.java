/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.user;

import java.util.List;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class UserRoleDtoHelper extends AdoDtoHelper<UserRole, UserRoleDto> {

	@Override
	protected Class<UserRole> getAdoClass() {
		return UserRole.class;
	}

	@Override
	protected Class<UserRoleDto> getDtoClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Call<List<UserRoleDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid) throws NoConnectionException {
		return RetroProvider.getUserRoleFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<UserRoleDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getUserRoleFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PostResponse>> pushAll(List<UserRoleDto> userRoleDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Can't change userroles in app");
	}

	@Override
	public void fillInnerFromDto(UserRole target, UserRoleDto source) {
		target.setUserRights(source.getUserRights());
		target.setCaption(source.getCaption());
		target.setDescription(source.getDescription());
		target.setEnabled(source.isEnabled());
		target.setPortHealthUser(source.isPortHealthUser());
		target.setHasAssociatedDistrictUser(source.getHasAssociatedDistrictUser());
		target.setHasOptionalHealthFacility(source.getHasOptionalHealthFacility());
		target.setJurisdictionLevel(source.getJurisdictionLevel());
		target.setLinkedDefaultUserRole(source.getLinkedDefaultUserRole());
		target.setRestrictAccessToAssignedEntities(source.isRestrictAccessToAssignedEntities());
	}

	@Override
	public void fillInnerFromAdo(UserRoleDto target, UserRole source) {
		target.setUserRights(source.getUserRights());
		target.setCaption(source.getCaption());
		target.setDescription(source.getDescription());
		target.setEnabled(source.isEnabled());
		target.setPortHealthUser(source.isPortHealthUser());
		target.setHasAssociatedDistrictUser(source.hasAssociatedDistrictUser());
		target.setHasOptionalHealthFacility(source.hasOptionalHealthFacility());
		target.setJurisdictionLevel(source.getJurisdictionLevel());
		target.setLinkedDefaultUserRole(source.getLinkedDefaultUserRole());
		target.setRestrictAccessToAssignedEntities(source.isRestrictAccessToAssignedEntities());
	}

	@Override
	protected long getApproximateJsonSizeInBytes() {
		return 0;
	}

	@Override
	protected void executeHandlePulledListAddition(int listSize) {
		if (listSize > 0) {
			// Clear the user rights cache if user roles have changed because user rights
			// of the current user might have been updated
			ConfigProvider.clearUserRights();
		}
	}
}
