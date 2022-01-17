/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.campaign.form;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class CampaignFormMetaDtoHelper extends AdoDtoHelper<CampaignFormMeta, CampaignFormMetaDto> {

    public static CampaignFormMetaReferenceDto toReferenceDto(CampaignFormMeta ado) {
        if (ado == null) {
            return null;
        }
        CampaignFormMetaReferenceDto dto = new CampaignFormMetaReferenceDto(ado.getUuid());
        return dto;
    }

    @Override
    protected Class<CampaignFormMeta> getAdoClass() {
        return CampaignFormMeta.class;
    }

    @Override
    protected Class<CampaignFormMetaDto> getDtoClass() {
        return CampaignFormMetaDto.class;
    }

    @Override
    protected Call<List<CampaignFormMetaDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid)  throws NoConnectionException {
        return RetroProvider.getCampaignFormMetaFacade().pullAllSince(since);
    }

    @Override
    protected Call<List<CampaignFormMetaDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
        return RetroProvider.getCampaignFormMetaFacade().pullByUuids(uuids);
    }

    @Override
    protected Call<List<PushResult>> pushAll(List<CampaignFormMetaDto> campaignFormMetaDtos) throws NoConnectionException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void fillInnerFromDto(CampaignFormMeta target, CampaignFormMetaDto source) {
        target.setFormId(source.getFormId());
        target.setFormName(source.getFormName());
        target.setCampaignFormElements(source.getCampaignFormElements());
        target.setCampaignFormTranslations(source.getCampaignFormTranslations());
        target.setLanguageCode(source.getLanguageCode());
    }

    @Override
    protected void fillInnerFromAdo(CampaignFormMetaDto dto, CampaignFormMeta campaignFormMeta) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected long getApproximateJsonSizeInBytes() {
        return 0;
    }
}
