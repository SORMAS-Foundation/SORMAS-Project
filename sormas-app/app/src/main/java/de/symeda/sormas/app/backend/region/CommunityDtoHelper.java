package de.symeda.sormas.app.backend.region;

import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class CommunityDtoHelper extends AdoDtoHelper<Community, CommunityDto> {

    @Override
    public Community create() {
        return new Community();
    }

    @Override
    public void fillInnerFromDto(Community ado, CommunityDto dto) {

        ado.setName(dto.getName());
        ado.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(dto.getDistrict().getUuid()));
    }
}
