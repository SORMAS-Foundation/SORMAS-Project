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

public class DistrictLoader {

    private static DistrictLoader sSoleInstance;

    private DistrictLoader() { }

    public static DistrictLoader getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new DistrictLoader();
        }

        return sSoleInstance;
    }

    public List<Item> load(Region region) {
        return DataUtils.toItems(DatabaseHelper.getDistrictDao().getByRegion(region));
    }
}
