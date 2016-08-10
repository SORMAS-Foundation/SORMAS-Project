package de.symeda.sormas.app.backend.location;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class LocationDtoHelper extends AdoDtoHelper<Location, LocationDto> {

    @Override
    public Location create() {
        return new Location();
    }

    @Override
    public void fillInnerFromDto(Location ado, LocationDto dto) {

        ado.setAddress(dto.getAddress());
        ado.setCity(dto.getCity());
        ado.setDetails(dto.getDetails());
        ado.setLatitude(dto.getLatitude());
        ado.setLongitude(dto.getLongitude());

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
    }
}
