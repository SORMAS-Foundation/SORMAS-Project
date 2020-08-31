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
import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataBurialDtoHelper extends AdoDtoHelper<EpiDataBurial, EpiDataBurialDto> {

	private LocationDtoHelper locationHelper;

	public EpiDataBurialDtoHelper() {
		locationHelper = new LocationDtoHelper();
	}

	@Override
	protected Class<EpiDataBurial> getAdoClass() {
		return EpiDataBurial.class;
	}

	@Override
	protected Class<EpiDataBurialDto> getDtoClass() {
		return EpiDataBurialDto.class;
	}

	@Override
	protected Call<List<EpiDataBurialDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<EpiDataBurialDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<EpiDataBurialDto> epiDataBurialDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(EpiDataBurial target, EpiDataBurialDto source) {

		// epi data is set by calling method

		target.setBurialAddress(locationHelper.fillOrCreateFromDto(target.getBurialAddress(), source.getBurialAddress()));
		target.setBurialDateFrom(source.getBurialDateFrom());
		target.setBurialDateTo(source.getBurialDateTo());
		target.setBurialPersonname(source.getBurialPersonName());
		target.setBurialRelation(source.getBurialRelation());
		target.setBurialIll(source.getBurialIll());
		target.setBurialTouching(source.getBurialTouching());

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	public void fillInnerFromAdo(EpiDataBurialDto a, EpiDataBurial b) {

		if (b.getBurialAddress() != null) {
			Location location = DatabaseHelper.getLocationDao().queryForId(b.getBurialAddress().getId());
			a.setBurialAddress(locationHelper.adoToDto(location));
		} else {
			a.setBurialAddress(null);
		}

		a.setBurialDateFrom(b.getBurialDateFrom());
		a.setBurialDateTo(b.getBurialDateTo());
		a.setBurialPersonName(b.getBurialPersonname());
		a.setBurialRelation(b.getBurialRelation());
		a.setBurialIll(b.getBurialIll());
		a.setBurialTouching(b.getBurialTouching());

		a.setPseudonymized(b.isPseudonymized());
	}
}
