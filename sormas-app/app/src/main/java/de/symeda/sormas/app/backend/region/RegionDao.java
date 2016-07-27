package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class RegionDao extends AbstractAdoDao<Region> {

    public RegionDao(Dao<Region,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Region.TABLE_NAME;
    }

}
