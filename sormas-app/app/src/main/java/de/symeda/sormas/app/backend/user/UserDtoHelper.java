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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class UserDtoHelper extends AdoDtoHelper<User, UserDto> {

	private LocationDtoHelper locationHelper = new LocationDtoHelper();

	@Override
	protected Class<User> getAdoClass() {
		return User.class;
	}

	@Override
	protected Class<UserDto> getDtoClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Call<List<UserDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getUserFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<UserDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getUserFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<UserDto> userDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Can't change users in app");
	}

	protected void preparePulledResult(List<UserDto> result) {
		Collections.sort(result, new Comparator<UserDto>() {

			@Override
			public int compare(UserDto lhs, UserDto rhs) {
				if (lhs.getAssociatedOfficer() == null && rhs.getAssociatedOfficer() != null) {
					return -1;
				} else if (lhs.getAssociatedOfficer() != null && rhs.getAssociatedOfficer() == null) {
					return 1;
				}
				return 0;
			}
		});
	}

	@Override
	protected void fillInnerFromDto(User target, UserDto source) {
		target.setActive(source.isActive());
		target.setUserName(source.getUserName());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setUserEmail(source.getUserEmail());

		if (source.getUserRoles().size() > 0) {
			target.setUserRoles(source.getUserRoles());
		}

		target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
		target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
		target.setCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(DatabaseHelper.getFacilityDao().getByReferenceDto(source.getHealthFacility()));
		target.setPointOfEntry(DatabaseHelper.getPointOfEntryDao().getByReferenceDto(source.getPointOfEntry()));

		target.setAssociatedOfficer(DatabaseHelper.getUserDao().getByReferenceDto(source.getAssociatedOfficer()));
		target.setLimitedDisease(source.getLimitedDisease());

		target.setAddress(locationHelper.fillOrCreateFromDto(target.getAddress(), source.getAddress()));
		target.setPhone(source.getPhone());
		target.setLanguage(source.getLanguage());
	}

	@Override
	protected void fillInnerFromAdo(UserDto userDto, User user) {
		// TODO
		throw new UnsupportedOperationException("Can't change users in app");
	}

	public static UserReferenceDto toReferenceDto(User ado) {
		if (ado == null) {
			return null;
		}
		UserReferenceDto dto = new UserReferenceDto(ado.getUuid());
		return dto;
	}
}
