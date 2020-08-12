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
import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataTravelDtoHelper extends AdoDtoHelper<EpiDataTravel, EpiDataTravelDto> {

	public EpiDataTravelDtoHelper() {
	}

	@Override
	protected Class<EpiDataTravel> getAdoClass() {
		return EpiDataTravel.class;
	}

	@Override
	protected Class<EpiDataTravelDto> getDtoClass() {
		return EpiDataTravelDto.class;
	}

	@Override
	protected Call<List<EpiDataTravelDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<EpiDataTravelDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<EpiDataTravelDto> epiDataTravelDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(EpiDataTravel target, EpiDataTravelDto source) {

		// epi data is set by calling method

		target.setTravelType(source.getTravelType());
		target.setTravelDestination(source.getTravelDestination());
		target.setTravelDateFrom(source.getTravelDateFrom());
		target.setTravelDateTo(source.getTravelDateTo());

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	public void fillInnerFromAdo(EpiDataTravelDto a, EpiDataTravel b) {

		a.setTravelType(b.getTravelType());
		a.setTravelDestination(b.getTravelDestination());
		a.setTravelDateFrom(b.getTravelDateFrom());
		a.setTravelDateTo(b.getTravelDateTo());

		a.setPseudonymized(b.isPseudonymized());
	}
}
