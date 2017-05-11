package de.symeda.sormas.app.backend.region;

import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class RegionDtoHelper extends AdoDtoHelper<Region, RegionDto> {

    @Override
    protected Class<Region> getAdoClass() {
        return Region.class;
    }

    @Override
    protected Class<RegionDto> getDtoClass() {
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
