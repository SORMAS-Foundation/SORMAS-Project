package de.symeda.sormas.app.component.dialog;

import de.symeda.sormas.app.util.MemoryDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;

/**
 * Created by Orson on 18/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class FacilityLoader implements IFacilityLoader {

    List<Facility> facilityList;
    private static FacilityLoader sSoleInstance;

    private FacilityLoader() {
        this.facilityList = new ArrayList<>(MemoryDatabaseHelper.FACILITY.getFacilities(5));

        //TODO: Orson Remove
        for(Facility c: facilityList) {
            c.setRegion(MemoryDatabaseHelper.REGION.getRegions(1).get(0));
            c.setDistrict(MemoryDatabaseHelper.DISTRICT.getDistricts(1).get(0));
            c.setCommunity(MemoryDatabaseHelper.COMMUNITY.getCommunities(1).get(0));
        }
    }

    public static FacilityLoader getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new FacilityLoader();
        }

        return sSoleInstance;
    }

    @Override
    public List<Facility> load(Community community) {
        List<Facility> child = new ArrayList<>();

        for (Facility o : facilityList) {
            if (o.getCommunity().getUuid() == community.getUuid()) {
                child.add(o);
            }
        }

        return child;
    }
}