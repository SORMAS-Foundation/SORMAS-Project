package de.symeda.sormas.app.component.dialog;

import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.region.Region;

/**
 * Created by Orson on 18/02/2018.
 *
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class RegionLoader implements IRegionLoader {

    List<Region> regionList;
    private static RegionLoader sSoleInstance;

    private RegionLoader() {
        this.regionList = new ArrayList<>(MemoryDatabaseHelper.REGION.getRegions(5));
    }

    public static RegionLoader getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new RegionLoader();
        }

        return sSoleInstance;
    }

    @Override
    public List<Region> load() {
        return regionList;
    }
}
