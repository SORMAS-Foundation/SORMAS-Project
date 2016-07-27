package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class DistrictDao extends AbstractAdoDao<District> {

    public DistrictDao(Dao<District,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return District.TABLE_NAME;
    }

}
