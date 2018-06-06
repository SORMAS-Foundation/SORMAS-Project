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

public class CommunityLoader {

    private static CommunityLoader sSoleInstance;

    private CommunityLoader() { }

    public static CommunityLoader getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new CommunityLoader();
        }

        return sSoleInstance;
    }

    public List<Item> load(District district) {
        if (district == null)
            return new ArrayList<>();
        return DataUtils.toItems(DatabaseHelper.getCommunityDao().getByDistrict(district));
    }
}