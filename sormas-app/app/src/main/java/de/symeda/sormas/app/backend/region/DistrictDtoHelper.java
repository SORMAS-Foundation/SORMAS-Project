package de.symeda.sormas.app.backend.region;

import java.util.List;

import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class DistrictDtoHelper extends AdoDtoHelper<District, DistrictDto> {

    @Override
    protected Class<District> getAdoClass() {
        return District.class;
    }

    @Override
    protected Class<DistrictDto> getDtoClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Call<List<DistrictDto>> pullAllSince(long since) {
        return RetroProvider.getDistrictFacade().pullAllSince(since);
    }

    @Override
    protected Call<Integer> pushAll(List<DistrictDto> districtDtos) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    @Override
    public void fillInnerFromDto(District ado, DistrictDto dto) {
        ado.setName(dto.getName());
        ado.setEpidCode(dto.getEpidCode());
        ado.setRegion(DatabaseHelper.getRegionDao().queryUuid(dto.getRegion().getUuid()));
    }

    @Override
    public void fillInnerFromAdo(DistrictDto districtDto, District district) {
        throw new UnsupportedOperationException("Entity is infrastructure");
    }

    public static DistrictReferenceDto toReferenceDto(District ado) {
        if (ado == null) {
            return null;
        }
        DistrictReferenceDto dto = new DistrictReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }
}
