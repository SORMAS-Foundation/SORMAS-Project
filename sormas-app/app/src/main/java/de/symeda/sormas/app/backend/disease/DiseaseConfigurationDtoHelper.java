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

package de.symeda.sormas.app.backend.disease;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class DiseaseConfigurationDtoHelper extends AdoDtoHelper<DiseaseConfiguration, DiseaseConfigurationDto> {

	@Override
	protected Class<DiseaseConfiguration> getAdoClass() {
		return DiseaseConfiguration.class;
	}

	@Override
	protected Class<DiseaseConfigurationDto> getDtoClass() {
		return DiseaseConfigurationDto.class;
	}

	@Override
	protected Call<List<DiseaseConfigurationDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getDiseaseConfigurationFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<DiseaseConfigurationDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getDiseaseConfigurationFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<DiseaseConfigurationDto> dtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is read-only");
	}

	@Override
	public void fillInnerFromDto(DiseaseConfiguration target, DiseaseConfigurationDto source) {
		target.setDisease(source.getDisease());
		target.setActive(source.getActive());
		target.setPrimaryDisease(source.getPrimaryDisease());
		target.setCaseBased(source.getCaseBased());
		target.setFollowUpEnabled(source.getFollowUpEnabled());
		target.setFollowUpDuration(source.getFollowUpDuration());
	}

	@Override
	public void fillInnerFromAdo(DiseaseConfigurationDto target, DiseaseConfiguration source) {
		target.setDisease(source.getDisease());
		target.setActive(source.getActive());
		target.setPrimaryDisease(source.getPrimaryDisease());
		target.setCaseBased(source.getCaseBased());
		target.setFollowUpEnabled(source.getFollowUpEnabled());
		target.setFollowUpDuration(source.getFollowUpDuration());
	}
}
