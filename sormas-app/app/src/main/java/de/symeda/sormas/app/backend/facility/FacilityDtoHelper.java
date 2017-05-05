package de.symeda.sormas.app.backend.facility;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class FacilityDtoHelper extends AdoDtoHelper<Facility, FacilityDto> {


    @Override
    public Facility create() {
        return new Facility();
    }

    @Override
    public FacilityDto createDto() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillInnerFromDto(Facility ado, FacilityDto dto) {
        try {
            ado.setName(dto.getName());

            if (dto.getCommunity() != null) {
                ado.setCommunity(DatabaseHelper.getCommunityDao().queryUuid(dto.getCommunity().getUuid()));
            } else {
                ado.setCommunity(null);
            }
            if (dto.getDistrict() != null) {
                ado.setDistrict(DatabaseHelper.getDistrictDao().queryUuid(dto.getDistrict().getUuid()));
            } else {
                ado.setDistrict(null);
            }
            if (dto.getRegion() != null) {
                ado.setRegion(DatabaseHelper.getRegionDao().queryUuid(dto.getRegion().getUuid()));
            } else {
                ado.setRegion(null);
            }

            ado.setCity(dto.getCity());
            ado.setLatitude(dto.getLatitude());
            ado.setLongitude(dto.getLongitude());
            ado.setPublicOwnership(dto.isPublicOwnership());
            ado.setType(dto.getType());
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fillInnerFromAdo(FacilityDto facilityDto, Facility facility) {
        throw new UnsupportedOperationException();
    }

    public static FacilityReferenceDto toReferenceDto(Facility ado) {
        if (ado == null) {
            return null;
        }
        FacilityReferenceDto dto = new FacilityReferenceDto();
        fillReferenceDto(dto, ado);

        return dto;
    }
}
