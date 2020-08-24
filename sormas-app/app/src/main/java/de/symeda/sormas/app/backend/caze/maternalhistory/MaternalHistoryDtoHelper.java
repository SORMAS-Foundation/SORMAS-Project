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

package de.symeda.sormas.app.backend.caze.maternalhistory;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class MaternalHistoryDtoHelper extends AdoDtoHelper<MaternalHistory, MaternalHistoryDto> {

	@Override
	protected Class<MaternalHistory> getAdoClass() {
		return MaternalHistory.class;
	}

	@Override
	protected Class<MaternalHistoryDto> getDtoClass() {
		return MaternalHistoryDto.class;
	}

	@Override
	protected Call<List<MaternalHistoryDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<MaternalHistoryDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<MaternalHistoryDto> dtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(MaternalHistory target, MaternalHistoryDto source) {
		target.setAgeAtBirth(source.getAgeAtBirth());
		target.setArthralgiaArthritis(source.getArthralgiaArthritis());
		target.setArthralgiaArthritisOnset(source.getArthralgiaArthritisOnset());
		target.setArthralgiaArthritisMonth(source.getArthralgiaArthritisMonth());
		target.setChildrenNumber(source.getChildrenNumber());
		target.setConjunctivitis(source.getConjunctivitis());
		target.setConjunctivitisOnset(source.getConjunctivitisOnset());
		target.setConjunctivitisMonth(source.getConjunctivitisMonth());
		target.setMaculopapularRash(source.getMaculopapularRash());
		target.setMaculopapularRashOnset(source.getMaculopapularRashOnset());
		target.setMaculopapularRashMonth(source.getMaculopapularRashMonth());
		target.setRubella(source.getRubella());
		target.setRubellaOnset(source.getRubellaOnset());
		target.setSwollenLymphs(source.getSwollenLymphs());
		target.setSwollenLymphsOnset(source.getSwollenLymphsOnset());
		target.setSwollenLymphsMonth(source.getSwollenLymphsMonth());
		target.setRashExposure(source.getRashExposure());
		target.setRashExposureDate(source.getRashExposureDate());
		target.setRashExposureMonth(source.getRashExposureMonth());
		target.setRashExposureRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRashExposureRegion()));
		target.setRashExposureDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getRashExposureDistrict()));
		target.setRashExposureCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getRashExposureCommunity()));
		target.setOtherComplications(source.getOtherComplications());
		target.setOtherComplicationsOnset(source.getOtherComplicationsOnset());
		target.setOtherComplicationsMonth(source.getOtherComplicationsMonth());
		target.setOtherComplicationsDetails(source.getOtherComplicationsDetails());

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	public void fillInnerFromAdo(MaternalHistoryDto target, MaternalHistory source) {
		target.setAgeAtBirth(source.getAgeAtBirth());
		target.setArthralgiaArthritis(source.getArthralgiaArthritis());
		target.setArthralgiaArthritisOnset(source.getArthralgiaArthritisOnset());
		target.setArthralgiaArthritisMonth(source.getArthralgiaArthritisMonth());
		target.setChildrenNumber(source.getChildrenNumber());
		target.setConjunctivitis(source.getConjunctivitis());
		target.setConjunctivitisOnset(source.getConjunctivitisOnset());
		target.setConjunctivitisMonth(source.getConjunctivitisMonth());
		target.setMaculopapularRash(source.getMaculopapularRash());
		target.setMaculopapularRashOnset(source.getMaculopapularRashOnset());
		target.setMaculopapularRashMonth(source.getMaculopapularRashMonth());
		target.setRubella(source.getRubella());
		target.setRubellaOnset(source.getRubellaOnset());
		target.setSwollenLymphs(source.getSwollenLymphs());
		target.setSwollenLymphsOnset(source.getSwollenLymphsOnset());
		target.setSwollenLymphsMonth(source.getSwollenLymphsMonth());
		target.setRashExposure(source.getRashExposure());
		target.setRashExposureDate(source.getRashExposureDate());
		target.setRashExposureMonth(source.getRashExposureMonth());

		if (source.getRashExposureRegion() != null) {
			target.setRashExposureRegion(
				RegionDtoHelper.toReferenceDto(DatabaseHelper.getRegionDao().queryForId(source.getRashExposureRegion().getId())));
		} else {
			target.setRashExposureRegion(null);
		}
		if (source.getRashExposureDistrict() != null) {
			target.setRashExposureDistrict(
				DistrictDtoHelper.toReferenceDto(DatabaseHelper.getDistrictDao().queryForId(source.getRashExposureDistrict().getId())));
		} else {
			target.setRashExposureDistrict(null);
		}
		if (source.getRashExposureCommunity() != null) {
			target.setRashExposureCommunity(
				CommunityDtoHelper.toReferenceDto(DatabaseHelper.getCommunityDao().queryForId(source.getRashExposureCommunity().getId())));
		} else {
			target.setRashExposureCommunity(null);
		}

		target.setOtherComplications(source.getOtherComplications());
		target.setOtherComplicationsOnset(source.getOtherComplicationsOnset());
		target.setOtherComplicationsMonth(source.getOtherComplicationsMonth());
		target.setOtherComplicationsDetails(source.getOtherComplicationsDetails());

		target.setPseudonymized(source.isPseudonymized());
	}
}
