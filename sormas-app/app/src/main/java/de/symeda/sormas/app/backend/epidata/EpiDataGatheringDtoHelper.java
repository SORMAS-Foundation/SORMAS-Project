package de.symeda.sormas.app.backend.epidata;

import java.util.List;

import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import retrofit2.Call;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataGatheringDtoHelper extends AdoDtoHelper<EpiDataGathering, EpiDataGatheringDto> {

    private LocationDtoHelper locationHelper;

    public EpiDataGatheringDtoHelper() {
        locationHelper = new LocationDtoHelper();
    }

    @Override
    protected Class<EpiDataGathering> getAdoClass() {
        return EpiDataGathering.class;
    }

    @Override
    protected Class<EpiDataGatheringDto> getDtoClass() {
        return EpiDataGatheringDto.class;
    }

    @Override
    protected Call<List<EpiDataGatheringDto>> pullAllSince(long since) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    protected Call<List<EpiDataGatheringDto>> pullByUuids(List<String> uuids) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    protected Call<Integer> pushAll(List<EpiDataGatheringDto> epiDataGatheringDtos) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    public void fillInnerFromDto(EpiDataGathering target, EpiDataGatheringDto source) {

        // epi data is set by calling method

        target.setGatheringAddress(locationHelper.fillOrCreateFromDto(target.getGatheringAddress(), source.getGatheringAddress()));
        target.setDescription(source.getDescription());
        target.setGatheringDate(source.getGatheringDate());
    }

    @Override
    public void fillInnerFromAdo(EpiDataGatheringDto a, EpiDataGathering b) {

        Location location = DatabaseHelper.getLocationDao().queryForId(b.getGatheringAddress().getId());
        a.setGatheringAddress(locationHelper.adoToDto(location));

        a.setDescription(b.getDescription());
        a.setGatheringDate(b.getGatheringDate());
    }
}
