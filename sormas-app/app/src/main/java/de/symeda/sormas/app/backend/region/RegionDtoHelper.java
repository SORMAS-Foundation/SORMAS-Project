package de.symeda.sormas.app.backend.region;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
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
    public RegionDto createDto() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillInnerFromDto(Region ado, RegionDto dto) {

        ado.setName(dto.getName());
    }

    @Override
    public void fillInnerFromAdo(RegionDto regionDto, Region region) {
        throw new UnsupportedOperationException();
    }

    public static RegionReferenceDto toReferenceDto(Region ado) {
        if (ado == null) {
            return null;
        }
        RegionReferenceDto dto = new RegionReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }
}
