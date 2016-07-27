package de.symeda.sormas.app.backend.region;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class RegionDtoHelper extends AdoDtoHelper<Region, RegionDto> {

    @Override
    public Region create() {
        return new Region();
    }

    @Override
    public void fillInnerFromDto(Region ado, RegionDto dto) {

        ado.setName(dto.getName());
    }
}
