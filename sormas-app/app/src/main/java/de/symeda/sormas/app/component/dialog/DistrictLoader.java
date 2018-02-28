package de.symeda.sormas.app.component.dialog;

import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

/**
 * Created by Orson on 18/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class DistrictLoader implements IDistrictLoader {

    List<District> districtList;
    private static DistrictLoader sSoleInstance;

    private DistrictLoader() {
        this.districtList = new ArrayList<>(MemoryDatabaseHelper.DISTRICT.getDistricts(5));

        //TODO: Orson Remove
        for(District d: districtList) {
            d.setRegion(MemoryDatabaseHelper.REGION.getRegions(1).get(0));
        }
    }

    public static DistrictLoader getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new DistrictLoader();
        }

        return sSoleInstance;
    }

    @Override
    public List<District> load(Region region) {
        List<District> child = new ArrayList<>();

        for(District o: districtList) {
            if (o.getRegion().getUuid() == region.getUuid()) {
                child.add(o);
            }
        }

        return child;
    }
}
