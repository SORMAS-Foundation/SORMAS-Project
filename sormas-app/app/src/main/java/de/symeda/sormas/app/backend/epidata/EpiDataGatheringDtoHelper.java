package de.symeda.sormas.app.backend.epidata;

import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

public class EpiDataGatheringDtoHelper extends AdoDtoHelper<EpiDataGathering, EpiDataGatheringDto> {

    private LocationDtoHelper locationHelper;
    private EpiDataDtoHelper epiDataHelper;

    public EpiDataGatheringDtoHelper(EpiDataDtoHelper epiDataHelper) {
        locationHelper = new LocationDtoHelper();
        this.epiDataHelper = epiDataHelper;
    }

    @Override
    public EpiDataGathering create() {
        return new EpiDataGathering();
    }

    @Override
    public EpiDataGatheringDto createDto() {
        return new EpiDataGatheringDto();
    }

    @Override
    public void fillInnerFromDto(EpiDataGathering a, EpiDataGatheringDto b) {
        // epi data is set by calling method

        if (b.getGatheringAddress() != null) {
            a.setGatheringAddress(DatabaseHelper.getLocationDao().queryUuid(b.getGatheringAddress().getUuid()));
        } else {
            a.setGatheringAddress(locationHelper.create());
        }

        a.setDescription(b.getDescription());
        a.setGatheringDate(b.getGatheringDate());
    }

    @Override
    public void fillInnerFromAdo(EpiDataGatheringDto a, EpiDataGathering b) {

        if (b.getGatheringAddress() != null) {
            Location location = DatabaseHelper.getLocationDao().queryForId(b.getGatheringAddress().getId());
            a.setGatheringAddress(locationHelper.adoToDto(location));
        } else {
            a.setGatheringAddress(locationHelper.createDto());
        }

        a.setDescription(b.getDescription());
        a.setGatheringDate(b.getGatheringDate());
    }
}
