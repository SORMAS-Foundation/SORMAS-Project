package de.symeda.sormas.app.backend.location;

import java.util.List;

import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class LocationDtoHelper extends AdoDtoHelper<Location, LocationDto> {

    @Override
    protected Class<Location> getAdoClass() {
        return Location.class;
    }

    @Override
    protected Class<LocationDto> getDtoClass() {
        return LocationDto.class;
    }

    @Override
    protected Call<List<LocationDto>> pullAllSince(long since) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    protected Call<List<LocationDto>> pullByUuids(List<String> uuids) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    protected Call<Integer> pushAll(List<LocationDto> locationDtos) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    public void fillInnerFromDto(Location target, LocationDto source) {

        target.setAddress(source.getAddress());
        target.setCity(source.getCity());
        target.setDetails(source.getDetails());
        target.setLatitude(source.getLatitude());
        target.setLongitude(source.getLongitude());
        target.setLatLonAccuracy(source.getLatLonAccuracy());

        target.setRegion(DatabaseHelper.getRegionDao().getByReferenceDto(source.getRegion()));
        target.setDistrict(DatabaseHelper.getDistrictDao().getByReferenceDto(source.getDistrict()));
        target.setCommunity(DatabaseHelper.getCommunityDao().getByReferenceDto(source.getCommunity()));
    }

    @Override
    public void fillInnerFromAdo(LocationDto target, Location source) {

        target.setAddress(source.getAddress());
        target.setCity(source.getCity());
        target.setDetails(source.getDetails());
        target.setLatitude(source.getLatitude());
        target.setLongitude(source.getLongitude());
        target.setLatLonAccuracy(source.getLatLonAccuracy());

        if (source.getCommunity() != null) {
            target.setCommunity(CommunityDtoHelper.toReferenceDto(DatabaseHelper.getCommunityDao().queryForId(source.getCommunity().getId())));
        } else {
            target.setCommunity(null);
        }
        if (source.getDistrict() != null) {
            target.setDistrict(DistrictDtoHelper.toReferenceDto(DatabaseHelper.getDistrictDao().queryForId(source.getDistrict().getId())));
        } else {
            target.setDistrict(null);
        }
        if (source.getRegion() != null) {
            target.setRegion(RegionDtoHelper.toReferenceDto(DatabaseHelper.getRegionDao().queryForId(source.getRegion().getId())));
        } else {
            target.setRegion(null);
        }
    }
}
