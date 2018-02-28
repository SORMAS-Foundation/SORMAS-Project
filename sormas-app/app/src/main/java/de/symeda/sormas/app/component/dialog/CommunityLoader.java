package de.symeda.sormas.app.component.dialog;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.util.MemoryDatabaseHelper;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;

/**
 * Created by Orson on 18/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CommunityLoader implements de.symeda.sormas.app.component.dialog.ICommunityLoader {

    List<Community> communityList;
    private static CommunityLoader sSoleInstance;

    private CommunityLoader() {
        this.communityList = new ArrayList<>(MemoryDatabaseHelper.COMMUNITY.getCommunities(5));

        //TODO: Orson Remove
        for(Community c: communityList) {
            c.setDistrict(MemoryDatabaseHelper.DISTRICT.getDistricts(1).get(0));
        }
    }

    public static CommunityLoader getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new CommunityLoader();
        }

        return sSoleInstance;
    }

    @Override
    public List<Community> load(District district) {
        List<Community> child = new ArrayList<>();

        for (Community o : communityList) {
            if (o.getDistrict().getUuid() == district.getUuid()) {
                child.add(o);
            }
        }

        return child;
    }
}