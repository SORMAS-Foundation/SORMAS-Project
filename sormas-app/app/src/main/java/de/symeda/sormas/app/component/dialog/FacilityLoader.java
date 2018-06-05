package de.symeda.sormas.app.component.dialog;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 18/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class FacilityLoader implements IFacilityLoader {

    List<Item> facilityList;
    private static FacilityLoader sSoleInstance;

    private FacilityLoader() {
        this.facilityList = new ArrayList<>();
        /*this.facilityList = new ArrayList<>(MemoryDatabaseHelper.FACILITY.getFacilities(5));

        //TODO: Orson Remove
        for(Facility c: facilityList) {
            c.setRegion(MemoryDatabaseHelper.REGION.getRegions(1).get(0));
            c.setDistrict(MemoryDatabaseHelper.DISTRICT.getDistricts(1).get(0));
            c.setCommunity(MemoryDatabaseHelper.COMMUNITY.getCommunities(1).get(0));
        }*/
    }

    public static FacilityLoader getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new FacilityLoader();
        }

        return sSoleInstance;
    }

    @Override
    public List<Item> load(Community community, boolean includeStaticFacilities) {
        if (community == null)
            return new ArrayList<>();

        this.facilityList = DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(community, includeStaticFacilities), false);
        return this.facilityList;
    }

    @Override
    public List<Item> load(District district, boolean includeStaticFacilities) {
        if (district == null)
            return new ArrayList<>();

        this.facilityList = DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict(district, includeStaticFacilities), false);
        return this.facilityList;
    }
}