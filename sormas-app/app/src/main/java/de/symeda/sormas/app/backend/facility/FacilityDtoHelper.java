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

package de.symeda.sormas.app.backend.facility;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.ServerCommunicationException;
import de.symeda.sormas.app.rest.ServerConnectionException;
import retrofit2.Call;

public class FacilityDtoHelper extends AdoDtoHelper<Facility, FacilityDto> {

	@Override
	protected Class<Facility> getAdoClass() {
		return Facility.class;
	}

	@Override
	protected Class<FacilityDto> getDtoClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Call<List<FacilityDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Use pullAllByRegionSince");
	}

	@Override
	protected Call<List<FacilityDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getFacilityFacade().pullByUuids(uuids);
	}

	protected Call<List<FacilityDto>> pullAllByRegionSince(Region region, long since) throws NoConnectionException {
		return RetroProvider.getFacilityFacade().pullAllByRegionSince(region.getUuid(), since);
	}

	protected Call<List<FacilityDto>> pullAllWithoutRegionSince(long since) throws NoConnectionException {
		return RetroProvider.getFacilityFacade().pullAllWithoutRegionSince(since);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<FacilityDto> facilityDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is infrastructure");
	}

	/**
	 * Pulls the data chunkwise per region
	 *
	 * @param markAsRead
	 * @throws DaoException
	 * @throws SQLException
	 * @throws IOException
	 */
	@Override
	public void pullEntities(final boolean markAsRead)
		throws DaoException, ServerCommunicationException, ServerConnectionException, NoConnectionException {
		try {
			final FacilityDao facilityDao = DatabaseHelper.getFacilityDao();

			List<Region> regions = DatabaseHelper.getRegionDao().queryForAll();
			for (Region region : regions) {
				Date maxModifiedDate = facilityDao.getLatestChangeDateByRegion(region);
				long maxModifiedTime = maxModifiedDate != null ? maxModifiedDate.getTime() : 0;
				databaseWasEmpty = maxModifiedDate == null;

				Call<List<FacilityDto>> dtoCall = pullAllByRegionSince(region, maxModifiedTime);
				if (dtoCall == null) {
					return;
				}
				handlePullResponse(markAsRead, facilityDao, dtoCall.execute());
			}

			{
				// Pull 'Other' health facility which has no region set
				Date maxModifiedDate = facilityDao.getLatestChangeDateByRegion(null);
				long maxModifiedTime = maxModifiedDate != null ? maxModifiedDate.getTime() : 0;
				databaseWasEmpty = maxModifiedDate == null;

				Call<List<FacilityDto>> dtoCall = pullAllWithoutRegionSince(maxModifiedTime);
				if (dtoCall == null) {
					return;
				}
				handlePullResponse(markAsRead, facilityDao, dtoCall.execute());
			}

		} catch (IOException e) {
			throw new ServerCommunicationException(e);
		} finally {
			databaseWasEmpty = false;
		}
	}

	// performance tweak: only query for existing during pull, when database was not empty
	private boolean databaseWasEmpty = false;

	@Override
	protected Facility handlePulledDto(AbstractAdoDao<Facility> dao, FacilityDto dto) throws SQLException {
		Facility existing = null;
		if (!databaseWasEmpty) {
			existing = dao.queryUuid(dto.getUuid());
		}
		Facility existingOrNew = fillOrCreateFromDto(existing, dto);
		dao.updateOrCreate(existingOrNew);
		return existingOrNew;
	}

	// cache of last queried entities
	private Community lastCommunity = null;
	private District lastDistrict = null;
	private Region lastRegion = null;

	@Override
	public void fillInnerFromDto(Facility target, FacilityDto source) {

		target.setName(source.getName());

		// keep a cache to improve performance
		if (source.getCommunity() != null) {
			if (lastCommunity == null || !lastCommunity.getUuid().equals(source.getCommunity().getUuid())) {
				lastCommunity = DatabaseHelper.getCommunityDao().getByReferenceDto(source.getCommunity());
			}
		} else {
			lastCommunity = null;
		}
		target.setCommunity(lastCommunity);
		if (source.getDistrict() != null) {
			if (lastDistrict == null || !lastDistrict.getUuid().equals(source.getDistrict().getUuid())) {
				lastDistrict = DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict());
			}
		} else {
			lastDistrict = null;
		}
		target.setDistrict(lastDistrict);
		if (source.getRegion() != null) {
			if (lastRegion == null || !lastRegion.getUuid().equals(source.getRegion().getUuid())) {
				lastRegion = DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion());
			}
		} else {
			lastRegion = null;
		}
		target.setRegion(lastRegion);

		target.setCity(source.getCity());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setPublicOwnership(source.isPublicOwnership());
		target.setType(source.getType());
		target.setArchived(source.isArchived());
	}

	@Override
	public void fillInnerFromAdo(FacilityDto facilityDto, Facility facility) {
		throw new UnsupportedOperationException();
	}

	public static FacilityReferenceDto toReferenceDto(Facility ado) {
		if (ado == null) {
			return null;
		}
		FacilityReferenceDto dto = new FacilityReferenceDto(ado.getUuid());

		return dto;
	}
}
