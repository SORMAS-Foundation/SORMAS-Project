package de.symeda.sormas.app.backend.location;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class LocationDtoHelper extends AdoDtoHelper<Location, LocationDto> {

    @Override
    public Location create() {
        return new Location();
    }

    @Override
    public LocationDto createDto() {
        return new LocationDto();
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

    @Override
    public void fillInnerFromAdo(LocationDto dto, Location ado) {

        dto.setAddress(ado.getAddress());
        dto.setCity(ado.getCity());
        dto.setDetails(ado.getDetails());
        dto.setLatitude(ado.getLatitude());
        dto.setLongitude(ado.getLongitude());

        if (ado.getCommunity() != null) {
            dto.setCommunity(CommunityDtoHelper.toReferenceDto(DatabaseHelper.getCommunityDao().queryForId(ado.getCommunity().getId())));
        } else {
            dto.setCommunity(null);
        }
        if (ado.getDistrict() != null) {
            dto.setDistrict(DistrictDtoHelper.toReferenceDto(DatabaseHelper.getDistrictDao().queryForId(ado.getDistrict().getId())));
        } else {
            dto.setDistrict(null);
        }
        if (ado.getRegion() != null) {
            dto.setRegion(RegionDtoHelper.toReferenceDto(DatabaseHelper.getRegionDao().queryForId(ado.getRegion().getId())));
        } else {
            dto.setRegion(null);
        }
    }
}
