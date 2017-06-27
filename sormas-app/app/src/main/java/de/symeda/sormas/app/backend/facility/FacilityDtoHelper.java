package de.symeda.sormas.app.backend.facility;

import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class FacilityDtoHelper extends AdoDtoHelper<Facility, FacilityDto> {

    @Override
    protected Class<Facility> getAdoClass() {
        return Facility.class;
    }

    @Override
    protected Class<FacilityDto> getDtoClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Call<List<FacilityDto>> pullAllSince(long since) {
        return RetroProvider.getFacilityFacade().pullAllSince(since);
    }

    @Override
    protected Call<Long> pushAll(List<FacilityDto> facilityDtos) {
        throw new UnsupportedOperationException("Entity is infrastructure");
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
