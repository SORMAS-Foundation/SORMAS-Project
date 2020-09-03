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

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.user.UserRoleConfigDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class UserRoleConfigDtoHelper extends AdoDtoHelper<UserRoleConfig, UserRoleConfigDto> {

	@Override
	protected Class<UserRoleConfig> getAdoClass() {
		return UserRoleConfig.class;
	}

	@Override
	protected Class<UserRoleConfigDto> getDtoClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Call<List<UserRoleConfigDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getUserRoleConfigFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<UserRoleConfigDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is read-only");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<UserRoleConfigDto> communityDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is read-onl");
	}

	@Override
	public void fillInnerFromDto(UserRoleConfig target, UserRoleConfigDto source) {
		target.setUserRole(source.getUserRole());
		target.setUserRights(source.getUserRights());
	}

	@Override
	public void fillInnerFromAdo(UserRoleConfigDto target, UserRoleConfig source) {
		throw new UnsupportedOperationException("Entity is read-only");
	}
}
