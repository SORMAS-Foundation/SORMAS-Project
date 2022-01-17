/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.campaign.data;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.app.backend.campaign.CampaignDtoHelper;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMetaDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class CampaignFormDataDtoHelper extends AdoDtoHelper<CampaignFormData, CampaignFormDataDto> {

	@Override
	protected Class<CampaignFormData> getAdoClass() {
		return CampaignFormData.class;
	}

	@Override
	protected Class<CampaignFormDataDto> getDtoClass() {
		return CampaignFormDataDto.class;
	}

	@Override
	protected Call<List<CampaignFormDataDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid)  throws NoConnectionException {
		return RetroProvider.getCampaignFormDataFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<CampaignFormDataDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getCampaignFormDataFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<CampaignFormDataDto> campaignFormMetaDtos) throws NoConnectionException {
		return RetroProvider.getCampaignFormDataFacade().pushAll(campaignFormMetaDtos);
	}

	@Override
	protected void fillInnerFromDto(CampaignFormData target, CampaignFormDataDto source) {
		target.setFormValues(source.getFormValues());
		target.setFormDate(source.getFormDate());
		target.setCampaign(DatabaseHelper.getCampaignDao().getByReferenceDto(source.getCampaign()));
		target.setCampaignFormMeta(DatabaseHelper.getCampaignFormMetaDao().getByReferenceDto(source.getCampaignFormMeta()));
		target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
		target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
		target.setCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getCommunity()));
		target.setCreatingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getCreatingUser()));
	}

	@Override
	protected void fillInnerFromAdo(CampaignFormDataDto target, CampaignFormData source) {
		target.setFormValues(source.getFormValues());
		target.setFormDate(source.getFormDate());
		target.setCampaign(CampaignDtoHelper.toReferenceDto(source.getCampaign()));
		target.setCampaignFormMeta(CampaignFormMetaDtoHelper.toReferenceDto(source.getCampaignFormMeta()));
		target.setRegion(RegionDtoHelper.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictDtoHelper.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityDtoHelper.toReferenceDto(source.getCommunity()));
		target.setCreatingUser(UserDtoHelper.toReferenceDto(source.getCreatingUser()));
	}

    @Override
    protected long getApproximateJsonSizeInBytes() {
        return 0;
    }
}
