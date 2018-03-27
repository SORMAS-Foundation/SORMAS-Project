package de.symeda.sormas.app.component.dialog;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 18/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class DistrictLoader implements IDistrictLoader {

    List<Item> districtList;
    private static DistrictLoader sSoleInstance;

    private DistrictLoader() {
        this.districtList = DataUtils.toItems(DatabaseHelper.getDistrictDao().queryForAll(), false);
        /*this.districtList = new ArrayList<>(MemoryDatabaseHelper.DISTRICT.getDistricts(5));

        //TODO: Orson Remove
        for(District d: districtList) {
            d.setRegion(MemoryDatabaseHelper.REGION.getRegions(1).get(0));
        }*/
    }

    public static DistrictLoader getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new DistrictLoader();
        }

        return sSoleInstance;
    }

    @Override
    public List<Item> load(Region region) {
        //districtList = DataUtils.toItems(DatabaseHelper.getDistrictDao().getByRegion(region));

        if (region == null)
            return new ArrayList<>();

        List<Item> child = new ArrayList<>();

        for (Item o : districtList) {
            if (((District)o.getValue()).getRegion().getUuid().equals(region.getUuid().trim())) {
                child.add(o);
            }
        }

        return child;
    }
}
