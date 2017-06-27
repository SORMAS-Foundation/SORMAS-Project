package de.symeda.sormas.app.backend.region;

import java.util.List;

import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.rest.RetroProvider;
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
    protected Call<List<CommunityDto>> pullAllSince(long since) {
        return RetroProvider.getCommunityFacade().pullAllSince(since);
    }

    @Override
    protected Call<Long> pushAll(List<CommunityDto> communityDtos) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    @Override
    public void fillInnerFromDto(Community ado, CommunityDto dto) {
        ado.setName(dto.getName());
        ado.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(dto.getDistrict().getUuid()));
    }

    @Override
    public void fillInnerFromAdo(CommunityDto communityDto, Community community) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    public static CommunityReferenceDto toReferenceDto(Community ado) {
        if (ado == null) {
            return null;
        }
        CommunityReferenceDto dto = new CommunityReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }
}
