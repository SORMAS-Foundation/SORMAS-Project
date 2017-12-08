package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class RegionDao extends AbstractAdoDao<Region> {

    public RegionDao(Dao<Region,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Region> getAdoClass() {
        return Region.class;
    }

    @Override
    public String getTableName() {
        return Region.TABLE_NAME;
    }

    @Override
    public Region saveAndSnapshot(Region source) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
