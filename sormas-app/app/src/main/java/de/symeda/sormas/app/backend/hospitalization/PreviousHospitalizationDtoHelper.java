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

package de.symeda.sormas.app.backend.hospitalization;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class PreviousHospitalizationDtoHelper extends AdoDtoHelper<PreviousHospitalization, PreviousHospitalizationDto> {

	@Override
	protected Class<PreviousHospitalization> getAdoClass() {
		return PreviousHospitalization.class;
	}

	@Override
	protected Class<PreviousHospitalizationDto> getDtoClass() {
		return PreviousHospitalizationDto.class;
	}

	@Override
	protected Call<List<PreviousHospitalizationDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PreviousHospitalizationDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<PreviousHospitalizationDto> previousHospitalizationDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(PreviousHospitalization a, PreviousHospitalizationDto b) {
		// hospitalization is set by calling method
		if (b.getRegion() != null) {
			a.setRegion(DatabaseHelper.getRegionDao().queryUuid(b.getRegion().getUuid()));
		} else {
			a.setRegion(null);
		}

		if (b.getDistrict() != null) {
			a.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(b.getDistrict().getUuid()));
		} else {
			a.setDistrict(null);
		}

		if (b.getCommunity() != null) {
			a.setCommunity(DatabaseHelper.getCommunityDao().queryUuid(b.getCommunity().getUuid()));
		} else {
			a.setCommunity(null);
		}

		if (b.getHealthFacility() != null) {
			a.setHealthFacility(DatabaseHelper.getFacilityDao().queryUuid(b.getHealthFacility().getUuid()));
		} else {
			a.setHealthFacility(null);
		}

		a.setHealthFacilityDetails(b.getHealthFacilityDetails());
		a.setIsolated(b.getIsolated());
		a.setAdmissionDate(b.getAdmissionDate());
		a.setDischargeDate(b.getDischargeDate());
		a.setDescription(b.getDescription());
	}

	@Override
	public void fillInnerFromAdo(PreviousHospitalizationDto a, PreviousHospitalization b) {

		if (b.getRegion() != null) {
			Region region = DatabaseHelper.getRegionDao().queryForId(b.getRegion().getId());
			a.setRegion(RegionDtoHelper.toReferenceDto(region));
		} else {
			a.setRegion(null);
		}

		if (b.getDistrict() != null) {
			District district = DatabaseHelper.getDistrictDao().queryForId(b.getDistrict().getId());
			a.setDistrict(DistrictDtoHelper.toReferenceDto(district));
		} else {
			a.setDistrict(null);
		}

		if (b.getCommunity() != null) {
			Community community = DatabaseHelper.getCommunityDao().queryForId(b.getCommunity().getId());
			a.setCommunity(CommunityDtoHelper.toReferenceDto(community));
		} else {
			a.setCommunity(null);
		}

		if (b.getHealthFacility() != null) {
			Facility facility = DatabaseHelper.getFacilityDao().queryForId(b.getHealthFacility().getId());
			a.setHealthFacility(FacilityDtoHelper.toReferenceDto(facility));
		} else {
			a.setHealthFacility(null);
		}

		a.setHealthFacilityDetails(b.getHealthFacilityDetails());
		a.setIsolated(b.getIsolated());
		a.setAdmissionDate(b.getAdmissionDate());
		a.setDischargeDate(b.getDischargeDate());
		a.setDescription(b.getDescription());
	}
}
