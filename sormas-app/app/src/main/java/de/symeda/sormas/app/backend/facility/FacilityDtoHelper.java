package de.symeda.sormas.app.backend.facility;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

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
    public void fillInnerFromDto(Facility target, FacilityDto source) {

        target.setName(source.getName());

        target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
        target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
        target.setCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getCommunity()));

        target.setCity(source.getCity());
        target.setLatitude(source.getLatitude());
        target.setLongitude(source.getLongitude());
        target.setPublicOwnership(source.isPublicOwnership());
        target.setType(source.getType());
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
