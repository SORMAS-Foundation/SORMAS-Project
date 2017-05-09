package de.symeda.sormas.app.backend.region;

import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class DistrictDtoHelper extends AdoDtoHelper<District, DistrictDto> {

    @Override
    public District create() {
        return new District();
    }

    @Override
    public DistrictDto createDto() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillInnerFromDto(District ado, DistrictDto dto) {
        ado.setName(dto.getName());
        ado.setRegion(DatabaseHelper.getRegionDao().queryUuid(dto.getRegion().getUuid()));
    }

    @Override
    public void fillInnerFromAdo(DistrictDto districtDto, District district) {
        throw new UnsupportedOperationException();
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
