package de.symeda.sormas.app.backend.facility;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class FacilityDao extends AbstractAdoDao<Facility> {

    public FacilityDao(Dao<Facility,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Facility.TABLE_NAME;
    }

    public List<Facility> getByCommunity(Community community) throws SQLException {
        QueryBuilder<Facility, Long> facilityQb = this.queryBuilder();
        facilityQb.where().eq("community_id", community);
        return facilityQb.query();
    }

    public List<Facility> getByType(FacilityType type) throws SQLException {
        QueryBuilder<Facility, Long> facilityQb = this.queryBuilder();
        facilityQb.where().eq("type", type);
        return facilityQb.query();
    }

}
