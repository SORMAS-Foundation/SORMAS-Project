package de.symeda.sormas.app.backend.region;

import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class CommunityDtoHelper extends AdoDtoHelper<Community, CommunityDto> {

    @Override
    public Community create() {
        return new Community();
    }

    @Override
    public CommunityDto createDto() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillInnerFromDto(Community ado, CommunityDto dto) {
        try {
            ado.setName(dto.getName());
            ado.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(dto.getDistrict().getUuid()));
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fillInnerFromAdo(CommunityDto communityDto, Community community) {
        throw new UnsupportedOperationException();
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
