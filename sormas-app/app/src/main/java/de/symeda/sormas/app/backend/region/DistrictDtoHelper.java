package de.symeda.sormas.app.backend.region;

import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
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
    public void fillInnerFromDto(District ado, DistrictDto dto) {

        ado.setName(dto.getName());
        ado.setRegion(DatabaseHelper.getRegionDao().queryUuid(dto.getRegion().getUuid()));
    }
}
