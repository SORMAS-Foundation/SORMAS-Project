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

package de.symeda.sormas.app.backend.epidata;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataGatheringDtoHelper extends AdoDtoHelper<EpiDataGathering, EpiDataGatheringDto> {

	private LocationDtoHelper locationHelper;

	public EpiDataGatheringDtoHelper() {
		locationHelper = new LocationDtoHelper();
	}

	@Override
	protected Class<EpiDataGathering> getAdoClass() {
		return EpiDataGathering.class;
	}

	@Override
	protected Class<EpiDataGatheringDto> getDtoClass() {
		return EpiDataGatheringDto.class;
	}

	@Override
	protected Call<List<EpiDataGatheringDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<EpiDataGatheringDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<EpiDataGatheringDto> epiDataGatheringDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(EpiDataGathering target, EpiDataGatheringDto source) {

		// epi data is set by calling method

		target.setGatheringAddress(locationHelper.fillOrCreateFromDto(target.getGatheringAddress(), source.getGatheringAddress()));
		target.setDescription(source.getDescription());
		target.setGatheringDate(source.getGatheringDate());

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	public void fillInnerFromAdo(EpiDataGatheringDto a, EpiDataGathering b) {

		Location location = DatabaseHelper.getLocationDao().queryForId(b.getGatheringAddress().getId());
		a.setGatheringAddress(locationHelper.adoToDto(location));

		a.setDescription(b.getDescription());
		a.setGatheringDate(b.getGatheringDate());

		a.setPseudonymized(b.isPseudonymized());
	}
}
