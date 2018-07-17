package de.symeda.sormas.app.backend.location;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.CommunityDao;
import de.symeda.sormas.app.backend.region.DistrictDao;
import de.symeda.sormas.app.backend.region.RegionDao;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class LocationDao extends AbstractAdoDao<Location> {

    public LocationDao(Dao<Location,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Location> getAdoClass() {
        return Location.class;
    }

    @Override
    public String getTableName() {
        return Location.TABLE_NAME;
    }

}
