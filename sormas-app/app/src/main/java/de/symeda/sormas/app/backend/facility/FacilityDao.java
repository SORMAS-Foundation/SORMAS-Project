package de.symeda.sormas.app.backend.facility;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.region.Community;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class FacilityDao extends AbstractAdoDao<Facility> {

    public FacilityDao(Dao<Facility,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Facility> getAdoClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTableName() {
        return Facility.TABLE_NAME;
    }

    public List<Facility> getHealthFacilitiesByCommunity(Community community, boolean includeOthers) {
        List<Facility> facilities = queryForEq(Facility.COMMUNITY + "_id", community, Facility.NAME, true);
        for (Facility facility : facilities) {
            if (facility.getType() == FacilityType.LABORATORY) {
                facilities.remove(facility);
            }
        }

        if (includeOthers) {
            facilities.add(0, queryUuid(FacilityDto.OTHER_FACILITY_UUID));
        }

        return facilities;
    }

    public List<Facility> getLaboratories() {
        List<Facility> facilities = queryForEq(Facility.TYPE, FacilityType.LABORATORY, Facility.NAME, true);
        return facilities;
    }
}
