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

package de.symeda.sormas.app.backend.region;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.ServerCommunicationException;
import de.symeda.sormas.app.rest.ServerConnectionException;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class CommunityDtoHelper extends AdoDtoHelper<Community, CommunityDto> {

	@Override
	protected Class<Community> getAdoClass() {
		return Community.class;
	}

	@Override
	protected Class<CommunityDto> getDtoClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Call<List<CommunityDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getCommunityFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<CommunityDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getCommunityFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<CommunityDto> communityDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	// cache of last queried entities
	private District lastDistrict = null;

	@Override
	public void fillInnerFromDto(Community target, CommunityDto source) {
		target.setName(source.getName());

		if (lastDistrict == null || !lastDistrict.getUuid().equals(source.getDistrict().getUuid())) {
			lastDistrict = DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict());
		}
		target.setDistrict(lastDistrict);
		target.setArchived(source.isArchived());
	}

	@Override
	public void pullEntities(final boolean markAsRead)
		throws DaoException, ServerCommunicationException, ServerConnectionException, NoConnectionException {
		databaseWasEmpty = DatabaseHelper.getCommunityDao().countOf() == 0;
		try {
			super.pullEntities(markAsRead);
		} finally {
			databaseWasEmpty = false;
		}
	}

	// performance tweak: only query for existing during pull, when database was not empty
	private boolean databaseWasEmpty = false;

	@Override
	protected Community handlePulledDto(AbstractAdoDao<Community> dao, CommunityDto dto) throws SQLException {
		Community existing = null;
		if (!databaseWasEmpty) {
			existing = dao.queryUuid(dto.getUuid());
		}
		Community existingOrNew = fillOrCreateFromDto(existing, dto);
		dao.updateOrCreate(existingOrNew);
		return existingOrNew;
	}

	@Override
	public void fillInnerFromAdo(CommunityDto communityDto, Community community) {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	public static CommunityReferenceDto toReferenceDto(Community ado) {
		if (ado == null) {
			return null;
		}
		CommunityReferenceDto dto = new CommunityReferenceDto(ado.getUuid());

		return dto;
	}
}
