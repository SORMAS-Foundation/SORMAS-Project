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

public class FacilityLoader {

    private static FacilityLoader sSoleInstance;

    private FacilityLoader() { }

    public static FacilityLoader getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new FacilityLoader();
        }
        return sSoleInstance;
    }

    public List<Item> load(Community community, boolean includeStaticFacilities) {
        if (community == null)
            return new ArrayList<>();
        return DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(community, includeStaticFacilities, true), false);
    }

    public List<Item> load(District district, boolean includeStaticFacilities) {
        if (district == null)
            return new ArrayList<>();
        return DataUtils.toItems(DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict(district, includeStaticFacilities, true), false);
    }
}